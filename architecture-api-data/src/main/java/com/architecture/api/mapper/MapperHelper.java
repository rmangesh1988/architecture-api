package com.architecture.api.mapper;

import com.architecture.api.model.Coordinate;
import lombok.experimental.UtilityClass;
import org.geojson.Feature;
import org.geojson.Polygon;

import java.util.List;

@UtilityClass
public class MapperHelper {

    public static List<Coordinate> populateCoordinates(Feature feature) {
        return ((Polygon)feature.getGeometry()).getCoordinates().get(0).stream().map(c -> Coordinate.builder()
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .build()).toList();
    }
}
