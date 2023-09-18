package com.architecture.api.service;

import com.architecture.api.exception.ConcurrentDataModificationException;
import com.architecture.api.model.Site;
import com.architecture.api.repository.BuildingLimitRepository;
import com.architecture.api.repository.CoordinateRepository;
import com.architecture.api.repository.HeightPlateauRepository;
import com.architecture.api.repository.SiteRepository;
import com.architecture.api.repository.SplitBuildingLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.architecture.api.exception.Errors.CONCURRENT_DATA_MODIFICATION_EXCEPTION;

@Service
@Transactional
@RequiredArgsConstructor
public class SiteDataService {

    private final SiteRepository siteRepository;

    private final BuildingLimitRepository buildingLimitRepository;

    private final HeightPlateauRepository heightPlateauRepository;

    private final SplitBuildingLimitRepository splitBuildingLimitRepository;

    private final CoordinateRepository coordinateRepository;

    public Site saveSiteData(Site site) {
        if (isNewSite(site)) {
            return siteRepository.save(site);
        } else {
            var siteData = siteRepository.getReferenceById(site.getId());
            deleteHeightPlateaus(siteData);
            deleteBuildingLimits(siteData);
            addNewSiteData(site, siteData);
            siteRepository.save(siteData);
            int rowCount = siteRepository.updateSiteVersion(site.getVersion(), site.getVersion() + 1);
            if(rowCount == 0) {
                throw new ConcurrentDataModificationException(CONCURRENT_DATA_MODIFICATION_EXCEPTION);
            }
            return siteData;
        }
    }

    private void addNewSiteData(Site site, Site siteData) {
        siteData.addNewBuildingLimits(site.getBuildingLimits());
        siteData.addNewHeightPlateaus(site.getHeightPlateaus());
    }

    private void deleteBuildingLimits(Site siteData) {
        var buildingLimits = siteData.getBuildingLimits();
        buildingLimits.forEach(bl -> bl.getSplitBuildingLimits().forEach(sbl -> coordinateRepository.deleteAllInBatch(sbl.getCoordinates())));
        buildingLimits.forEach(bl -> splitBuildingLimitRepository.deleteAllInBatch(bl.getSplitBuildingLimits()));
        buildingLimits.forEach(bl -> coordinateRepository.deleteAllInBatch(bl.getCoordinates()));
        buildingLimitRepository.deleteAllInBatch(buildingLimits);
    }

    private void deleteHeightPlateaus(Site siteData) {
        var heightPlateaus = siteData.getHeightPlateaus();
        heightPlateaus.forEach(hp -> coordinateRepository.deleteAllInBatch(hp.getCoordinates()));
        heightPlateauRepository.deleteAllInBatch(heightPlateaus);
    }

    private boolean isNewSite(Site site) {
        return site.getId() == null;
    }
}
