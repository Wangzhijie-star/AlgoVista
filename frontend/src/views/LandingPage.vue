<template>
  <section class="hero tech-hero">
    <div class="hero-grid"></div>
    <div class="hero-orbit hero-orbit-one"></div>
    <div class="hero-orbit hero-orbit-two"></div>

    <div class="hero-content">
      <p class="eyebrow">ALGORITHM VISUALIZATION</p>
      <h1>看见算法，理解每一步。</h1>
      <p class="hero-copy">用动态柱形、代码高亮和步骤拆解，把抽象算法转化为可观察、可播放、可复盘的学习体验。</p>
      <div class="hero-actions">
        <RouterLink class="primary-action glow-action" to="/home">开启旅程</RouterLink>
        <RouterLink class="secondary-action" to="/home">查看算法库</RouterLink>
      </div>
    </div>

    <div class="cube-stage" aria-label="算法可视化动态预览">
      <div class="cube-cluster">
        <div
          v-for="(height, index) in barHeights"
          :key="index"
          class="data-cube"
          :class="`cube-${index + 1}`"
          :style="{ '--cube-height': `${height}px`, '--delay': `${index * 90}ms` }"
        >
          <span>{{ height / 8 }}</span>
        </div>
      </div>
      <div class="preview-console">
        <div class="console-dot"></div>
        <div class="console-lines">
          <span>compare(arr[i], arr[i + 1])</span>
          <span class="active-line">swap when left &gt; right</span>
          <span>render current step</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'

const barHeights = [104, 64, 136, 88, 152, 112, 72, 128, 96]
</script>
