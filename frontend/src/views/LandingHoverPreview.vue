<template>
  <section class="hover-preview-page" @pointermove="handlePointerMove" @pointerleave="resetPointer">
    <div class="hover-preview-bg"></div>
    <div class="hover-preview-copy">
      <p class="eyebrow">Landing Animation Preview</p>
      <h1>鼠标掠过，数据块被唤醒。</h1>
      <p>这是独立预览页，用来确认开始界面的方块跳动效果。确认后再替换项目正式首页。</p>
      <RouterLink class="primary-action glow-action" to="/home">返回算法库</RouterLink>
    </div>

    <div class="hover-cube-stage" ref="stageRef">
      <div
        v-for="(cube, index) in cubes"
        :key="index"
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
  activeIndex.value = 4
}
</script>
