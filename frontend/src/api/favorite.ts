import { http, request } from './http'
import type { Algorithm } from './algorithm'

export interface FavoriteStatus {
  favorite: boolean
}

export const favoriteApi = {
  list() {
    return request<Algorithm[]>(http.get('/favorites'))
  },
  status(algorithmId: number) {
    return request<FavoriteStatus>(http.get(`/favorites/${algorithmId}`))
  },
  add(algorithmId: number) {
    return request<void>(http.post('/favorites', { algorithmId }))
  },
  remove(algorithmId: number) {
    return request<void>(http.delete(`/favorites/${algorithmId}`))
  }
}
