package com.team2002.capstone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HabitStatusUpdateRequestDTO {
    @NotNull
    private boolean active;
}
