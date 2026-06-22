package com.algovista.visualization.dto;

import java.util.List;

public record VisualizationResponse(
        Long algorithmId,
        String algorithmName,
        List<Integer> input,
        List<String> codeLines,
        List<VisualizationStep> steps
) {
}
