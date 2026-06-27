<template>
  <section class="page profile-page">
    <div class="section-heading">
      <div>
        <p class="eyebrow">Profile</p>
        <h1>个人中心</h1>
        <p class="page-subtitle">查看登录信息、收藏算法和后续学习数据。</p>
      </div>
    </div>

    <div v-if="userStore.user" class="profile-layout">
      <div class="profile-block">
        <h2>账号信息</h2>
        <p>用户名：{{ userStore.user.username }}</p>
        <p>昵称：{{ userStore.user.nickname }}</p>
        <button class="ghost-button" @click="logout">退出登录</button>
      </div>

      <div class="profile-block">
        <div class="profile-block-header">
          <h2>我的收藏</h2>
          <button class="link-button" @click="loadFavorites">刷新</button>
        </div>

        <div v-if="favorites.length" class="favorite-list">
          <RouterLink
            v-for="algorithm in favorites"
            :key="algorithm.id"
            class="favorite-list-item"
            :to="`/algorithms/${algorithm.id}`"
          >
            <span>{{ algorithm.difficulty }}</span>
            <strong>{{ algorithm.name }}</strong>
            <p>{{ algorithm.summary }}</p>
          </RouterLink>
        </div>
        <p v-else class="empty-profile-state">还没有收藏算法，可以先去算法库收藏一个。</p>
      </div>
    </div>

    <RouterLink v-else class="primary-action" to="/auth">去登录</RouterLink>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import type { Algorithm } from '../api/algorithm'
import { favoriteApi } from '../api/favorite'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const router = useRouter()
const favorites = ref<Algorithm[]>([])

async function loadFavorites() {
  if (!userStore.user) {
    favorites.value = []
    return
  }
  try {
    favorites.value = await favoriteApi.list()
  } catch {
    favorites.value = []
  }
}

async function logout() {
  await userStore.logout()
  router.push('/')
}

onMounted(async () => {
  await userStore.fetchMe()
  await loadFavorites()
})
</script>
