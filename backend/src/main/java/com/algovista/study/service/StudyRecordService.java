package com.algovista.study.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.algovista.algorithm.service.AlgorithmService;
import com.algovista.study.controller.StudyCalendarResponse;
import com.algovista.study.entity.StudyRecord;
import com.algovista.study.mapper.StudyRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudyRecordService {
    private final StudyRecordMapper studyRecordMapper;
    private final AlgorithmService algorithmService;

    public StudyRecordService(StudyRecordMapper studyRecordMapper, AlgorithmService algorithmService) {
        this.studyRecordMapper = studyRecordMapper;
        this.algorithmService = algorithmService;
    }

    public void record(Long userId, Long algorithmId) {
        algorithmService.getAlgorithm(algorithmId);
        LocalDate today = LocalDate.now();
        StudyRecord exists = studyRecordMapper.selectOne(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getUserId, userId)
                .eq(StudyRecord::getAlgorithmId, algorithmId)
                .eq(StudyRecord::getStudiedDate, today));
        if (exists == null) {
            StudyRecord record = new StudyRecord();
            record.setUserId(userId);
            record.setAlgorithmId(algorithmId);
            record.setStudiedDate(today);
            record.setStudyCount(1);
            studyRecordMapper.insert(record);
        }
    }

    public StudyCalendarResponse getCalendar(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(55);
        var records = studyRecordMapper.selectList(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getUserId, userId)
                .ge(StudyRecord::getStudiedDate, start)
                .le(StudyRecord::getStudiedDate, today)
                .orderByAsc(StudyRecord::getStudiedDate));
        Map<LocalDate, Integer> countByDate = records.stream()
                .collect(Collectors.groupingBy(StudyRecord::getStudiedDate, Collectors.summingInt(StudyRecord::getStudyCount)));
        Set<LocalDate> studiedDates = countByDate.keySet();

        var days = new ArrayList<StudyCalendarResponse.StudyDay>();
        for (int i = 0; i < 56; i++) {
            LocalDate date = start.plusDays(i);
            days.add(new StudyCalendarResponse.StudyDay(date, countByDate.getOrDefault(date, 0)));
        }

        int currentStreak = 0;
        for (LocalDate date = today; studiedDates.contains(date); date = date.minusDays(1)) {
            currentStreak++;
        }
        int monthStudyDays = (int) studiedDates.stream()
                .filter(date -> date.getYear() == today.getYear() && date.getMonth() == today.getMonth())
                .count();

        return new StudyCalendarResponse(currentStreak, studiedDates.size(), monthStudyDays, currentStreak, days);
    }
}
