<template>
  <section class="detail-layout" :class="{ 'method-detail': usesMethodLayout }" v-if="algorithm">
    <aside class="algorithm-info detail-sidebar">
      <span class="difficulty">{{ algorithm.difficulty }}</span>
      <h1>{{ algorithm.name }}</h1>
      <button class="favorite-detail-button" :class="{ active: isFavorite }" @click="toggleFavorite">
        {{ isFavorite ? '已收藏该算法' : '收藏该算法' }}
      </button>

      <div class="code-section">
        <h2>代码</h2>
        <pre class="code-panel" :class="{ 'long-code-panel': usesMethodLayout }"><code
          v-for="(line, index) in displayCodeLines"
          :key="`${line}-${index}`"
          :class="{ highlight: !usesMethodLayout && currentStep?.codeLine === index + 1 }"
        >{{ line }}
</code></pre>
      </div>

      <div class="intro-section">
        <h2>算法介绍</h2>
        <p>{{ algorithm.description }}</p>
      </div>

      <div class="input-section">
        <h2>参考用例</h2>
        <label>
          {{ inputLabel }}
          <textarea v-if="usesGraphInput" v-model="inputText" rows="4" :placeholder="inputPlaceholder"></textarea>
          <input v-else v-model="inputText" :placeholder="inputPlaceholder" />
        </label>
        <p class="input-hint">{{ inputHint }}</p>
        <p v-if="inputError" class="error-text">{{ inputError }}</p>
      </div>
    </aside>

    <div class="visual-panel demo-stage">
      <div class="demo-header">
        <div>
          <p class="eyebrow">Visualization Stage</p>
          <h2>{{ stageTitle }}</h2>
        </div>
        <span v-if="currentStep">Step {{ currentStep.stepIndex + 1 }}</span>
      </div>

      <div v-if="isDijkstra" class="dijkstra-visualization">
        <div v-if="activeGraphNodes.length" class="graph-stage">
          <svg viewBox="0 0 720 320" role="img" aria-label="Dijkstra 图结构演示">
            <g v-for="edge in dijkstraEdges" :key="`${edge.from}-${edge.to}-${edge.weight}`">
              <line
                :x1="nodePosition(edge.from).x"
                :y1="nodePosition(edge.from).y"
                :x2="nodePosition(edge.to).x"
                :y2="nodePosition(edge.to).y"
                :class="['graph-edge', { active: isCheckingEdge(edge) }]"
              />
              <text
                class="edge-weight"
                :x="(nodePosition(edge.from).x + nodePosition(edge.to).x) / 2"
                :y="(nodePosition(edge.from).y + nodePosition(edge.to).y) / 2 - 8"
              >
                {{ edge.weight }}
              </text>
            </g>
            <g v-for="node in dijkstraNodes" :key="node">
              <circle
                :cx="nodePosition(node).x"
                :cy="nodePosition(node).y"
                r="26"
                :class="['graph-node', {
                  start: node === dijkstraMeta.start,
                  current: node === dijkstraMeta.currentNode,
                  visited: dijkstraVisited.includes(node),
                  updated: node === dijkstraMeta.updatedNode
                }]"
              />
              <text class="node-label" :x="nodePosition(node).x" :y="nodePosition(node).y + 5">{{ node }}</text>
            </g>
          </svg>
        </div>

        <div v-if="dijkstraNodes.length" class="distance-table-wrap">
          <table class="distance-table">
            <tbody>
              <tr>
                <th>节点编号</th>
                <td v-for="node in dijkstraNodes" :key="`node-${node}`">{{ node }}</td>
              </tr>
              <tr>
                <th>与源点路径长度</th>
                <td
                  v-for="node in dijkstraNodes"
                  :key="`distance-${node}`"
                  :class="{ updated: node === dijkstraMeta.updatedNode, current: node === dijkstraMeta.currentNode }"
                >
                  {{ dijkstraDistances[node] ?? '∞' }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else-if="isGraphColoring" class="coloring-visualization">
        <div v-if="activeGraphNodes.length" class="graph-stage coloring-stage">
          <svg viewBox="0 0 720 320" role="img" aria-label="图着色关系演示">
            <g v-for="edge in coloringEdges" :key="`${edge.from}-${edge.to}`">
              <line
                :x1="nodePosition(edge.from).x"
                :y1="nodePosition(edge.from).y"
                :x2="nodePosition(edge.to).x"
                :y2="nodePosition(edge.to).y"
                :class="['graph-edge', {
                  active: isCurrentColoringEdge(edge),
                  conflict: isConflictEdge(edge)
                }]"
              />
            </g>
            <g v-for="node in coloringNodes" :key="node">
              <circle
                :cx="nodePosition(node).x"
                :cy="nodePosition(node).y"
                r="28"
                :class="['graph-node', 'coloring-node', {
                  current: node === coloringMeta.currentNode,
                  conflict: node === coloringMeta.conflictNode,
                  updated: node === coloringMeta.updatedNode,
                  colored: Boolean(coloringColors[node])
                }]"
                :style="{ fill: nodeFill(node) }"
              />
              <text class="node-label" :x="nodePosition(node).x" :y="nodePosition(node).y + 5">{{ node }}</text>
            </g>
          </svg>
        </div>

        <div class="coloring-summary">
          <div class="palette-row">
            <span
              v-for="color in coloringPalette"
              :key="color"
              class="palette-chip"
              :class="{ trying: color === coloringMeta.tryingColor }"
              :style="{ '--chip-color': colorToCss(color) }"
            >
              {{ color }}
            </span>
          </div>

          <div v-if="coloringNodes.length" class="distance-table-wrap">
            <table class="distance-table coloring-table">
              <tbody>
                <tr>
                  <th>节点编号</th>
                  <td v-for="node in coloringNodes" :key="`color-node-${node}`">{{ node }}</td>
                </tr>
                <tr>
                  <th>当前颜色</th>
                  <td
                    v-for="node in coloringNodes"
                    :key="`color-value-${node}`"
                    :class="{ updated: node === coloringMeta.updatedNode, current: node === coloringMeta.currentNode }"
                  >
                    {{ coloringColors[node] ?? '未染色' }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="bars" v-else-if="currentStep?.arrayState.length">
        <div
          v-for="(value, index) in currentStep.arrayState"
          :key="index"
          class="bar"
          :class="{ active: currentStep.activeIndexes.includes(index) }"
          :style="{ height: `${barHeight(value)}px` }"
        >
          {{ value }}
        </div>
      </div>
      <p v-else class="empty-state">点击开始演示加载可视化步骤。</p>

      <div class="visual-footer">
        <div class="step-card" v-if="currentStep">
          <span>{{ currentStep.operationType }}</span>
          <strong>Step {{ currentStep.stepIndex + 1 }} / {{ steps.length }}</strong>
          <p>{{ currentStep.description }}</p>
          <p v-if="recordMessage" class="record-message">{{ recordMessage }}</p>
        </div>
        <div class="player-controls">
          <button @click="previousStep" :disabled="stepIndex <= 0">上一步</button>
          <button @click="nextStep" :disabled="stepIndex >= steps.length - 1">下一步</button>
          <button @click="resetPlayer">重置</button>
          <button class="primary-action inline-action" @click="loadVisualization">开始演示</button>
        </div>
        <p v-if="error" class="error-text">{{ error }}</p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { algorithmApi, type Algorithm, type VisualizationStep } from '../api/algorithm'
import { favoriteApi } from '../api/favorite'
import { studyApi } from '../api/study'

interface GraphEdge {
  from: string
  to: string
  weight?: number
}

interface Point {
  x: number
  y: number
}

interface DijkstraMeta {
  nodes?: string[]
  edges?: GraphEdge[]
  start?: string
  currentNode?: string
  checkingNode?: string
  checkingEdge?: GraphEdge | null
  visitedNodes?: string[]
  distances?: Record<string, string | number>
  updatedNode?: string | null
}

interface ColoringMeta {
  nodes?: string[]
  edges?: GraphEdge[]
  colors?: Record<string, string>
  palette?: string[]
  currentNode?: string | null
  tryingColor?: string | null
  conflictNode?: string | null
  updatedNode?: string | null
  coloredNodes?: string[]
}

const route = useRoute()
const router = useRouter()
const algorithm = ref<Algorithm | null>(null)
const steps = ref<VisualizationStep[]>([])
const codeLines = ref<string[]>([])
const stepIndex = ref(0)
const error = ref('')
const inputText = ref('5,3,8,4,2')
const inputError = ref('')
const recordMessage = ref('')
const hasRecordedCurrentRun = ref(false)
const isFavorite = ref(false)
const currentStep = computed(() => steps.value[stepIndex.value])
const isDijkstra = computed(() => algorithm.value?.slug === 'dijkstra')
const isGraphColoring = computed(() => algorithm.value?.slug === 'graph-coloring')
const usesGraphInput = computed(() => isDijkstra.value || isGraphColoring.value)
const usesMethodLayout = computed(() => ['bubble-sort', 'merge-sort', 'dijkstra', 'graph-coloring'].includes(algorithm.value?.slug ?? ''))
const stageTitle = computed(() => {
  if (isDijkstra.value) {
    return 'Dijkstra 路径演示'
  }
  if (isGraphColoring.value) {
    return '图着色演示'
  }
  return '算法演示动画区域'
})
const displayCodeLines = computed(() => codeLines.value.length ? codeLines.value : [
  'public int[] bubbleSort(int[] nums) {',
  '    // 点击开始演示后加载完整方法代码',
  '    return nums;',
  '}'
])
const inputLabel = computed(() => {
  if (isDijkstra.value) {
    return '输入图结构'
  }
  if (isGraphColoring.value) {
    return '输入节点、边和颜色'
  }
  return '输入数组'
})
const inputPlaceholder = computed(() => {
  if (isDijkstra.value) {
    return '起点:A;边:A-B-5,A-C-2,B-D-6,C-D-4,C-E-7,D-E-1'
  }
  if (isGraphColoring.value) {
    return '节点:A,B,C,D,E;边:A-B,A-C,B-D,C-D,C-E,D-E;颜色:红,蓝,绿'
  }
  return '例如：5,3,8,4,2'
})
const inputHint = computed(() => {
  if (isDijkstra.value) {
    return '格式：起点:A;边:A-B-5,A-C-2。节点使用大写字母，边权值为 1-99 的正整数。'
  }
  if (isGraphColoring.value) {
    return '格式：节点:A,B,C;边:A-B,B-C;颜色:红,蓝,绿。节点数量 3-8 个，边数量 2-16 条，颜色数量 2-6 种。'
  }
  return '格式：2-12 个整数，用英文逗号分隔；每个数范围 1-20。'
})

const dijkstraMeta = computed<DijkstraMeta>(() => (currentStep.value?.metadata ?? {}) as DijkstraMeta)
const dijkstraNodes = computed(() => dijkstraMeta.value.nodes ?? [])
const dijkstraEdges = computed(() => dijkstraMeta.value.edges ?? [])
const dijkstraVisited = computed(() => dijkstraMeta.value.visitedNodes ?? [])
const dijkstraDistances = computed(() => dijkstraMeta.value.distances ?? {})

const coloringMeta = computed<ColoringMeta>(() => (currentStep.value?.metadata ?? {}) as ColoringMeta)
const coloringNodes = computed(() => coloringMeta.value.nodes ?? [])
const coloringEdges = computed(() => coloringMeta.value.edges ?? [])
const coloringColors = computed(() => coloringMeta.value.colors ?? {})
const coloringPalette = computed(() => coloringMeta.value.palette ?? [])
const activeGraphNodes = computed(() => isGraphColoring.value ? coloringNodes.value : dijkstraNodes.value)
const nodePositions = computed<Record<string, Point>>(() => {
  const nodes = activeGraphNodes.value
  const centerX = 360
  const centerY = 160
  const radiusX = 250
  const radiusY = 105
  return nodes.reduce<Record<string, Point>>((positions, node, index) => {
    const angle = nodes.length === 1 ? 0 : (Math.PI * 2 * index) / nodes.length - Math.PI / 2
    positions[node] = {
      x: Math.round(centerX + Math.cos(angle) * radiusX),
      y: Math.round(centerY + Math.sin(angle) * radiusY)
    }
    return positions
  }, {})
})

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
    description: '图着色问题要求给图中的每个节点分配颜色，并保证任意一条边连接的两个节点颜色不同。它常用于课程表安排、地图着色、寄存器分配等约束满足场景。本演示会按照节点顺序逐个尝试颜色，检查相邻节点是否冲突，并在每次接受或拒绝颜色时解释原因。'
  }
}

