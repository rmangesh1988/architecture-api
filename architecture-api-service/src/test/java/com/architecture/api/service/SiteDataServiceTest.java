package com.architecture.api.service;

import com.architecture.api.exception.ConcurrentDataModificationException;
import com.architecture.api.model.BuildingLimit;
import com.architecture.api.model.HeightPlateau;
import com.architecture.api.model.Site;
import com.architecture.api.model.SplitBuildingLimit;
import com.architecture.api.repository.BuildingLimitRepository;
import com.architecture.api.repository.CoordinateRepository;
import com.architecture.api.repository.HeightPlateauRepository;
import com.architecture.api.repository.SiteRepository;
import com.architecture.api.repository.SplitBuildingLimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.architecture.api.exception.Errors.CONCURRENT_DATA_MODIFICATION_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiteDataServiceTest {

    @Mock
    SiteRepository siteRepository;

    @Mock
    BuildingLimitRepository buildingLimitRepository;

    @Mock
    HeightPlateauRepository heightPlateauRepository;

    @Mock
    SplitBuildingLimitRepository splitBuildingLimitRepository;

    @Mock
    CoordinateRepository coordinateRepository;

    @InjectMocks
    SiteDataService siteDataService;

    @BeforeEach
    void setup() {

    }

    @DisplayName("Test to verify if new site is saved successfully and necessary services are invoked")
    @Test
    void testSaveNewSiteSuccessful() {
        var site = new Site();
        var siteSaved = Site.builder()
                .id(1L)
                .version(0)
                .build();
        when(siteRepository.save(site)).thenReturn(siteSaved);
        var siteResult = siteDataService.saveSiteData(site);

        assertEquals(1L, siteResult.getId());
        verify(siteRepository, times(1)).save(site);
        verifyNoMoreInteractions(siteRepository, heightPlateauRepository, buildingLimitRepository, splitBuildingLimitRepository, coordinateRepository);
    }

    @DisplayName("Test to verify if existing site is saved/updated successfully and necessary services are invoked")
    @Test
    void testUpdateExistingSiteSuccessful() {
        var siteId = 1L;
        var version = 1;
        var site = Site.builder()
                .id(siteId)
                .version(version)
                .heightPlateaus(new ArrayList<>(List.of(
                        HeightPlateau.builder().build()
                )))
                .buildingLimits(new ArrayList<>(List.of(
                        BuildingLimit.builder()
                                .splitBuildingLimits(new ArrayList<>(List.of(
                                        SplitBuildingLimit.builder().build()
                                )))
                                .build()
                )))
                .build();

        when(siteRepository.getReferenceById(siteId)).thenReturn(site);
        when(siteRepository.updateSiteVersion(version, version+1)).thenReturn(1);
        siteDataService.saveSiteData(site);

        verify(siteRepository, times(1)).getReferenceById(anyLong());
        verify(heightPlateauRepository, times(1)).deleteAllInBatch(anyList());
        verify(buildingLimitRepository, times(1)).deleteAllInBatch(anyList());
        verify(splitBuildingLimitRepository, times(1)).deleteAllInBatch(anyList());
        verify(coordinateRepository, times(3)).deleteAllInBatch(any());
        verify(siteRepository, times(1)).save(any(Site.class));
        verifyNoMoreInteractions(siteRepository, heightPlateauRepository, buildingLimitRepository, splitBuildingLimitRepository, coordinateRepository);
    }

    @DisplayName("Test to an exception is thrown when concurrent updates are tried on an existing site data")
    @Test
    void testUpdateExistingSiteConcurrentlyThrowsException() {
        var siteId = 1L;
        var version = 1;
        var site = Site.builder()
                .id(siteId)
                .version(version)
                .heightPlateaus(new ArrayList<>(List.of(
                        HeightPlateau.builder().build()
                )))
                .buildingLimits(new ArrayList<>(List.of(
                        BuildingLimit.builder()
                                .splitBuildingLimits(new ArrayList<>(List.of(
                                        SplitBuildingLimit.builder().build()
                                )))
                                .build()
                )))
                .build();

        when(siteRepository.getReferenceById(siteId)).thenReturn(site);
        when(siteRepository.updateSiteVersion(version, version+1)).thenReturn(0);
        ConcurrentDataModificationException concurrentDataModificationException = assertThrows(ConcurrentDataModificationException.class, () -> siteDataService.saveSiteData(site));
        assertEquals(CONCURRENT_DATA_MODIFICATION_EXCEPTION, concurrentDataModificationException.getMessage());

        verify(siteRepository, times(1)).getReferenceById(anyLong());
        verify(heightPlateauRepository, times(1)).deleteAllInBatch(anyList());
        verify(buildingLimitRepository, times(1)).deleteAllInBatch(anyList());
        verify(splitBuildingLimitRepository, times(1)).deleteAllInBatch(anyList());
        verify(coordinateRepository, times(3)).deleteAllInBatch(any());
        verify(siteRepository, times(1)).save(any(Site.class));
        verifyNoMoreInteractions(siteRepository, heightPlateauRepository, buildingLimitRepository, splitBuildingLimitRepository, coordinateRepository);
    }

}