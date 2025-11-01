package com.team2002.capstone.controller;

import com.team2002.capstone.dto.GroupCreateRequestDTO;
import com.team2002.capstone.dto.GroupResponseDTO;
import com.team2002.capstone.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @Operation(summary = "독서 모임 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupResponseDTO> createGroup(
            @Validated @RequestPart (value = "request") GroupCreateRequestDTO groupCreateRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image)
    throws IOException {
        GroupResponseDTO groupResponseDTO = groupService.createGroup(groupCreateRequestDTO, image);
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

    @Operation(summary = "독서 모임 목록 조회 (인기순)")
    @GetMapping("/popular")
    public ResponseEntity<List<GroupResponseDTO>> getPopularGroups() {
        List<GroupResponseDTO> groupResponseDTOList = groupService.getPopularGroups();
        return ResponseEntity.status(HttpStatus.OK).body(groupResponseDTOList);
    }

    @Operation(summary = "독서 모임 탈퇴")
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long groupId) {
        groupService.leaveGroup(groupId);
        return ResponseEntity.ok().build();
    }
}
