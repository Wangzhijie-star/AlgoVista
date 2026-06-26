import { http, request } from './http'

export interface StudyDay {
  date: string
  studyCount: number
}

export interface StudyCalendar {
  currentStreak: number
  totalStudyDays: number
  monthStudyDays: number
  dailyChallengeStreak: number
  days: StudyDay[]
}

export const studyApi = {
  record(algorithmId: number) {
    return request<void>(http.post('/study-records', { algorithmId }))
  },
  calendar() {
    return request<StudyCalendar>(http.get('/study-records/calendar'))
  }
}