async function loadAlgorithm() {
  const id = Number(route.params.id)
  try {
    algorithm.value = await algorithmApi.detail(id)
  } catch {
    algorithm.value = fallbackAlgorithms[id] ?? fallbackAlgorithms[1]
  }
  if (isDijkstra.value) {
    inputText.value = '起点:A;边:A-B-5,A-C-2,B-D-6,C-D-4,C-E-7,D-E-1'
  } else if (isGraphColoring.value) {
    inputText.value = '节点:A,B,C,D,E;边:A-B,A-C,B-D,C-D,C-E,D-E;颜色:红,蓝,绿'
  } else {
    inputText.value = '5,3,8,4,2'
  }
  await loadFavoriteStatus(id)
}

async function loadFavoriteStatus(algorithmId: number) {
  try {
    const result = await favoriteApi.status(algorithmId)
    isFavorite.value = result.favorite
  } catch {
    isFavorite.value = false
  }
}

async function toggleFavorite() {
  const algorithmId = Number(route.params.id)
  try {
    if (isFavorite.value) {
      await favoriteApi.remove(algorithmId)
      isFavorite.value = false
    } else {
      await favoriteApi.add(algorithmId)
      isFavorite.value = true
    }
  } catch {
    router.push('/auth')
  }
}

async function loadVisualization() {
  error.value = ''
  inputError.value = ''
  recordMessage.value = ''
  const payload = parseInput()
  if (!payload) {
    return
  }
  try {
    const result = await algorithmApi.visualization(Number(route.params.id), payload)
    steps.value = result.steps
    codeLines.value = result.codeLines
    stepIndex.value = 0
    hasRecordedCurrentRun.value = false
  } catch (err) {
    error.value = '请先登录后再使用算法可视化。'
    if (err instanceof Error && err.message !== '请先登录') {
      error.value = err.message
    }
    if (error.value.includes('登录')) {
      router.push('/auth')
    }
  }
}

