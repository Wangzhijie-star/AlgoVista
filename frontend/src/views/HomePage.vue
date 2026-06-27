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
            <button
              class="favorite-chip"
              :class="{ active: favoriteIds.has(algorithm.id) }"
              @click.prevent.stop="toggleFavorite(algorithm.id)"
            >
              {{ favoriteIds.has(algorithm.id) ? '已收藏' : '收藏' }}
            </button>
            <h2>{{ algorithm.name }}</h2>
            <p>{{ algorithm.summary }}</p>
            <strong>进入演示</strong>
          </RouterLink>
        </div>
      </div>

      <aside class="study-card">
        <div class="week-row">
          <div v-for="day in weekDays" :key="day.label" :class="{ today: day.isToday }">
            <span>{{ day.label }}</span>
            <strong>{{ day.isToday ? '今' : day.date }}</strong>
          </div>
        </div>

        <div class="daily-challenge">
          <span>{{ userStore.user ? '今日记录' : '登录后记录学习' }}</span>
          <strong>{{ todayStudyCount > 0 ? `今日完成 ${todayStudyCount} 题` : '完成一次算法演示后自动打卡' }}</strong>
          <i :class="{ checked: todayStudyCount > 0 }"></i>
        </div>

        <div class="study-stats">
          <div>
            <span>连续学习</span>
            <strong>{{ currentStreak }} 天</strong>
          </div>
          <div>
            <span>本月学习</span>
            <strong>{{ monthStudyDays }} 天</strong>
          </div>
          <div>
            <span>今日完成</span>
            <strong>{{ todayStudyCount }} 题</strong>
          </div>
        </div>

        <div class="heatmap">
          <span
            v-for="(day, index) in heatmapDays"
            :key="index"
            :class="`level-${heatLevel(day.studyCount)}`"
            :title="`${day.date}: ${day.studyCount} 次学习`"
          ></span>
        </div>
        <div class="month-labels">
          <span v-for="month in monthLabels" :key="month">{{ month }}</span>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { algorithmApi, type Algorithm, type AlgorithmCategory } from '../api/algorithm'
import { favoriteApi } from '../api/favorite'
import { studyApi, type StudyCalendar, type StudyDay } from '../api/study'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const router = useRouter()
const categories = ref<AlgorithmCategory[]>([])
const algorithms = ref<Algorithm[]>([])
const selectedCategoryId = ref<number | undefined>()
const studyCalendar = ref<StudyCalendar | null>(null)
const favoriteIds = ref(new Set<number>())
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

const weekDays = computed(() => {
  const labels = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
  const today = new Date()
  const monday = new Date(today)
  const weekday = today.getDay() || 7
  monday.setDate(today.getDate() - weekday + 1)

  return labels.map((label, index) => {
    const date = new Date(monday)
    date.setDate(monday.getDate() + index)
    return {
      label,
      date: date.getDate(),
      isToday: date.toDateString() === today.toDateString()
    }
  })
})
const heatmapDays = computed(() => studyCalendar.value?.days ?? emptyStudyDays())
const currentStreak = computed(() => studyCalendar.value?.currentStreak ?? 0)
const monthStudyDays = computed(() => studyCalendar.value?.monthStudyDays ?? 0)
const todayStudyCount = computed(() => {
  const today = toDateKey(new Date())
  return heatmapDays.value.find((day) => day.date === today)?.studyCount ?? 0
})
const monthLabels = computed(() => {
  const labels = Array.from(new Set(heatmapDays.value.map((day) => `${Number(day.date.slice(5, 7))}月`)))
  return labels.slice(-4)
})

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
  await loadStudyCalendar()
  await loadFavorites()
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

async function loadStudyCalendar() {
  if (!userStore.user) {
    studyCalendar.value = null
    return
  }
  try {
    studyCalendar.value = await studyApi.calendar()
  } catch {
    studyCalendar.value = null
  }
}

async function loadFavorites() {
  if (!userStore.user) {
    favoriteIds.value = new Set()
    return
  }
  try {
    const favorites = await favoriteApi.list()
    favoriteIds.value = new Set(favorites.map((algorithm) => algorithm.id))
  } catch {
    favoriteIds.value = new Set()
  }
}

async function toggleFavorite(algorithmId: number) {
  if (!userStore.user) {
    router.push('/auth')
    return
  }

  const nextIds = new Set(favoriteIds.value)
  if (nextIds.has(algorithmId)) {
    await favoriteApi.remove(algorithmId)
    nextIds.delete(algorithmId)
  } else {
    await favoriteApi.add(algorithmId)
    nextIds.add(algorithmId)
  }
  favoriteIds.value = nextIds
}

function emptyStudyDays(): StudyDay[] {
  const today = new Date()
  const start = new Date(today)
  start.setDate(today.getDate() - 55)
  return Array.from({ length: 56 }, (_, index) => {
    const date = new Date(start)
    date.setDate(start.getDate() + index)
    return { date: toDateKey(date), studyCount: 0 }
  })
}

function toDateKey(date: Date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function heatLevel(studyCount: number) {
  if (studyCount >= 4) {
    return 4
  }
  return studyCount
}

onMounted(async () => {
  await userStore.fetchMe()
  await loadData()
})
</script>
