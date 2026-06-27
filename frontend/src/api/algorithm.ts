import { http, request } from './http'

export interface AlgorithmCategory {
  id: number
  name: string
  sortOrder: number
}

export interface Algorithm {
  id: number
  categoryId: number
  name: string
  slug: string
  difficulty: string
  summary: string
  description: string
}

export interface VisualizationStep {
  stepIndex: number
  description: string
  operationType: string
  arrayState: number[]
  activeIndexes: number[]
  codeLine: number
  metadata: Record<string, unknown>
}

export interface VisualizationResult {
  algorithmId: number
  algorithmName: string
  input: number[]
  codeLines: string[]
  steps: VisualizationStep[]
}

export const algorithmApi = {
  categories() {
    return request<AlgorithmCategory[]>(http.get('/categories'))
  },
  algorithms(categoryId?: number) {
    return request<Algorithm[]>(http.get('/algorithms', { params: { categoryId } }))
  },
  detail(id: number) {
    return request<Algorithm>(http.get(`/algorithms/${id}`))
  },
  visualization(id: number, payload?: number[] | { graphText: string }) {
    if (Array.isArray(payload) && payload.length) {
      return request<VisualizationResult>(http.post(`/algorithms/${id}/visualization`, { array: payload }))
    }
    if (payload && !Array.isArray(payload)) {
      return request<VisualizationResult>(http.post(`/algorithms/${id}/visualization`, payload))
    }
    return request<VisualizationResult>(http.get(`/algorithms/${id}/visualization`))
  }
}
