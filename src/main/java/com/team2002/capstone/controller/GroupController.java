package com.team2002.capstone.controller;

import com.team2002.capstone.dto.GroupCreateRequestDTO;
import com.team2002.capstone.dto.GroupResponseDTO;
import com.team2002.capstone.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @Operation(summary = "독서 모임 생성")
    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(@RequestBody @Validated GroupCreateRequestDTO groupCreateRequestDTO) {
        GroupResponseDTO groupResponseDTO = groupService.createGroup(groupCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupResponseDTO);
    }

    @Operation(summary = "독서 모임 가입")
    @PostMapping("/{groupId}/join")
    public ResponseEntity<GroupResponseDTO> joinGroup(@PathVariable Long groupId) {
        GroupResponseDTO groupResponseDTO = groupService.joinGroup(groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupResponseDTO);
    }

    @Operation(summary = "나의 모임 목록 조회")
    @GetMapping("/my")
    public ResponseEntity<List<GroupResponseDTO>> getMyGroups() {
        List<GroupResponseDTO> groupResponseDTOList = groupService.getMyGroups();
        return ResponseEntity.status(HttpStatus.OK).body(groupResponseDTOList);
    }

    @Operation(summary = "독서 모임 목록 조회")
    @GetMapping
    public ResponseEntity<List<GroupResponseDTO>> getGroups() {
        List<GroupResponseDTO> groupResponseDTOList = groupService.getGroups();
        return ResponseEntity.status(HttpStatus.OK).body(groupResponseDTOList);
    }

    @Operation(summary = "독서 모임 탈퇴")
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long groupId) {
        groupService.leaveGroup(groupId);
        return ResponseEntity.ok().build();
    }
}
