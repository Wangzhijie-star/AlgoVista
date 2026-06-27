package com.algovista.visualization.service;

import com.algovista.algorithm.entity.Algorithm;
import com.algovista.algorithm.service.AlgorithmService;
import com.algovista.common.exception.BusinessException;
import com.algovista.visualization.dto.VisualizationRequest;
import com.algovista.visualization.dto.VisualizationResponse;
import com.algovista.visualization.dto.VisualizationStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

@Service
public class VisualizationService {
    private static final int INF = 1_000_000;
    private static final List<Integer> DEFAULT_INPUT = List.of(5, 3, 8, 4, 2);
    private static final String DEFAULT_DIJKSTRA_INPUT = "起点:A;边:A-B-5,A-C-2,B-D-6,C-D-4,C-E-7,D-E-1";
    private static final String DEFAULT_COLORING_INPUT = "节点:A,B,C,D,E;边:A-B,A-C,B-D,C-D,C-E,D-E;颜色:红,蓝,绿";

    private static final List<String> BUBBLE_CODE = List.of(
            "public int[] bubbleSort(int[] nums) {",
            "    if (nums == null || nums.length < 2) {",
            "        return nums;",
            "    }",
            "",
            "    for (int i = 0; i < nums.length - 1; i++) {",
            "        boolean swapped = false;",
            "        for (int j = 0; j < nums.length - i - 1; j++) {",
            "            if (nums[j] > nums[j + 1]) {",
            "                int temp = nums[j];",
            "                nums[j] = nums[j + 1];",
            "                nums[j + 1] = temp;",
            "                swapped = true;",
            "            }",
            "        }",
            "",
            "        if (!swapped) {",
            "            break;",
            "        }",
            "    }",
            "",
            "    return nums;",
            "}"
    );

    private static final List<String> MERGE_CODE = List.of(
            "public int[] mergeSort(int[] nums) {",
            "    if (nums == null || nums.length < 2) {",
            "        return nums;",
            "    }",
            "",
            "    int[] temp = new int[nums.length];",
            "    mergeSort(nums, 0, nums.length - 1, temp);",
            "    return nums;",
            "}",
            "",
            "private void mergeSort(int[] nums, int left, int right, int[] temp) {",
            "    if (left >= right) {",
            "        return;",
            "    }",
            "",
            "    int mid = left + (right - left) / 2;",
            "    mergeSort(nums, left, mid, temp);",
            "    mergeSort(nums, mid + 1, right, temp);",
            "    merge(nums, left, mid, right, temp);",
            "}",
            "",
            "private void merge(int[] nums, int left, int mid, int right, int[] temp) {",
            "    int i = left;",
            "    int j = mid + 1;",
            "    int k = left;",
            "",
            "    while (i <= mid && j <= right) {",
            "        if (nums[i] <= nums[j]) {",
            "            temp[k++] = nums[i++];",
            "        } else {",
            "            temp[k++] = nums[j++];",
            "        }",
            "    }",
            "",
            "    while (i <= mid) {",
            "        temp[k++] = nums[i++];",
            "    }",
            "",
            "    while (j <= right) {",
            "        temp[k++] = nums[j++];",
            "    }",
            "",
            "    for (int index = left; index <= right; index++) {",
            "        nums[index] = temp[index];",
            "    }",
            "}"
    );

