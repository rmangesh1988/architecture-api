package com.architecture.api.util;

import com.architecture.api.model.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JTSUtil {

    public List<Coordinate> convertToEntityCoordinates(org.locationtech.jts.geom.Coordinate[] coordinates) {
        return Arrays.stream(coordinates).map(c -> Coordinate.builder()
                .longitude(c.x)
                .latitude(c.y)
                .build()).toList();
    }

    public LinearRing convertToLinearRing(List<Coordinate> coordinates) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createLinearRing(coordinates.stream().map(c -> new org.locationtech.jts.geom.Coordinate(c.getLongitude(), c.getLatitude())).toArray(org.locationtech.jts.geom.Coordinate[]::new));
    }
}
