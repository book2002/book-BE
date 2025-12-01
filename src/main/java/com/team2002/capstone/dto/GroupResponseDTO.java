package com.team2002.capstone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupResponseDTO {
    private Long groupId;
    private String name;
    private String description;
    private String goal;
    private String ownerName;
    private int maxMembers;
    private int currentMembers;
    private String groupImageUrl;
    private Boolean isJoined;
    private Boolean isOwner;
}
