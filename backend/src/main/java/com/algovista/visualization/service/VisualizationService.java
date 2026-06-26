package com.algovista.visualization.service;

import com.algovista.algorithm.entity.Algorithm;
import com.algovista.algorithm.service.AlgorithmService;
import com.algovista.common.exception.BusinessException;
import com.algovista.visualization.dto.VisualizationResponse;
import com.algovista.visualization.dto.VisualizationStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisualizationService {
    private static final List<Integer> DEFAULT_BUBBLE_INPUT = List.of(5, 3, 8, 4, 2);
    private static final List<String> BUBBLE_CODE = List.of(
            "for i from 0 to n - 2",
            "  for j from 0 to n - i - 2",
            "    if arr[j] > arr[j + 1]",
            "      swap arr[j] and arr[j + 1]"
    );

    private final AlgorithmService algorithmService;

    public VisualizationService(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    public VisualizationResponse getVisualization(Long algorithmId) {
        return getVisualization(algorithmId, DEFAULT_BUBBLE_INPUT);
    }

    public VisualizationResponse getVisualization(Long algorithmId, List<Integer> input) {
        Algorithm algorithm = algorithmService.getAlgorithm(algorithmId);
        if (!"bubble-sort".equals(algorithm.getSlug())) {
            return placeholder(algorithm);
        }
        return buildBubbleSort(algorithm, input == null || input.isEmpty() ? DEFAULT_BUBBLE_INPUT : input);
    }

    private VisualizationResponse buildBubbleSort(Algorithm algorithm, List<Integer> input) {
        if (input.size() < 2 || input.size() > 12) {
            throw new BusinessException(400, "数组长度必须在 2 到 12 之间");
        }
        if (input.stream().anyMatch(value -> value == null || value < 1 || value > 20)) {
            throw new BusinessException(400, "数组元素必须是 1 到 20 之间的整数");
        }
        List<Integer> normalizedInput = List.copyOf(input);
        List<Integer> arr = new ArrayList<>(normalizedInput);
        List<VisualizationStep> steps = new ArrayList<>();
        int stepIndex = 0;
        steps.add(new VisualizationStep(stepIndex++, "初始数组准备完成", "init", List.copyOf(arr), List.of(), 1));

        for (int i = 0; i < arr.size() - 1; i++) {
            for (int j = 0; j < arr.size() - i - 1; j++) {
                steps.add(new VisualizationStep(stepIndex++, "比较第 " + (j + 1) + " 和第 " + (j + 2) + " 个元素",
                        "compare", List.copyOf(arr), List.of(j, j + 1), 3));
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    steps.add(new VisualizationStep(stepIndex++, "左侧元素更大，交换两个元素",
                            "swap", List.copyOf(arr), List.of(j, j + 1), 4));
                }
            }
        }

        steps.add(new VisualizationStep(stepIndex, "排序完成", "done", List.copyOf(arr), List.of(), 1));
        return new VisualizationResponse(algorithm.getId(), algorithm.getName(), normalizedInput, BUBBLE_CODE, steps);
    }

    private VisualizationResponse placeholder(Algorithm algorithm) {
        if (algorithm.getSlug() == null) {
            throw new BusinessException(404, "算法不存在");
        }
        List<VisualizationStep> steps = List.of(new VisualizationStep(
                0,
                algorithm.getName() + " 的步骤生成将在后续阶段接入",
                "placeholder",
                List.of(),
                List.of(),
                1
        ));
        return new VisualizationResponse(algorithm.getId(), algorithm.getName(), List.of(), List.of("即将接入可视化步骤"), steps);
    }
}
