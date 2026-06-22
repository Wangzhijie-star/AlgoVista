import { http, request } from './http'

export interface UserProfile {
  id: number
  username: string
  nickname: string
  avatarUrl?: string
}

export interface AuthPayload {
  username: string
  password: string
  nickname?: string
}

export const authApi = {
  register(payload: AuthPayload) {
    return request<UserProfile>(http.post('/auth/register', payload))
  },
  login(payload: AuthPayload) {
    return request<UserProfile>(http.post('/auth/login', payload))
  },
  logout() {
    return request<void>(http.post('/auth/logout'))
  },
  me() {
    return request<UserProfile>(http.get('/auth/me'))
  }
}
