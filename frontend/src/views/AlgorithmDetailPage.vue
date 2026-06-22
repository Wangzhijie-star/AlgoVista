<template>
  <section class="detail-layout" v-if="algorithm">
    <aside class="algorithm-info detail-sidebar">
      <span class="difficulty">{{ algorithm.difficulty }}</span>
      <h1>{{ algorithm.name }}</h1>

      <div class="code-section">
        <h2>代码</h2>
        <pre class="code-panel"><code
          v-for="(line, index) in displayCodeLines"
          :key="`${line}-${index}`"
          :class="{ highlight: currentStep?.codeLine === index + 1 }"
        >{{ line }}
</code></pre>
      </div>

      <div class="intro-section">
        <h2>算法介绍</h2>
        <p>{{ algorithm.description }}</p>
      </div>

      <div class="step-card" v-if="currentStep">
        <span>{{ currentStep.operationType }}</span>
        <strong>Step {{ currentStep.stepIndex + 1 }} / {{ steps.length }}</strong>
        <p>{{ currentStep.description }}</p>
      </div>

      <div class="player-controls">
        <button @click="previousStep" :disabled="stepIndex <= 0">上一步</button>
        <button @click="nextStep" :disabled="stepIndex >= steps.length - 1">下一步</button>
        <button @click="resetPlayer">重置</button>
      </div>

      <button class="primary-action" @click="loadVisualization">开始演示</button>
      <p v-if="error" class="error-text">{{ error }}</p>
    </aside>

    <div class="visual-panel demo-stage">
      <div class="demo-header">
        <div>
          <p class="eyebrow">Visualization Stage</p>
          <h2>算法演示动画区域</h2>
        </div>
        <span v-if="currentStep">Step {{ currentStep.stepIndex + 1 }}</span>
      </div>

      <div class="bars" v-if="currentStep?.arrayState.length">
        <div
          v-for="(value, index) in currentStep.arrayState"
          :key="index"
          class="bar"
          :class="{ active: currentStep.activeIndexes.includes(index) }"
          :style="{ height: `${value * 28}px` }"
        >
          {{ value }}
        </div>
      </div>
      <p v-else class="empty-state">点击开始演示加载可视化步骤。</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { algorithmApi, type Algorithm, type VisualizationStep } from '../api/algorithm'

const route = useRoute()
const router = useRouter()
const algorithm = ref<Algorithm | null>(null)
const steps = ref<VisualizationStep[]>([])
const codeLines = ref<string[]>([])
const stepIndex = ref(0)
const error = ref('')
const currentStep = computed(() => steps.value[stepIndex.value])
const displayCodeLines = computed(() => codeLines.value.length ? codeLines.value : [
  'for i from 0 to n - 2',
  '  for j from 0 to n - i - 2',
  '    if arr[j] > arr[j + 1]',
  '      swap arr[j] and arr[j + 1]'
])
const fallbackAlgorithms: Record<number, Algorithm> = {
  1: {
    id: 1,
    categoryId: 1,
    name: '冒泡排序',
    slug: 'bubble-sort',
    difficulty: '入门',
    summary: '通过相邻元素比较和交换，将较大元素逐步移动到序列末尾。',
    description: '冒泡排序适合理解比较、交换和多轮扫描的基本思想，是 AlgoVista 第一阶段优先打通的完整可视化算法。'
  },
  2: {
    id: 2,
    categoryId: 1,
    name: '归并排序',
    slug: 'merge-sort',
    difficulty: '中等',
    summary: '采用分治思想，将数组拆分后分别排序，再合并为有序序列。',
    description: '归并排序用于展示递归拆分、子问题求解和有序合并过程。'
  },
  3: {
    id: 3,
    categoryId: 2,
    name: 'Dijkstra 最短路径',
    slug: 'dijkstra',
    difficulty: '中等',
    summary: '从起点出发，逐步确定到其他节点的最短距离。',
    description: 'Dijkstra 算法用于展示图中距离松弛、已确定集合和候选距离更新过程。'
  },
  4: {
    id: 4,
    categoryId: 2,
    name: '图着色',
    slug: 'graph-coloring',
    difficulty: '进阶',
    summary: '为图中节点分配颜色，使相邻节点颜色不同。',
    description: '图着色用于展示约束满足和回溯搜索的基本过程。'
  }
}

async function loadAlgorithm() {
  const id = Number(route.params.id)
  try {
    algorithm.value = await algorithmApi.detail(id)
  } catch {
    algorithm.value = fallbackAlgorithms[id] ?? fallbackAlgorithms[1]
  }
}

async function loadVisualization() {
  error.value = ''
  try {
    const result = await algorithmApi.visualization(Number(route.params.id))
    steps.value = result.steps
    codeLines.value = result.codeLines
    stepIndex.value = 0
  } catch (err) {
    error.value = '请先登录后再使用算法可视化。'
    if (err instanceof Error && err.message !== '请先登录') {
      error.value = err.message
    }
    router.push('/auth')
  }
}

function nextStep() {
  stepIndex.value = Math.min(stepIndex.value + 1, steps.value.length - 1)
}

function previousStep() {
  stepIndex.value = Math.max(stepIndex.value - 1, 0)
}

function resetPlayer() {
  stepIndex.value = 0
}

onMounted(loadAlgorithm)
</script>
