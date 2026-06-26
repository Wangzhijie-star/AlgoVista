package com.algovista.study.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("study_record")
public class StudyRecord {
    private Long id;
    private Long userId;
    private Long algorithmId;
    private LocalDate studiedDate;
    private Integer studyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Long algorithmId) {
        this.algorithmId = algorithmId;
    }

    public LocalDate getStudiedDate() {
        return studiedDate;
    }

    public void setStudiedDate(LocalDate studiedDate) {
        this.studiedDate = studiedDate;
    }

    public Integer getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(Integer studyCount) {
        this.studyCount = studyCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
