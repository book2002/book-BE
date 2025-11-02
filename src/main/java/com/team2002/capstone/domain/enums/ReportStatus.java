package com.team2002.capstone.domain.enums;

public enum ReportStatus {
    PENDING("처리 전"), // 신고 접수, 처리 대기 중
    PROCESSING("처리 중"), // 관리자가 확인 중
    COMPLETED("처리 완료"); // 관리자의 조치가 완료됨 (답변 등록)

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
