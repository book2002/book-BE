package com.team2002.capstone.controller;

import com.team2002.capstone.dto.ReportDetailDTO;
import com.team2002.capstone.dto.ReportRequestDTO;
import com.team2002.capstone.dto.ReportResponseDTO;
import com.team2002.capstone.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "사용자 신고 접수")
    @PostMapping
    public ResponseEntity<ReportDetailDTO> createReport(@Validated @RequestBody ReportRequestDTO reportRequestDTO) {
        ReportDetailDTO reportDetailDTO = reportService.createReport(reportRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportDetailDTO);
    }

    @Operation(summary = "내가 신고한 내역 목록")
    @GetMapping
    public ResponseEntity<List<ReportResponseDTO>> getAllMyReports() {
        List<ReportResponseDTO> reports = reportService.getAllMyReports();
        return ResponseEntity.status(HttpStatus.OK).body(reports);
    }

    @Operation(summary = "내가 신고한 내역 상세 조회 (답변 확인)")
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailDTO> getMyReport(@PathVariable Long reportId) {
        ReportDetailDTO reportDetailDTO = reportService.getMyReport(reportId);
        return ResponseEntity.status(HttpStatus.OK).body(reportDetailDTO);
    }
}
