package com.algovista.study.controller;

import java.time.LocalDate;
import java.util.List;

public record StudyCalendarResponse(
        int currentStreak,
        int totalStudyDays,
        int monthStudyDays,
        int dailyChallengeStreak,
        List<StudyDay> days
) {
    public record StudyDay(LocalDate date, int studyCount) {
    }
}