    private static final List<String> DIJKSTRA_CODE = List.of(
            "public Map<String, Integer> dijkstra(Map<String, List<Edge>> graph, String start) {",
            "    Map<String, Integer> dist = new HashMap<>();",
            "    Set<String> visited = new HashSet<>();",
            "    PriorityQueue<NodeDistance> queue =",
            "            new PriorityQueue<>(Comparator.comparingInt(NodeDistance::distance));",
            "",
            "    for (String node : graph.keySet()) {",
            "        dist.put(node, Integer.MAX_VALUE);",
            "    }",
            "",
            "    dist.put(start, 0);",
            "    queue.offer(new NodeDistance(start, 0));",
            "",
            "    while (!queue.isEmpty()) {",
            "        NodeDistance current = queue.poll();",
            "        String currentNode = current.node();",
            "",
            "        if (visited.contains(currentNode)) {",
            "            continue;",
            "        }",
            "",
            "        visited.add(currentNode);",
            "",
            "        for (Edge edge : graph.getOrDefault(currentNode, List.of())) {",
            "            String next = edge.to();",
            "            int newDistance = dist.get(currentNode) + edge.weight();",
            "",
            "            if (newDistance < dist.get(next)) {",
            "                dist.put(next, newDistance);",
            "                queue.offer(new NodeDistance(next, newDistance));",
            "            }",
            "        }",
            "    }",
            "",
            "    return dist;",
            "}"
    );

    private static final List<String> COLORING_CODE = List.of(
            "public Map<String, String> colorGraph(",
            "        Map<String, List<String>> graph, List<String> palette) {",
            "    Map<String, String> result = new LinkedHashMap<>();",
            "",
            "    for (String node : graph.keySet()) {",
            "        for (String color : palette) {",
            "            if (canUseColor(graph, result, node, color)) {",
            "                result.put(node, color);",
            "                break;",
            "            }",
            "        }",
            "    }",
            "",
            "    return result;",
            "}",
            "",
            "private boolean canUseColor(Map<String, List<String>> graph,",
            "        Map<String, String> result, String node, String color) {",
            "    for (String neighbor : graph.getOrDefault(node, List.of())) {",
            "        if (color.equals(result.get(neighbor))) {",
            "            return false;",
            "        }",
            "    }",
            "",
            "    return true;",
            "}"
    );

    private final AlgorithmService algorithmService;

    public VisualizationService(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    public VisualizationResponse getVisualization(Long algorithmId) {
        Algorithm algorithm = algorithmService.getAlgorithm(algorithmId);
        return switch (algorithm.getSlug()) {
            case "dijkstra" -> buildDijkstra(algorithm, DEFAULT_DIJKSTRA_INPUT);
            case "graph-coloring" -> buildGraphColoring(algorithm, DEFAULT_COLORING_INPUT);
            default -> getVisualization(algorithmId, DEFAULT_INPUT);
        };
    }

    public VisualizationResponse getVisualization(Long algorithmId, List<Integer> input) {
        Algorithm algorithm = algorithmService.getAlgorithm(algorithmId);
        List<Integer> normalizedInput = normalizeInput(input);
        return switch (algorithm.getSlug()) {
            case "bubble-sort" -> buildBubbleSort(algorithm, normalizedInput);
            case "merge-sort" -> buildMergeSort(algorithm, normalizedInput);
            case "dijkstra" -> buildDijkstra(algorithm, DEFAULT_DIJKSTRA_INPUT);
            case "graph-coloring" -> buildGraphColoring(algorithm, DEFAULT_COLORING_INPUT);
            default -> throw new BusinessException(400, "暂不支持该算法演示");
        };
    }

    public VisualizationResponse getVisualization(Long algorithmId, VisualizationRequest request) {
        Algorithm algorithm = algorithmService.getAlgorithm(algorithmId);
        if ("dijkstra".equals(algorithm.getSlug())) {
            String graphText = request.graphText() == null || request.graphText().isBlank()
                    ? DEFAULT_DIJKSTRA_INPUT
                    : request.graphText();
            return buildDijkstra(algorithm, graphText);
        }
        if ("graph-coloring".equals(algorithm.getSlug())) {
            String graphText = request.graphText() == null || request.graphText().isBlank()
                    ? DEFAULT_COLORING_INPUT
                    : request.graphText();
            return buildGraphColoring(algorithm, graphText);
        }
        return getVisualization(algorithmId, request.array());
    }

    private List<Integer> normalizeInput(List<Integer> input) {
        List<Integer> values = input == null || input.isEmpty() ? DEFAULT_INPUT : input;
        if (values.size() < 2 || values.size() > 12) {
            throw new BusinessException(400, "数组长度必须在 2 到 12 之间");
        }
        if (values.stream().anyMatch(value -> value == null || value < 1 || value > 20)) {
            throw new BusinessException(400, "数组元素必须是 1 到 20 之间的整数");
        }
        return List.copyOf(values);
    }

    private VisualizationResponse buildBubbleSort(Algorithm algorithm, List<Integer> input) {
        List<Integer> arr = new ArrayList<>(input);
        List<VisualizationStep> steps = new ArrayList<>();
        int stepIndex = 0;
        steps.add(step(stepIndex++, "初始数组准备完成", "init", arr, List.of(), 1));

        for (int i = 0; i < arr.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < arr.size() - i - 1; j++) {
                steps.add(step(stepIndex++, "比较第 " + (j + 1) + " 和第 " + (j + 2) + " 个元素",
                        "compare", arr, List.of(j, j + 1), 1));
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapped = true;
                    steps.add(step(stepIndex++, "左侧元素更大，交换两个元素",
                            "swap", arr, List.of(j, j + 1), 1));
                }
            }
            if (!swapped) {
                steps.add(step(stepIndex++, "本轮没有发生交换，数组已经有序，可以提前结束",
                        "early-stop", arr, List.of(), 1));
                break;
            }
        }