async function nextStep() {
  stepIndex.value = Math.min(stepIndex.value + 1, steps.value.length - 1)
  await recordIfFinished()
}

function previousStep() {
  stepIndex.value = Math.max(stepIndex.value - 1, 0)
}

function resetPlayer() {
  stepIndex.value = 0
  recordMessage.value = ''
  hasRecordedCurrentRun.value = false
}

function parseInput(): number[] | { graphText: string } | null {
  if (isDijkstra.value) {
    if (!inputText.value.includes('起点') || !inputText.value.includes('边')) {
      inputError.value = '请输入类似“起点:A;边:A-B-5,A-C-2”的图结构。'
      return null
    }
    return { graphText: inputText.value }
  }

  if (isGraphColoring.value) {
    if (!inputText.value.includes('节点') || !inputText.value.includes('边')) {
      inputError.value = '请输入类似“节点:A,B,C;边:A-B,B-C;颜色:红,蓝,绿”的图结构。'
      return null
    }
    return { graphText: inputText.value }
  }

  const values = inputText.value
    .split(',')
    .map((part) => part.trim())
    .filter(Boolean)

  if (values.length < 2 || values.length > 12) {
    inputError.value = '请输入 2 到 12 个整数。'
    return null
  }

  const numbers = values.map(Number)
  if (numbers.some((value) => !Number.isInteger(value))) {
    inputError.value = '用例只能包含整数，并使用英文逗号分隔。'
    return null
  }
  if (numbers.some((value) => value < 1 || value > 20)) {
    inputError.value = '每个数字必须在 1 到 20 之间。'
    return null
  }
  return numbers
}

