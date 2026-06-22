<template>
  <section class="page narrow">
    <h1>个人中心</h1>
    <div v-if="userStore.user" class="profile-block">
      <p>用户名：{{ userStore.user.username }}</p>
      <p>昵称：{{ userStore.user.nickname }}</p>
      <button class="ghost-button" @click="logout">退出登录</button>
    </div>
    <RouterLink v-else class="primary-action" to="/auth">去登录</RouterLink>
  </section>
</template>

<script setup lang="ts">
import { RouterLink, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const router = useRouter()

async function logout() {
  await userStore.logout()
  router.push('/')
}
</script>
