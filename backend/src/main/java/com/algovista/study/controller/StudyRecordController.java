package com.algovista.study.controller;

import com.algovista.auth.security.SessionKeys;
import com.algovista.common.result.ApiResponse;
import com.algovista.study.service.StudyRecordService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study-records")
public class StudyRecordController {
    private final StudyRecordService studyRecordService;

    public StudyRecordController(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }

    @PostMapping
    public ApiResponse<Void> record(@Valid @RequestBody StudyRecordRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        studyRecordService.record(userId, request.algorithmId());
        return ApiResponse.ok();
    }

    @GetMapping("/calendar")
    public ApiResponse<StudyCalendarResponse> calendar(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        return ApiResponse.ok(studyRecordService.getCalendar(userId));
    }

    public record StudyRecordRequest(@NotNull Long algorithmId) {
    }
}
