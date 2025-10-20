package com.team2002.capstone.domain;

import com.team2002.capstone.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Discussion extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ReadingGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Profile author;

    private String topicTitle;
    private String topicContent;

    @Column(columnDefinition = "boolean default false")
    private boolean isClosed;

    public Discussion() {
    }
}
