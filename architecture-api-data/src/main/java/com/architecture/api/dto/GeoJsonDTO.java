package com.architecture.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoJsonDTO {

    @JsonProperty("building_limits")
    private BuildingLimitsDTO buildingLimitsDTO;

    @JsonProperty("height_plateaus")
    private HeightPlateausDTO heightPlateausDTO;
}
