package com.team2002.capstone.dto;

import com.team2002.capstone.domain.enums.AdminActionType;
import com.team2002.capstone.domain.enums.ReportStatus;
import lombok.Getter;

@Getter
public class ReportUpdateDTO {
    private ReportStatus status;
    private AdminActionType actionType;
    private String adminResponse;
}
