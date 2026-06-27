package com.algovista.visualization.dto;

import java.util.List;
import java.util.Map;

public record VisualizationStep(
        int stepIndex,
        String description,
        String operationType,
        List<Integer> arrayState,
        List<Integer> activeIndexes,
        int codeLine,
        Map<String, Object> metadata
) {
    public VisualizationStep(
            int stepIndex,
            String description,
            String operationType,
            List<Integer> arrayState,
            List<Integer> activeIndexes,
            int codeLine
    ) {
        this(stepIndex, description, operationType, arrayState, activeIndexes, codeLine, Map.of());
    }
}
