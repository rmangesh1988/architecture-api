package com.architecture.api.mapper;

import com.architecture.api.dto.BuildingLimitsDTO;
import com.architecture.api.model.BuildingLimit;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.architecture.api.mapper.MapperHelper.populateCoordinates;

@Component
public class BuildingLimitMapper {

    public List<BuildingLimit> toBuildingLimit(BuildingLimitsDTO buildingLimitsDTO) {
        var feature = buildingLimitsDTO.getFeatures();
        return feature.stream().map(f -> BuildingLimit.builder()
                .coordinates(populateCoordinates(f))
                .build()).toList();
    }


}
