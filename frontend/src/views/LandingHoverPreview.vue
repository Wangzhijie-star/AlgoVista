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
          '--x': `${cube.x}px`,
          '--z': `${cube.z}px`,
          '--delay': `${index * 50}ms`
        }"
      >
        <span>{{ cube.value }}</span>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'

const stageRef = ref<HTMLElement | null>(null)
const activeIndex = ref(4)
const cubes = [
  { value: 8, height: 112, x: -250, z: 38 },
  { value: 4, height: 72, x: -180, z: -18 },
  { value: 13, height: 164, x: -110, z: 24 },
  { value: 6, height: 96, x: -40, z: -32 },
  { value: 16, height: 190, x: 30, z: 18 },
  { value: 10, height: 132, x: 100, z: -24 },
  { value: 5, height: 84, x: 170, z: 30 },
  { value: 12, height: 152, x: 240, z: -12 }
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
