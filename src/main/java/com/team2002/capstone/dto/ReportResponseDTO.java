package com.team2002.capstone.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class ReportResponseDTO {
    private Long id;
    private String title;
    private String statusDescription;
    private LocalDateTime createdAt;
}
