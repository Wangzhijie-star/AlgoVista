import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../views/LandingPage.vue'
import HomePage from '../views/HomePage.vue'
import AuthPage from '../views/AuthPage.vue'
import AlgorithmDetailPage from '../views/AlgorithmDetailPage.vue'
import ProfilePage from '../views/ProfilePage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: LandingPage },
    { path: '/home', component: HomePage },
    { path: '/auth', component: AuthPage },
    { path: '/algorithms/:id', component: AlgorithmDetailPage },
    { path: '/profile', component: ProfilePage }
  ]
})

export default router
