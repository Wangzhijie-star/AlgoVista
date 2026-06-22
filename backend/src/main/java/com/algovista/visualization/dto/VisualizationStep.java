package com.algovista.visualization.dto;

import java.util.List;

public record VisualizationStep(
        int stepIndex,
        String description,
        String operationType,
        List<Integer> arrayState,
        List<Integer> activeIndexes,
        int codeLine
) {
}
