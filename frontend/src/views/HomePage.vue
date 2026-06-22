<template>
  <section class="page">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Algorithm Library</p>
        <h1>算法列表</h1>
      </div>
      <button class="ghost-button" @click="loadData">刷新</button>
    </div>

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
        <span>{{ algorithm.difficulty }}</span>
        <h2>{{ algorithm.name }}</h2>
        <p>{{ algorithm.summary }}</p>
      </RouterLink>
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

async function loadData() {
  categories.value = await algorithmApi.categories()
  algorithms.value = await algorithmApi.algorithms(selectedCategoryId.value)
}

async function selectCategory(categoryId: number | undefined) {
  selectedCategoryId.value = categoryId
  algorithms.value = await algorithmApi.algorithms(categoryId)
}

onMounted(loadData)
</script>
