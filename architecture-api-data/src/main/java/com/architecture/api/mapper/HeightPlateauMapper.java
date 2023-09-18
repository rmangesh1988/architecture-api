package com.architecture.api.mapper;

import com.architecture.api.dto.HeightPlateausDTO;
import com.architecture.api.model.HeightPlateau;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.architecture.api.mapper.MapperHelper.populateCoordinates;

@Component
public class HeightPlateauMapper {

    public List<HeightPlateau> toHeightPlateau(HeightPlateausDTO heightPlateausDTO) {
        var features = heightPlateausDTO.getFeatures();
        return features.stream().map(f -> HeightPlateau.builder()
                .coordinates(populateCoordinates(f))
                .elevation(f.getProperty("elevation"))
                .build()).toList();
    }
}
