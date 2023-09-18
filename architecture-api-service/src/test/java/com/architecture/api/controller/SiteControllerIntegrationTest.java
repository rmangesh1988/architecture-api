package com.architecture.api.controller;

import com.architecture.api.ArchitectureApiApplication;
import com.architecture.api.model.Site;
import com.architecture.api.repository.SiteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.architecture.api.TestHelper.buildEntityFromFile;
import static com.architecture.api.exception.Errors.CONCURRENT_DATA_MODIFICATION_EXCEPTION;
import static com.architecture.api.exception.Errors.INCORRECT_HEIGHT_PLATEAUS_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ArchitectureApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
class SiteControllerIntegrationTest {

    static final String INGEST_URI = "/api/v1/ingest-site-data/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SiteRepository siteRepository;

    @AfterEach
    public void cleanUp() {
        siteRepository.deleteAll();
    }

    @DisplayName("Test to check if the building limits and height plateaus are ingested correctly, generating proper building splits limits. Then saved to the DB")
    @Test
    void testIngestSiteDataWhenCreatingNewSiteSuccess() throws Exception {

        var content = Files.readString(Path.of("src/test/resources/data/input/valid_input.json"));

        mockMvc.perform(post(INGEST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.buildingLimits").exists())
                .andExpect(jsonPath("$.buildingLimits[*]", hasSize(1)))
                .andExpect(jsonPath("$.buildingLimits[0].splitBuildingLimits[*]", hasSize(2)));

        var sites = siteRepository.findAll();

        assertThat(sites).hasSize(1);
        var siteActual = sites.get(0);
        assertNotNull(siteActual.getId());
        assertEquals(0, siteActual.getVersion());

        var siteExpected = buildEntityFromFile("src/test/resources/data/output/valid_site_result.json", Site.class);
        assertSiteComponentsEquals(siteExpected, siteActual);
    }

    @DisplayName("Test to check if the building limits and height plateaus are ingested correctly, generating proper building splits limits. And existing site is updated.")
    @Test
    void testIngestSiteDataWhenUpdatingExistingSiteSuccess() throws Exception {

        //Dummy existing site
        var existingSite = siteRepository.save(Site.builder()
                .version(0)
                .build());

        var content = Files.readString(Path.of("src/test/resources/data/input/valid_input.json"));

        mockMvc.perform(post(INGEST_URI)
                        .param("siteId", existingSite.getId().toString())
                        .param("version", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.buildingLimits").exists())
                .andExpect(jsonPath("$.buildingLimits[*]", hasSize(1)))
                .andExpect(jsonPath("$.buildingLimits[0].splitBuildingLimits[*]", hasSize(2)));

        var sites = siteRepository.findAll();

        assertThat(sites).hasSize(1);
        var siteActual = sites.get(0);
        assertNotNull(siteActual.getId());
        assertEquals(existingSite.getId(), siteActual.getId());
        assertEquals(1, siteActual.getVersion());

        var siteExpected = buildEntityFromFile("src/test/resources/data/output/valid_site_result.json", Site.class);
        assertSiteComponentsEquals(siteExpected, siteActual);
    }

    @DisplayName("Test to check if an exception is thrown when the height plateau/Building limit data is incorrect and gaps are present.")
    @Test
    void testIngestSiteDataWithIncorrectHeightPlateausThrowsExceptionAndIsABadRequest() throws Exception {

        var content = Files.readString(Path.of("src/test/resources/data/input/invalid_input.json"));

        mockMvc.perform(post(INGEST_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(INCORRECT_HEIGHT_PLATEAUS_EXCEPTION)));

        var sites = siteRepository.findAll();

        assertThat(sites).hasSize(0);
    }

    @DisplayName("Test to check if an exception is thrown when concurrent updates are tried")
    @Test
    void testIngestSiteDataWhenTryingToDoConcurrentUpdatesThrowsException() throws Exception {

        //Dummy existing site
        var site = siteRepository.save(Site.builder()
                .version(0)
                .build());

        //Concurrent update to site
        site.setVersion(1);
        siteRepository.save(site);

        var content = Files.readString(Path.of("src/test/resources/data/input/valid_input.json"));

        mockMvc.perform(post(INGEST_URI)
                        .param("siteId", "1")
                        .param("version", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(CONCURRENT_DATA_MODIFICATION_EXCEPTION)));

        var sites = siteRepository.findAll();

        assertThat(sites).hasSize(1);
    }

    @DisplayName("Test to check if an exception is thrown when a site that does not exist is tried to be updated.")
    @Test
    void testIngestSiteDataWhenTryingToUpdateSiteThatDoesNotExistsThrowsExceptionAndIsABadRequest() throws Exception {

        var content = Files.readString(Path.of("src/test/resources/data/input/invalid_input.json"));

        mockMvc.perform(post(INGEST_URI)
                        .param("siteId", "20")
                        .param("version", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());

        var sites = siteRepository.findAll();

        assertThat(sites).hasSize(0);
    }


    private void assertSiteComponentsEquals(Site siteExpected, Site siteActual) {
        var buildingLimitsExpected = siteExpected.getBuildingLimits();
        var buildingLimitsActual = siteActual.getBuildingLimits();
        assertEquals(buildingLimitsExpected.size(), buildingLimitsActual.size());

        var heightPlateausExpected = siteExpected.getHeightPlateaus();
        var heightPlateausActual = siteActual.getHeightPlateaus();
        assertEquals(heightPlateausExpected.size(), heightPlateausActual.size());

        for (int i = 0; i < buildingLimitsExpected.size(); i++) {
            var buildingLimitExpectedCoordinates = buildingLimitsExpected.get(i).getCoordinates();
            var buildingLimitsActualCoordinates = buildingLimitsActual.get(i).getCoordinates();
            assertThat(buildingLimitExpectedCoordinates)
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                    .isEqualTo(buildingLimitsActualCoordinates);

            var splitBuildingLimitsExpected = buildingLimitsExpected.get(i).getSplitBuildingLimits();
            var splitBuildingLimitsActual = buildingLimitsActual.get(i).getSplitBuildingLimits();
            for (int j = 0; j < splitBuildingLimitsExpected.size(); j++) {
                assertThat(splitBuildingLimitsExpected.get(j).getCoordinates())
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                        .isEqualTo(splitBuildingLimitsActual.get(j).getCoordinates());
            }

        }
        for (int i = 0; i < heightPlateausExpected.size(); i++) {
            var heightPlateausExpectedCoordinates = heightPlateausExpected.get(i).getCoordinates();
            var heightPlateausActualCoordinates = heightPlateausActual.get(i).getCoordinates();
            assertThat(heightPlateausExpectedCoordinates)
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                    .isEqualTo(heightPlateausActualCoordinates);
        }
    }

}