<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">AlgoVista</RouterLink>
      <nav>
        <RouterLink to="/home">算法</RouterLink>
        <RouterLink v-if="userStore.user" to="/profile">个人中心</RouterLink>
        <RouterLink v-else to="/auth">登录</RouterLink>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { useUserStore } from './stores/user'

const userStore = useUserStore()

onMounted(() => {
  userStore.fetchMe()
})
</script>
