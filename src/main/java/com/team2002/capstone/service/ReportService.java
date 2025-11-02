package com.team2002.capstone.service;

import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.domain.Report;
import com.team2002.capstone.domain.enums.ReportStatus;
import com.team2002.capstone.dto.ReportDetailDTO;
import com.team2002.capstone.dto.ReportRequestDTO;
import com.team2002.capstone.dto.ReportResponseDTO;
import com.team2002.capstone.dto.ReportUpdateDTO;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.notification.FcmService;
import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.repository.ProfileRepository;
import com.team2002.capstone.repository.ReportRepository;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final MemberService memberService;
    private final FcmService fcmService;

    @Transactional
    public ReportDetailDTO createReport(ReportRequestDTO requestDTO) {
        Profile reporter = getCurrentProfile();
        Profile reportedProfile = profileRepository.findById(requestDTO.getReportedProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Reported profile not found"));
        if (reporter.getId() == reportedProfile.getId()) {
            throw new IllegalArgumentException("자신을 신고할 수 없습니다.");
        }

        Report report = Report.builder()
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .status(ReportStatus.PENDING)
                .reporter(reporter)
                .reportedProfile(reportedProfile)
                .build();
        reportRepository.save(report);

        return ReportDetailDTO.builder()
                .id(report.getId())
                .title(report.getTitle())
                .content(report.getContent())
                .status(report.getStatus())
                .statusDescription(report.getStatus().getDescription())
                .reporterNickname(report.getReporter().getNickname())
                .reportedNickname(report.getReportedProfile().getNickname())
                .adminResponse(report.getAdminResponse())
                .createdAt(report.getCreatedAt())
                .build();
    }

    public List<ReportResponseDTO> getAllMyReports() {
        Profile profile = getCurrentProfile();
        List<Report> reports = reportRepository.findAllByReporterIdOrderByCreatedAtDesc(profile.getId());

        return reports.stream()
                .map(report -> ReportResponseDTO.builder()
                        .id(report.getId())
                        .title(report.getTitle())
                        .statusDescription(report.getStatus().getDescription())
                        .createdAt(report.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public ReportDetailDTO getMyReport(Long id) {
        Profile profile = getCurrentProfile();
        Report report =reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        if (report.getReporter().getId() != profile.getId()) {
            throw new IllegalAccessError("해당 신고 내역을 조회할 권한이 없습니다.");
        }

        return ReportDetailDTO.builder()
                .id(report.getId())
                .title(report.getTitle())
                .content(report.getContent())
                .status(report.getStatus())
                .statusDescription(report.getStatus().getDescription())
                .reporterNickname(report.getReporter().getNickname())
                .reportedNickname(report.getReportedProfile().getNickname())
                .adminResponse(report.getAdminResponse())
                .createdAt(report.getCreatedAt())
                .build();
    }

    /*
    * 관리자용
    */
    public List<ReportResponseDTO> getAllReports() {
        List<Report> reports = reportRepository.findAllByOrderByStatusAscCreatedAtDesc();
        return reports.stream()
                .map(report -> ReportResponseDTO.builder()
                        .id(report.getId())
                        .title(report.getTitle())
                        .statusDescription(report.getStatus().getDescription())
                        .createdAt(report.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDetailDTO updateReport(Long reportId, ReportUpdateDTO requestDTO) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.updateStatusAndResponse(requestDTO.getStatus(), requestDTO.getAdminResponse());

        if (requestDTO.getStatus() == ReportStatus.COMPLETED) {
            Member reportedMember = getCurrentProfile().getMember();
            switch (requestDTO.getActionType()) {
                case SUSPEND:
                    memberService.suspendedMember(reportedMember);
                    log.warn("🚨 Member ID {} 계정 정지 조치 적용됨.", reportedMember.getId());
                    break;
                case WARNING:
                    fcmService.sendNotification(reportedMember.getFcmToken(), "관리자 경고", requestDTO.getAdminResponse());
                    log.info("📢 Member ID {}에게 텍스트 경고 알림 발송됨.", reportedMember.getId());
                    break;
                case NO_ACTION:
                default:
                    log.info("✅ Member ID {}에게 답변 등록됨 (조치 없음).", reportedMember.getId());
                    break;
            }
        }

        return ReportDetailDTO.builder()
                .id(report.getId())
                .title(report.getTitle())
                .content(report.getContent())
                .status(report.getStatus())
                .statusDescription(report.getStatus().getDescription())
                .reporterNickname(report.getReporter().getNickname())
                .reportedNickname(report.getReportedProfile().getNickname())
                .adminResponse(report.getAdminResponse())
                .createdAt(report.getCreatedAt())
                .build();
    }

    private Profile getCurrentProfile() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 사용자의 프로필을 찾을 수 없습니다."));
    }
}
