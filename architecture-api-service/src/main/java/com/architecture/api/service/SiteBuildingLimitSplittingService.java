package com.architecture.api.service;

import com.architecture.api.dto.GeoJsonDTO;
import com.architecture.api.exception.IncorrectHeightPlateausException;
import com.architecture.api.mapper.BuildingLimitMapper;
import com.architecture.api.mapper.HeightPlateauMapper;
import com.architecture.api.model.BuildingLimit;
import com.architecture.api.model.HeightPlateau;
import com.architecture.api.model.Site;
import com.architecture.api.model.SplitBuildingLimit;
import com.architecture.api.util.JTSUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.architecture.api.exception.Errors.INCORRECT_HEIGHT_PLATEAUS_EXCEPTION;

@Service
@RequiredArgsConstructor
public class SiteBuildingLimitSplittingService {

    private final HeightPlateauMapper heightPlateauMapper;
    private final BuildingLimitMapper buildingLimitMapper;

    private final JTSUtil jtsUtil;

    public Site splitBuildingLimitsOnTheSite(GeoJsonDTO geoJsonDTO, Long siteId, Integer version) {
        var heightPlateaus = heightPlateauMapper.toHeightPlateau(geoJsonDTO.getHeightPlateausDTO());
        var buildingLimits = buildingLimitMapper.toBuildingLimit(geoJsonDTO.getBuildingLimitsDTO());
        var splitBuildingLimitsList = new ArrayList<List<SplitBuildingLimit>>();
        org.locationtech.jts.geom.Coordinate[] coordinates;

        for (BuildingLimit buildingLimit : buildingLimits) {
            var blRing = jtsUtil.convertToLinearRing(buildingLimit.getCoordinates());
            var blPolygon = new Polygon(blRing, null, new GeometryFactory());
            var blArea = blPolygon.getArea();
            var blSplitsAreaAccumulator = 0.0;
            var splitBuildingLimits = new ArrayList<SplitBuildingLimit>();
            for (HeightPlateau heightPlateau : heightPlateaus) {
                var hpRing = jtsUtil.convertToLinearRing(heightPlateau.getCoordinates());

                var hpPolygon = new Polygon(hpRing, null, new GeometryFactory());

                var commonPolygon = hpPolygon.intersection(blPolygon);
                blSplitsAreaAccumulator += commonPolygon.getArea();
                coordinates = commonPolygon.getCoordinates();
                splitBuildingLimits.add(SplitBuildingLimit.builder()
                        .elevation(heightPlateau.getElevation())
                        .coordinates(jtsUtil.convertToEntityCoordinates(coordinates))
                        .build());
            }
            if(blSplitsAreaAccumulator != blArea) {
                throw new IncorrectHeightPlateausException(INCORRECT_HEIGHT_PLATEAUS_EXCEPTION);
            }
            splitBuildingLimitsList.add(splitBuildingLimits);
            buildingLimit.setSplitBuildingLimits(splitBuildingLimits);
        }
        return Site.builder()
                .id(siteId)
                .version(version == null ? 0 : version)
                .buildingLimits(buildingLimits)
                .heightPlateaus(heightPlateaus)
                .build();
    }



}
