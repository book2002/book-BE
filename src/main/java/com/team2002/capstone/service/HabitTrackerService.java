package com.team2002.capstone.service;

import com.team2002.capstone.domain.HabitTracker;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.dto.HabitStatusUpdateRequestDTO;
import com.team2002.capstone.dto.HabitStatusUpdateResponseDTO;
import com.team2002.capstone.dto.HabitTrackerRequestDTO;
import com.team2002.capstone.dto.HabitTrackerResponseDTO;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.HabitTrackerRepository;
import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitTrackerService {
    private final HabitTrackerRepository habitTrackerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public HabitTrackerResponseDTO record(HabitTrackerRequestDTO requestDTO) {
        Member member = getCurrentMember();
        LocalDate targetDate = requestDTO.getRecordDate();

        if (habitTrackerRepository.findByMemberAndRecordDate(member, targetDate).isPresent()) {
            throw new IllegalStateException(targetDate + " already exists");
        }

        HabitTracker habitTracker = HabitTracker.builder()
                .member(member)
                .recordDate(targetDate)
                .build();
        habitTrackerRepository.save(habitTracker);

        return HabitTrackerResponseDTO.builder()
                .id(habitTracker.getId())
                .memberId(member.getId())
                .recordDate(targetDate)
                .message("독서 기록이 완료되었습니다.")
                .build();
    }

    @Transactional
    public List<LocalDate> getMonthlyHabitRecords(int year, int month) {
        Member member = getCurrentMember();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<HabitTracker> records = habitTrackerRepository.findAllByMemberAndRecordDateBetween(member, startDate, endDate);

        return records.stream().map(HabitTracker::getRecordDate).collect(Collectors.toList());
    }

    @Transactional
    public HabitTrackerResponseDTO getTodayHabitRecord() {
        Member member = getCurrentMember();
        LocalDate today = LocalDate.now();

        boolean completed = habitTrackerRepository.findByMemberAndRecordDate(member, today).isPresent();
        String message = completed ? "독서 기록이 완료되었습니다." : "독서 기록이 되지 않았습니다.";

        return HabitTrackerResponseDTO.builder()
                .memberId(member.getId())
                .recordDate(today)
                .message(message)
                .build();
    }

    @Transactional
    public HabitStatusUpdateResponseDTO updateHabitActiveStatus(HabitStatusUpdateRequestDTO requestDTO) {
        Member member = getCurrentMember();
        member.setHabitTrackerActive(requestDTO.isActive());
        memberRepository.save(member);

        return HabitStatusUpdateResponseDTO.builder()
                .memberId(member.getId())
                .isActive(member.isHabitTrackerActive())
                .build();
    }

    public HabitStatusUpdateResponseDTO getHabitStatus() {
        Member member = getCurrentMember();
        boolean habitTrackerActive = member.isHabitTrackerActive();

        return HabitStatusUpdateResponseDTO.builder()
                .memberId(member.getId())
                .isActive(habitTrackerActive)
                .build();
    }

    private Member getCurrentMember() {
        String userEmail = SecurityUtil.getCurrentUsername();
        return memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
    }
}
