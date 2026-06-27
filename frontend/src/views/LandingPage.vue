<template>
  <section class="landing-page" @pointermove="handlePointerMove" @pointerleave="resetPointer">
    <div class="landing-bg"></div>
    <div class="landing-content">
      <p class="eyebrow">Algorithm Visualization</p>
      <h1>每一步变化，都有迹可循。</h1>
      <p class="landing-copy">
        通过可视化演示跟踪数据状态，理解算法如何一步步得到结果。
      </p>
      <div class="hero-actions">
        <RouterLink class="primary-action glow-action" to="/home">开始学习</RouterLink>
        <RouterLink class="secondary-action" to="/home">查看算法库</RouterLink>
      </div>
    </div>

    <div class="landing-cube-stage" ref="stageRef" aria-label="算法数据柱鼠标悬浮动画">
      <div
        v-for="(cube, index) in cubes"
        :key="cube.value"
        class="hover-cube"
        :class="{ hot: activeIndex === index, near: Math.abs(activeIndex - index) === 1 }"
        :style="{
          '--h': `${cube.height}px`,
          '--delay': `${index * 50}ms`
        }"
      >
        <div class="cuboid-face cuboid-front"><span>{{ cube.value }}</span></div>
        <div class="cuboid-face cuboid-right"></div>
        <div class="cuboid-face cuboid-top"></div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'

const stageRef = ref<HTMLElement | null>(null)
const activeIndex = ref(2)
const cubes = [
  { value: 9, height: 230 },
  { value: 5, height: 142 },
  { value: 16, height: 330 },
  { value: 8, height: 190 },
  { value: 11, height: 246 },
  { value: 6, height: 158 }
]

function handlePointerMove(event: PointerEvent) {
  const stage = stageRef.value
  if (!stage) {
    return
  }

  const rect = stage.getBoundingClientRect()
  const ratio = Math.min(Math.max((event.clientX - rect.left) / rect.width, 0), 0.999)
  activeIndex.value = Math.floor(ratio * cubes.length)
}

function resetPointer() {
  activeIndex.value = 2
}
</script>
