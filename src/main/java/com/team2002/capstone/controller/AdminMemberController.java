package com.team2002.capstone.controller;

import com.team2002.capstone.dto.AdminMemberResponseDTO;
import com.team2002.capstone.dto.AdminNotificationRequestDTO;
import com.team2002.capstone.notification.NotificationService;
import com.team2002.capstone.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Secured("ROLE_ADMIN")
public class AdminMemberController {
    private final MemberService memberService;
    private final NotificationService notificationService;

    @Operation(summary = "관리자: 전체 회원 목록 조회")
    @GetMapping("/members")
    public ResponseEntity<List<AdminMemberResponseDTO>> getAllMembers() {
        List<AdminMemberResponseDTO> allMembers = memberService.getAllMembers();
        return ResponseEntity.status(HttpStatus.OK).body(allMembers);
    }

    @Operation(summary = "관리자: 특정 회원 상세 정보 조회")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<AdminMemberResponseDTO> getMemberDetail(@PathVariable Long memberId) {
        AdminMemberResponseDTO adminMemberResponseDTO = memberService.getMemberDetail(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(adminMemberResponseDTO);
    }

    @Operation(summary = "관리자: 계정 상태 복구 (정지 해제)")
    @PutMapping("/members/{memberId}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable Long memberId) {
        memberService.activeMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "관리자: 전체 사용자에게 공지 알림 발송")
    @PostMapping("/members/notify-all")
    public ResponseEntity<String> sendGlobalNotification(
            @Validated @RequestBody AdminNotificationRequestDTO adminNotificationRequestDTO
            ) {
        int tokenCount = notificationService.sendGlobalNotification(adminNotificationRequestDTO);
        String message = tokenCount > 0
                ? "총 " + tokenCount + "명의 사용자에게 공지 발송."
                : "발송 대상 토큰이 유효하지 않거나 없음.";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
