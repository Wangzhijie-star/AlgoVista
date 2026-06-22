<template>
  <section class="auth-page">
    <form class="auth-panel" @submit.prevent="submit">
      <h1>{{ mode === 'login' ? '登录' : '注册' }}</h1>
      <label>
        用户名
        <input v-model="form.username" autocomplete="username" required minlength="3" />
      </label>
      <label>
        密码
        <input v-model="form.password" autocomplete="current-password" type="password" required minlength="6" />
      </label>
      <label v-if="mode === 'register'">
        昵称
        <input v-model="form.nickname" />
      </label>
      <p v-if="error" class="error-text">{{ error }}</p>
      <button class="primary-action" type="submit">{{ mode === 'login' ? '登录' : '创建账号' }}</button>
      <button class="link-button" type="button" @click="toggleMode">
        {{ mode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
      </button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const mode = ref<'login' | 'register'>('login')
const error = ref('')
const form = reactive({
  username: '',
  password: '',
  nickname: ''
})

function toggleMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  error.value = ''
}

async function submit() {
  error.value = ''
  try {
    if (mode.value === 'login') {
      await userStore.login(form)
    } else {
      await userStore.register(form)
    }
    router.push('/home')
  } catch (err) {
    error.value = err instanceof Error ? err.message : '操作失败'
  }
}
</script>
