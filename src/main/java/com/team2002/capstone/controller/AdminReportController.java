package com.team2002.capstone.controller;

import com.team2002.capstone.dto.ReportDetailDTO;
import com.team2002.capstone.dto.ReportResponseDTO;
import com.team2002.capstone.dto.ReportUpdateDTO;
import com.team2002.capstone.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminReportController {
    private final ReportService reportService;

    @Operation(summary = "관리자: 전체 신고 목록 조회")
    @GetMapping
    public ResponseEntity<List<ReportResponseDTO>> getReports() {
        List<ReportResponseDTO> reports = reportService.getAllReports();
        return ResponseEntity.status(HttpStatus.OK).body(reports);
    }

    @Operation(summary = "관리자: 신고 처리 및 답변 등록")
    @PatchMapping("/{reportId}")
    public ResponseEntity<ReportDetailDTO> updateReportStatus(
            @PathVariable Long reportId,
            @Validated @RequestBody ReportUpdateDTO updateDTO
    ) {
        ReportDetailDTO reportDetailDTO = reportService.updateReport(reportId, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(reportDetailDTO);
    }
}
