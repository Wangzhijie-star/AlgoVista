package com.algovista.algorithm.controller;

import com.algovista.algorithm.entity.Algorithm;
import com.algovista.algorithm.entity.AlgorithmCategory;
import com.algovista.algorithm.service.AlgorithmService;
import com.algovista.common.result.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlgorithmController {
    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<AlgorithmCategory>> categories() {
        return ApiResponse.ok(algorithmService.listCategories());
    }

    @GetMapping("/algorithms")
    public ApiResponse<List<Algorithm>> algorithms(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.ok(algorithmService.listAlgorithms(categoryId));
    }

    @GetMapping("/algorithms/{id}")
    public ApiResponse<Algorithm> detail(@PathVariable Long id) {
        return ApiResponse.ok(algorithmService.getAlgorithm(id));
    }
}
