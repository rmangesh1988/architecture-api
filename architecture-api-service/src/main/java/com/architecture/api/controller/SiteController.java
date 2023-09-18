package com.architecture.api.controller;

import com.architecture.api.dto.GeoJsonDTO;
import com.architecture.api.model.Site;
import com.architecture.api.service.SiteBuildingLimitSplittingService;
import com.architecture.api.service.SiteDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SiteController {

    private final SiteBuildingLimitSplittingService siteBuildingLimitSplittingService;

    private final SiteDataService siteDataService;

    @PostMapping("/ingest-site-data/")
    public ResponseEntity<Site> ingestSiteData(@RequestBody GeoJsonDTO geoJsonDTO, @RequestParam(name = "siteId", required = false) Long siteId, @RequestParam(name = "version", required = false) Integer version) {
        var site = siteBuildingLimitSplittingService.splitBuildingLimitsOnTheSite(geoJsonDTO, siteId, version);
        siteDataService.saveSiteData(site);
        return ResponseEntity.ok(site);
    }
}
