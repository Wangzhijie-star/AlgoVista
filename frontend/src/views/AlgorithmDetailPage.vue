<template>
  <section class="detail-layout" v-if="algorithm">
    <aside class="algorithm-info">
      <span class="difficulty">{{ algorithm.difficulty }}</span>
      <h1>{{ algorithm.name }}</h1>
      <p>{{ algorithm.description }}</p>
      <button class="primary-action" @click="loadVisualization">开始演示</button>
      <p v-if="error" class="error-text">{{ error }}</p>
    </aside>

    <div class="visual-panel">
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

      <div class="player-controls">
        <button @click="previousStep" :disabled="stepIndex <= 0">上一步</button>
        <button @click="nextStep" :disabled="stepIndex >= steps.length - 1">下一步</button>
        <button @click="resetPlayer">重置</button>
      </div>

      <p class="step-description" v-if="currentStep">{{ currentStep.description }}</p>

      <pre class="code-panel" v-if="codeLines.length"><code
        v-for="(line, index) in codeLines"
        :key="line"
        :class="{ highlight: currentStep?.codeLine === index + 1 }"
      >{{ line }}
</code></pre>
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

async function loadAlgorithm() {
  algorithm.value = await algorithmApi.detail(Number(route.params.id))
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