        steps.add(step(stepIndex, "排序完成", "done", arr, List.of(), 1));
        return new VisualizationResponse(algorithm.getId(), algorithm.getName(), input, BUBBLE_CODE, steps);
    }

    private VisualizationResponse buildMergeSort(Algorithm algorithm, List<Integer> input) {
        List<Integer> arr = new ArrayList<>(input);
        List<VisualizationStep> steps = new ArrayList<>();
        int[] stepIndex = {0};
        steps.add(step(stepIndex[0]++, "初始数组准备完成", "init", arr, List.of(), 1));
        mergeSort(arr, 0, arr.size() - 1, steps, stepIndex);
        steps.add(step(stepIndex[0], "归并排序完成", "done", arr, range(0, arr.size() - 1), 1));
        return new VisualizationResponse(algorithm.getId(), algorithm.getName(), input, MERGE_CODE, steps);
    }

    private void mergeSort(List<Integer> arr, int left, int right, List<VisualizationStep> steps, int[] stepIndex) {
        if (left >= right) {
            steps.add(step(stepIndex[0]++, "区间 [" + (left + 1) + ", " + (right + 1) + "] 长度为 1，无需继续拆分",
                    "single", arr, List.of(left), 1));
            return;
        }

        int mid = (left + right) / 2;
        steps.add(step(stepIndex[0]++, "拆分区间 [" + (left + 1) + ", " + (right + 1) + "]",
                "split", arr, range(left, right), 1));
        mergeSort(arr, left, mid, steps, stepIndex);
        mergeSort(arr, mid + 1, right, steps, stepIndex);
        merge(arr, left, mid, right, steps, stepIndex);
    }

    private void merge(List<Integer> arr, int left, int mid, int right, List<VisualizationStep> steps, int[] stepIndex) {
        List<Integer> merged = new ArrayList<>();
        int i = left;
        int j = mid + 1;
        while (i <= mid && j <= right) {
            steps.add(step(stepIndex[0]++, "比较左半区第 " + (i + 1) + " 个和右半区第 " + (j + 1) + " 个元素",
                    "compare", arr, List.of(i, j), 1));
            if (arr.get(i) <= arr.get(j)) {
                merged.add(arr.get(i++));
            } else {
                merged.add(arr.get(j++));
            }
        }
        while (i <= mid) {
            merged.add(arr.get(i++));
        }
        while (j <= right) {
            merged.add(arr.get(j++));
        }
        for (int offset = 0; offset < merged.size(); offset++) {
            arr.set(left + offset, merged.get(offset));
            steps.add(step(stepIndex[0]++, "写回合并结果到第 " + (left + offset + 1) + " 个位置",
                    "merge", arr, List.of(left + offset), 1));
        }
    }

    private VisualizationResponse buildDijkstra(Algorithm algorithm, String inputText) {
        DijkstraGraph graph = parseDijkstraGraph(inputText);
        Map<String, Integer> dist = new LinkedHashMap<>();
        Set<String> visited = new LinkedHashSet<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(NodeDistance::distance));

        for (String node : graph.nodes()) {
            dist.put(node, INF);
        }
        dist.put(graph.start(), 0);
        queue.offer(new NodeDistance(graph.start(), 0));

        List<VisualizationStep> steps = new ArrayList<>();
        int stepIndex = 0;
        steps.add(dijkstraStep(stepIndex++, "从 " + graph.start() + " 出发，初始化源点距离为 0",
                "init", graph, dist, visited, graph.start(), null, null));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            if (visited.contains(current.node())) {
                continue;
            }

            steps.add(dijkstraStep(stepIndex++, "选择当前距离最短的未确定节点 " + current.node(),
                    "select-node", graph, dist, visited, current.node(), null, null));
            visited.add(current.node());

            for (Edge edge : graph.adjacency().getOrDefault(current.node(), List.of())) {
                if (visited.contains(edge.to())) {
                    continue;
                }
                int candidate = dist.get(current.node()) + edge.weight();
                String edgeLabel = edge.from() + " -> " + edge.to();
                steps.add(dijkstraStep(stepIndex++, "检查边 " + edgeLabel + "，候选距离 = "
                        + dist.get(current.node()) + " + " + edge.weight() + " = " + candidate,
                        "relax-edge", graph, dist, visited, current.node(), edge, edge.to()));

                if (candidate < dist.get(edge.to())) {
                    dist.put(edge.to(), candidate);
                    queue.offer(new NodeDistance(edge.to(), candidate));
                    steps.add(dijkstraStep(stepIndex++, "更新节点 " + edge.to() + " 的最短距离为 " + candidate,
                            "update-distance", graph, dist, visited, current.node(), edge, edge.to()));
                } else {
                    steps.add(dijkstraStep(stepIndex++, "节点 " + edge.to() + " 当前距离更短，不更新",
                            "keep-distance", graph, dist, visited, current.node(), edge, null));
                }
            }
        }

        steps.add(dijkstraStep(stepIndex, "Dijkstra 计算完成，表格中为源点到各节点的最短路径长度",
                "done", graph, dist, visited, null, null, null));
        return new VisualizationResponse(algorithm.getId(), algorithm.getName(), List.of(), DIJKSTRA_CODE, steps);
    }

    private DijkstraGraph parseDijkstraGraph(String inputText) {
        String normalized = normalizeGraphText(inputText);
        String start = null;
        String edgesText = null;
        for (String part : normalized.split(";")) {
            if (part.startsWith("起点:") || part.toLowerCase().startsWith("start:")) {
                start = part.substring(part.indexOf(':') + 1).trim().toUpperCase();
            }
            if (part.startsWith("边:") || part.toLowerCase().startsWith("edges:")) {
                edgesText = part.substring(part.indexOf(':') + 1).trim().toUpperCase();
            }
        }
        if (start == null || start.isBlank() || edgesText == null || edgesText.isBlank()) {
            throw new BusinessException(400, "Dijkstra 输入格式应为：起点:A;边:A-B-5,A-C-2");
        }

        LinkedHashSet<String> nodes = new LinkedHashSet<>();
        List<Edge> displayEdges = new ArrayList<>();
        Map<String, List<Edge>> adjacency = new LinkedHashMap<>();
        nodes.add(start);

        for (String item : edgesText.split(",")) {
            String[] parts = item.split("-");
            if (parts.length != 3) {
                throw new BusinessException(400, "边格式错误，请使用 A-B-5 这样的格式");
            }
            String from = parts[0].trim().toUpperCase();
            String to = parts[1].trim().toUpperCase();
            int weight;
            try {
                weight = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException ex) {
                throw new BusinessException(400, "边权值必须是正整数");
            }
            if (!from.matches("[A-Z]") || !to.matches("[A-Z]") || weight <= 0 || weight > 99) {
                throw new BusinessException(400, "节点必须是大写字母，权值必须是 1 到 99 的整数");
            }
            nodes.add(from);
            nodes.add(to);
            displayEdges.add(new Edge(from, to, weight));
            adjacency.computeIfAbsent(from, key -> new ArrayList<>()).add(new Edge(from, to, weight));
            adjacency.computeIfAbsent(to, key -> new ArrayList<>()).add(new Edge(to, from, weight));
        }

        if (!nodes.contains(start)) {
            throw new BusinessException(400, "起点必须出现在边列表中");
        }
        if (nodes.size() < 3 || nodes.size() > 8) {
            throw new BusinessException(400, "Dijkstra 节点数量必须在 3 到 8 个之间");
        }
        if (displayEdges.size() < 2 || displayEdges.size() > 16) {
            throw new BusinessException(400, "Dijkstra 边数量必须在 2 到 16 条之间");
        }

        return new DijkstraGraph(start, List.copyOf(nodes), List.copyOf(displayEdges), adjacency);
    }

    private VisualizationResponse buildGraphColoring(Algorithm algorithm, String inputText) {
        ColoringGraph graph = parseColoringGraph(inputText);
        Map<String, String> colors = new LinkedHashMap<>();
        List<VisualizationStep> steps = new ArrayList<>();
        int stepIndex = 0;

        steps.add(coloringStep(stepIndex++, "准备开始图着色。规则是：相邻节点不能使用相同颜色；系统会按节点顺序依次尝试调色板中的颜色。",
                "init", graph, colors, null, null, null, null));

        for (String node : graph.nodes()) {
            steps.add(coloringStep(stepIndex++, "轮到节点 " + node + "。先观察它的相邻节点："
                            + neighborText(graph, node) + "，只要候选颜色没有被这些相邻节点使用，就可以保留。",
                    "select-node", graph, colors, node, null, null, null));

            boolean colored = false;
            for (String color : graph.palette()) {
                steps.add(coloringStep(stepIndex++, "尝试把节点 " + node + " 染成「" + color + "」。需要逐一检查相邻节点 "
                                + neighborText(graph, node) + " 是否已经使用「" + color + "」。",
                        "try-color", graph, colors, node, color, null, null));

                String conflictNode = findColorConflict(graph, colors, node, color);
                if (conflictNode == null) {
                    colors.put(node, color);
                    steps.add(coloringStep(stepIndex++, "颜色「" + color + "」可以用于节点 " + node
                                    + "，因为它的相邻节点中没有任何一个已经使用该颜色。于是将节点 " + node + " 正式染成「" + color + "」。",
                            "set-color", graph, colors, node, color, null, node));
                    colored = true;
                    break;
                }

                steps.add(coloringStep(stepIndex++, "颜色「" + color + "」不能用于节点 " + node
                                + "，因为相邻节点 " + conflictNode + " 已经是「" + color + "」。相邻节点同色会违反图着色约束，所以继续尝试下一个颜色。",
                        "reject-color", graph, colors, node, color, conflictNode, null));
            }

            if (!colored) {
                steps.add(coloringStep(stepIndex++, "节点 " + node + " 在当前调色板中找不到可用颜色，说明这个图至少需要更多颜色或需要换一种搜索顺序。",
                        "need-more-colors", graph, colors, node, null, null, null));
            }
        }

        steps.add(coloringStep(stepIndex, "图着色演示完成。最终结果满足：每一条边连接的两个节点颜色都不相同。",
                "done", graph, colors, null, null, null, null));
        return new VisualizationResponse(algorithm.getId(), algorithm.getName(), List.of(), COLORING_CODE, steps);
    }

    private ColoringGraph parseColoringGraph(String inputText) {
        String normalized = normalizeGraphText(inputText);
        String nodesText = null;
        String edgesText = null;
        String colorsText = null;
        for (String part : normalized.split(";")) {
            String lower = part.toLowerCase();
            if (part.startsWith("节点:") || lower.startsWith("nodes:")) {
                nodesText = part.substring(part.indexOf(':') + 1).trim().toUpperCase();
            }
            if (part.startsWith("边:") || lower.startsWith("edges:")) {
                edgesText = part.substring(part.indexOf(':') + 1).trim().toUpperCase();
            }
            if (part.startsWith("颜色:") || lower.startsWith("colors:")) {
                colorsText = part.substring(part.indexOf(':') + 1).trim();
            }
        }
        if (nodesText == null || nodesText.isBlank() || edgesText == null || edgesText.isBlank()) {
            throw new BusinessException(400, "图着色输入格式应为：节点:A,B,C;边:A-B,B-C;颜色:红,蓝,绿");
        }

        LinkedHashSet<String> nodes = new LinkedHashSet<>();
        for (String node : nodesText.split(",")) {
            String value = node.trim().toUpperCase();
            if (!value.matches("[A-Z]")) {
                throw new BusinessException(400, "节点必须使用大写英文字母，例如 A,B,C");
            }
            nodes.add(value);
        }
        if (nodes.size() < 3 || nodes.size() > 8) {
            throw new BusinessException(400, "图着色节点数量必须在 3 到 8 个之间");
        }

        List<Edge> displayEdges = new ArrayList<>();
        Map<String, Set<String>> adjacency = new LinkedHashMap<>();
        for (String node : nodes) {
            adjacency.put(node, new LinkedHashSet<>());
        }

        for (String item : edgesText.split(",")) {
            String[] parts = item.split("-");
            if (parts.length != 2) {
                throw new BusinessException(400, "边格式错误，请使用 A-B 这样的格式");
            }
            String from = parts[0].trim().toUpperCase();
            String to = parts[1].trim().toUpperCase();
            if (!nodes.contains(from) || !nodes.contains(to)) {
                throw new BusinessException(400, "每条边的两个节点都必须出现在节点列表中");
            }
            if (from.equals(to)) {
                throw new BusinessException(400, "图着色暂不支持自环边，请删除 " + from + "-" + to);
            }
            displayEdges.add(new Edge(from, to, 0));
            adjacency.get(from).add(to);
            adjacency.get(to).add(from);
        }
        if (displayEdges.size() < 2 || displayEdges.size() > 16) {
            throw new BusinessException(400, "图着色边数量必须在 2 到 16 条之间");
        }

        List<String> palette = parsePalette(colorsText);
        return new ColoringGraph(List.copyOf(nodes), List.copyOf(displayEdges), adjacency, palette);
    }

    private List<String> parsePalette(String colorsText) {
        if (colorsText == null || colorsText.isBlank()) {
            return List.of("红", "蓝", "绿");
        }
        LinkedHashSet<String> palette = new LinkedHashSet<>();
        for (String color : colorsText.split(",")) {
            String value = color.trim();
            if (!value.isBlank()) {
                palette.add(value);
            }
        }
        if (palette.size() < 2 || palette.size() > 6) {
            throw new BusinessException(400, "颜色数量必须在 2 到 6 种之间");
        }
        return List.copyOf(palette);
    }

    private String findColorConflict(ColoringGraph graph, Map<String, String> colors, String node, String color) {
        for (String neighbor : graph.adjacency().getOrDefault(node, Set.of())) {
            if (color.equals(colors.get(neighbor))) {
                return neighbor;
            }
        }
        return null;
    }

    private String neighborText(ColoringGraph graph, String node) {
        Set<String> neighbors = graph.adjacency().getOrDefault(node, Set.of());
        return neighbors.isEmpty() ? "暂无相邻节点" : String.join("、", neighbors);
    }

    private VisualizationStep coloringStep(int stepIndex, String description, String operationType,
                                           ColoringGraph graph, Map<String, String> colors, String currentNode,
                                           String tryingColor, String conflictNode, String updatedNode) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("nodes", graph.nodes());
        metadata.put("edges", graph.displayEdges().stream().map(this::coloringEdgeMetadata).toList());
        metadata.put("colors", new LinkedHashMap<>(colors));
        metadata.put("palette", graph.palette());
        metadata.put("currentNode", currentNode);
        metadata.put("tryingColor", tryingColor);
        metadata.put("conflictNode", conflictNode);
        metadata.put("updatedNode", updatedNode);
        metadata.put("coloredNodes", List.copyOf(colors.keySet()));
        return new VisualizationStep(stepIndex, description, operationType, List.of(), List.of(), 0, metadata);
    }

    private VisualizationStep dijkstraStep(int stepIndex, String description, String operationType,
                                           DijkstraGraph graph, Map<String, Integer> dist, Set<String> visited,
                                           String currentNode, Edge checkingEdge, String updatedNode) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("nodes", graph.nodes());
        metadata.put("edges", graph.displayEdges().stream().map(this::edgeMetadata).toList());
        metadata.put("start", graph.start());
        metadata.put("currentNode", currentNode);
        metadata.put("checkingNode", checkingEdge == null ? null : checkingEdge.to());
        metadata.put("checkingEdge", checkingEdge == null ? null : edgeMetadata(checkingEdge));
        metadata.put("visitedNodes", List.copyOf(visited));
        metadata.put("distances", distanceMetadata(graph.nodes(), dist));
        metadata.put("updatedNode", updatedNode);
        return new VisualizationStep(stepIndex, description, operationType, List.of(), List.of(), 0, metadata);
    }

    private String normalizeGraphText(String inputText) {
        return inputText
                .replace('；', ';')
                .replace('，', ',')
                .replace("：", ":")
                .replace(" ", "")
                .trim();
    }

    private Map<String, Object> edgeMetadata(Edge edge) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("from", edge.from());
        metadata.put("to", edge.to());
        metadata.put("weight", edge.weight());
        return metadata;
    }

    private Map<String, Object> coloringEdgeMetadata(Edge edge) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("from", edge.from());
        metadata.put("to", edge.to());
        return metadata;
    }

    private Map<String, Object> distanceMetadata(List<String> nodes, Map<String, Integer> dist) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String node : nodes) {
            int value = dist.getOrDefault(node, INF);
            result.put(node, value >= INF ? "∞" : value);
        }
        return result;
    }

    private List<Integer> range(int left, int right) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            indexes.add(i);
        }
        return indexes;
    }

    private VisualizationStep step(int stepIndex, String description, String operationType,
                                   List<Integer> arr, List<Integer> activeIndexes, int codeLine) {
        return new VisualizationStep(stepIndex, description, operationType, List.copyOf(arr), activeIndexes, codeLine);
    }

    private record DijkstraGraph(
            String start,
            List<String> nodes,
            List<Edge> displayEdges,
            Map<String, List<Edge>> adjacency
    ) {
    }

    private record ColoringGraph(
            List<String> nodes,
            List<Edge> displayEdges,
            Map<String, Set<String>> adjacency,
            List<String> palette
    ) {
    }

    private record Edge(String from, String to, int weight) {
    }

    private record NodeDistance(String node, int distance) {
    }
}