function barHeight(value: number) {
  return Math.max(36, Math.min(value * 28, 420))
}

function nodePosition(node: string) {
  return nodePositions.value[node] ?? { x: 360, y: 160 }
}

function isCheckingEdge(edge: GraphEdge) {
  const checkingEdge = dijkstraMeta.value.checkingEdge
  if (!checkingEdge) {
    return false
  }
  return (edge.from === checkingEdge.from && edge.to === checkingEdge.to)
    || (edge.from === checkingEdge.to && edge.to === checkingEdge.from)
}

function isCurrentColoringEdge(edge: GraphEdge) {
  const currentNode = coloringMeta.value.currentNode
  return Boolean(currentNode && (edge.from === currentNode || edge.to === currentNode))
}

function isConflictEdge(edge: GraphEdge) {
  const currentNode = coloringMeta.value.currentNode
  const conflictNode = coloringMeta.value.conflictNode
  if (!currentNode || !conflictNode) {
    return false
  }
  return (edge.from === currentNode && edge.to === conflictNode)
    || (edge.from === conflictNode && edge.to === currentNode)
}

function nodeFill(node: string) {
  const color = coloringColors.value[node]
  if (color) {
    return colorToCss(color)
  }
  if (node === coloringMeta.value.currentNode && coloringMeta.value.tryingColor) {
    return colorToCss(coloringMeta.value.tryingColor)
  }
  return '#ffffff'
}

function colorToCss(color: string) {
  const colorMap: Record<string, string> = {
    红: '#f87171',
    蓝: '#60a5fa',
    绿: '#34d399',
    黄: '#fbbf24',
    紫: '#a78bfa',
    橙: '#fb923c',
    青: '#2dacb5'
  }
  return colorMap[color] ?? color
}

async function recordIfFinished() {
  if (!steps.value.length || stepIndex.value !== steps.value.length - 1 || hasRecordedCurrentRun.value) {
    return
  }
  try {
    await studyApi.record(Number(route.params.id))
    hasRecordedCurrentRun.value = true
    recordMessage.value = '本次演示已完成，今日学习打卡已记录。'
  } catch {
    recordMessage.value = '演示已完成。登录后可记录学习打卡。'
  }
}

onMounted(loadAlgorithm)
</script>
