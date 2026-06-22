package com.algovista.visualization.controller;

import com.algovista.common.result.ApiResponse;
import com.algovista.visualization.dto.VisualizationResponse;
import com.algovista.visualization.service.VisualizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/algorithms")
public class VisualizationController {
    private final VisualizationService visualizationService;

    public VisualizationController(VisualizationService visualizationService) {
        this.visualizationService = visualizationService;
    }

    @GetMapping("/{id}/visualization")
    public ApiResponse<VisualizationResponse> visualization(@PathVariable Long id) {
        return ApiResponse.ok(visualizationService.getVisualization(id));
    }
}
