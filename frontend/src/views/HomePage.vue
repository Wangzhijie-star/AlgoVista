<template>
  <section class="page home-page">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Algorithm Library</p>
        <h1>选择一个算法开始探索</h1>
        <p class="page-subtitle">从步骤、代码和图形三个视角理解算法运行过程。</p>
      </div>
      <button class="ghost-button" @click="loadData">刷新</button>
    </div>

    <div class="home-layout">
      <div>
        <div class="category-tabs">
          <button :class="{ active: selectedCategoryId === undefined }" @click="selectCategory(undefined)">全部</button>
          <button
            v-for="category in categories"
            :key="category.id"
            :class="{ active: selectedCategoryId === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </button>
        </div>

        <div class="algorithm-grid">
          <RouterLink v-for="algorithm in algorithms" :key="algorithm.id" class="algorithm-card" :to="`/algorithms/${algorithm.id}`">
            <div class="algorithm-visual" :class="algorithm.slug">
              <span v-for="value in visualBars(algorithm.slug)" :key="`${algorithm.slug}-${value}`" :style="{ height: `${value}px` }"></span>
            </div>
            <span class="difficulty-pill">{{ algorithm.difficulty }}</span>
            <h2>{{ algorithm.name }}</h2>
            <p>{{ algorithm.summary }}</p>
            <strong>进入演示</strong>
          </RouterLink>
        </div>
      </div>

      <aside class="study-card">
        <div class="week-row">
          <div class="today">
            <span>MON</span>
            <strong>今</strong>
          </div>
          <div v-for="day in weekDays" :key="day.label">
            <span>{{ day.label }}</span>
            <strong>{{ day.date }}</strong>
          </div>
        </div>

        <div class="daily-challenge">
          <span>每日 1 题</span>
          <strong>冒泡排序：相邻元素交换</strong>
          <i></i>
        </div>

        <div class="study-stats">
          <div>
            <span>连续提交</span>
            <strong>0 天</strong>
          </div>
          <div>
            <span>本月解决</span>
            <strong>1 题</strong>
          </div>
          <div>
            <span>每日一题</span>
            <strong>0 连胜</strong>
          </div>
        </div>

        <div class="heatmap">
          <span
            v-for="(level, index) in heatmapLevels"
            :key="index"
            :class="`level-${level}`"
          ></span>
        </div>
        <div class="month-labels">
          <span>3月</span>
          <span>4月</span>
          <span>5月</span>
          <span>6月</span>
        </div>
        <button class="analysis-button">进展分析</button>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { algorithmApi, type Algorithm, type AlgorithmCategory } from '../api/algorithm'

const categories = ref<AlgorithmCategory[]>([])
const algorithms = ref<Algorithm[]>([])
const selectedCategoryId = ref<number | undefined>()
const fallbackCategories: AlgorithmCategory[] = [
  { id: 1, name: '排序算法', sortOrder: 1 },
  { id: 2, name: '图算法', sortOrder: 2 }
]
const fallbackAlgorithms: Algorithm[] = [
  {
    id: 1,
    categoryId: 1,
    name: '冒泡排序',
    slug: 'bubble-sort',
    difficulty: '入门',
    summary: '通过相邻元素比较和交换，将较大元素逐步移动到序列末尾。',
    description: '冒泡排序适合理解比较、交换和多轮扫描的基本思想。'
  },
  {
    id: 2,
    categoryId: 1,
    name: '归并排序',
    slug: 'merge-sort',
    difficulty: '中等',
    summary: '采用分治思想，将数组拆分后分别排序，再合并为有序序列。',
    description: '归并排序用于展示递归拆分、子问题求解和有序合并过程。'
  },
  {
    id: 3,
    categoryId: 2,
    name: 'Dijkstra 最短路径',
    slug: 'dijkstra',
    difficulty: '中等',
    summary: '从起点出发，逐步确定到其他节点的最短距离。',
    description: 'Dijkstra 算法用于展示图中距离松弛和候选距离更新过程。'
  },
  {
    id: 4,
    categoryId: 2,
    name: '图着色',
    slug: 'graph-coloring',
    difficulty: '进阶',
    summary: '为图中节点分配颜色，使相邻节点颜色不同。',
    description: '图着色用于展示约束满足和回溯搜索的基本过程。'
  }
]
const weekDays = [
  { label: 'TUE', date: '23' },
  { label: 'WED', date: '24' },
  { label: 'THU', date: '25' },
  { label: 'FRI', date: '26' },
  { label: 'SAT', date: '27' },
  { label: 'SUN', date: '28' }
]
const heatmapLevels = [0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 1, 0, 4, 0, 0, 0, 0, 2, 0, 3, 0, 0, 2, 0, 4, 0, 0, 0, 1, 0, 3, 4, 2, 0, 0, 4, 0, 0, 3, 0, 0, 0, 2, 0, 0, 4, 1, 0, 0, 0, 0, 3, 0, 2, 0, 0]

function visualBars(slug: string) {
  if (slug === 'merge-sort') {
    return [44, 82, 62, 108, 72]
  }
  if (slug === 'dijkstra') {
    return [70, 40, 100, 58, 86]
  }
  if (slug === 'graph-coloring') {
    return [62, 92, 52, 112, 76]
  }
  return [96, 58, 118, 72, 104]
}

async function loadData() {
  try {
    categories.value = await algorithmApi.categories()
    algorithms.value = await algorithmApi.algorithms(selectedCategoryId.value)
  } catch {
    categories.value = fallbackCategories
    algorithms.value = filterFallbackAlgorithms(selectedCategoryId.value)
  }
}

async function selectCategory(categoryId: number | undefined) {
  selectedCategoryId.value = categoryId
  try {
    algorithms.value = await algorithmApi.algorithms(categoryId)
  } catch {
    algorithms.value = filterFallbackAlgorithms(categoryId)
  }
}

function filterFallbackAlgorithms(categoryId: number | undefined) {
  return categoryId === undefined
    ? fallbackAlgorithms
    : fallbackAlgorithms.filter((algorithm) => algorithm.categoryId === categoryId)
}

onMounted(loadData)
</script>
