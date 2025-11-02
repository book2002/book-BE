package com.team2002.capstone.dto;

import lombok.Getter;

@Getter
public class ReportRequestDTO {
    private Long reportedProfileId; // 신고 대상 ID
    private String title;
    private String content;
}
