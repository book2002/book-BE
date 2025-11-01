package com.team2002.capstone.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupResponseDTO {
    private Long groupId;
    private String name;
    private String description;
    private String goal;
    private String ownerName;
    private int maxMembers;
    private int currentMembers;
    private boolean isJoined;
}
