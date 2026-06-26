package com.algovista.visualization.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record VisualizationRequest(
        @Size(min = 2, max = 12, message = "数组长度必须在 2 到 12 之间")
        List<@Min(value = 1, message = "数组元素最小为 1") @Max(value = 20, message = "数组元素最大为 20") Integer> array
) {
}
