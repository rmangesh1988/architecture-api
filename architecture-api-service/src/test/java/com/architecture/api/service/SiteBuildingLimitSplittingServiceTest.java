package com.architecture.api.service;

import com.architecture.api.dto.GeoJsonDTO;
import com.architecture.api.exception.IncorrectHeightPlateausException;
import com.architecture.api.mapper.BuildingLimitMapper;
import com.architecture.api.mapper.HeightPlateauMapper;
import com.architecture.api.model.Site;
import com.architecture.api.util.JTSUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.architecture.api.TestHelper.buildEntityFromFile;
import static com.architecture.api.exception.Errors.INCORRECT_HEIGHT_PLATEAUS_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SiteBuildingLimitSplittingServiceTest {

    ObjectMapper objectMapper = new ObjectMapper();

    HeightPlateauMapper heightPlateauMapper = new HeightPlateauMapper();

    BuildingLimitMapper buildingLimitMapper = new BuildingLimitMapper();

    JTSUtil jtsUtil = new JTSUtil();

    SiteBuildingLimitSplittingService siteBuildingLimitSplittingService = new SiteBuildingLimitSplittingService(heightPlateauMapper, buildingLimitMapper, jtsUtil);

    @DisplayName("Test to check if the building limits are split correctly when the input is valid. The Site object contains all the info, including result")
    @Test
    void testSplitBuildingLimitsOnANewSiteWorksCorrectlyWithValidInput() throws IOException {
        var geoJsonDTOInput = buildEntityFromFile("src/test/resources/data/input/valid_input.json", GeoJsonDTO.class);
        var siteActual = siteBuildingLimitSplittingService.splitBuildingLimitsOnTheSite(geoJsonDTOInput, null, null);
        var siteExpected = buildEntityFromFile("src/test/resources/data/output/valid_site_result.json", Site.class);
        assertEquals(siteExpected, siteActual);
    }

    @DisplayName("Test to check if an exception is thrown properly when the data is invalid i.e. The height plateaus do not cover the building limits completely")
    @Test
    void testSplitBuildingLimitsOnANewSiteThrowExceptionWithInvalidInput() throws IOException {
        var geoJsonDTOInput = buildEntityFromFile("src/test/resources/data/input/invalid_input.json", GeoJsonDTO.class);
        var incorrectHeightPlateausException = assertThrows(IncorrectHeightPlateausException.class, () -> siteBuildingLimitSplittingService.splitBuildingLimitsOnTheSite(geoJsonDTOInput, null, null));
        assertEquals(INCORRECT_HEIGHT_PLATEAUS_EXCEPTION, incorrectHeightPlateausException.getMessage());
    }

    @DisplayName("Test to check if the building limits are split correctly when the input is valid and the site metadata is set correct")
    @Test
    void testSplitBuildingLimitsOnAnExistingSiteWorksCorrectlyWithValidInputAndVersion() throws IOException {
        var geoJsonDTOInput = buildEntityFromFile("src/test/resources/data/input/valid_input.json", GeoJsonDTO.class);
        var siteActual = siteBuildingLimitSplittingService.splitBuildingLimitsOnTheSite(geoJsonDTOInput, 1L, 1);
        var siteExpected = buildEntityFromFile("src/test/resources/data/output/valid_site_result_existing_update.json", Site.class);
        assertEquals(siteExpected, siteActual);
    }

}