package com.team2002.capstone.domain;

import com.team2002.capstone.domain.common.BaseEntity;
import com.team2002.capstone.domain.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Report extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Profile reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_profile_id")
    private Profile reportedProfile;

    private String adminResponse; // 관리자 답변

    public void updateStatusAndResponse(ReportStatus status, String adminResponse) {
        this.status = status;
        this.adminResponse = adminResponse;
    }

    public Report() {
    }
}
