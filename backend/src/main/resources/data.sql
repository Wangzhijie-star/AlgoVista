INSERT INTO algorithm_category (id, name, sort_order)
VALUES
    (1, '排序算法', 1),
    (2, '图算法', 2)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort_order = VALUES(sort_order);

INSERT INTO algorithm (id, category_id, name, slug, difficulty, summary, description, enabled)
VALUES
    (1, 1, '冒泡排序', 'bubble-sort', '入门', '通过相邻元素比较和交换，将较大元素逐步移动到序列末尾。', '冒泡排序适合理解比较、交换和多轮扫描的基本思想，是 AlgoVista 第一阶段优先打通的完整可视化算法。', TRUE),
    (2, 1, '归并排序', 'merge-sort', '中等', '采用分治思想，将数组拆分后分别排序，再合并为有序序列。', '归并排序用于展示递归拆分、子问题求解和有序合并过程。', TRUE),
    (3, 2, 'Dijkstra 最短路径', 'dijkstra', '中等', '从起点出发，逐步确定到其他节点的最短距离。', 'Dijkstra 算法用于展示图中距离松弛、已确定集合和候选距离更新过程。', TRUE),
    (4, 2, '图着色', 'graph-coloring', '进阶', '为图中节点分配颜色，使相邻节点颜色不同。', '图着色用于展示约束满足和回溯搜索的基本过程。', TRUE)
ON DUPLICATE KEY UPDATE
    category_id = VALUES(category_id),
    name = VALUES(name),
    slug = VALUES(slug),
    difficulty = VALUES(difficulty),
    summary = VALUES(summary),
    description = VALUES(description),
    enabled = VALUES(enabled);
