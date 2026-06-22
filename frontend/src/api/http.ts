import axios from 'axios'

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export const http = axios.create({
  baseURL: '/api',
  withCredentials: true
})

export async function request<T>(promise: Promise<{ data: ApiResponse<T> }>) {
  const response = await promise
  if (response.data.code !== 0) {
    throw new Error(response.data.message)
  }
  return response.data.data
}
