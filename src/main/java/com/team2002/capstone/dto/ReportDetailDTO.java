package com.team2002.capstone.dto;

import com.team2002.capstone.domain.enums.ReportStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class ReportDetailDTO {
    private Long id;
    private String title;
    private String content;
    private ReportStatus status;
    private String statusDescription;
    private String reporterNickname;
    private String reportedNickname;
    private String adminResponse; // 관리자 답변
    private LocalDateTime createdAt;
}
