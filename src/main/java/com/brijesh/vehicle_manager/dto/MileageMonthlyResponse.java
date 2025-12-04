package com.brijesh.vehicle_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MileageMonthlyResponse {
    private Map<String, MonthlyMileageData> data; // "JAN" → MonthlyMileageData
}
