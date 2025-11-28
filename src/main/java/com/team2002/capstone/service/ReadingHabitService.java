package com.team2002.capstone.service;

import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.ReadingHabit;
import com.team2002.capstone.dto.HabitStatusUpdateRequestDTO;
import com.team2002.capstone.dto.ReadingHabitRequestDTO;
import com.team2002.capstone.dto.ReadingHabitResponseDTO;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.repository.ReadingHabitRepository;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReadingHabitService {

    private final ReadingHabitRepository readingHabitRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReadingHabitResponseDTO createReadingHabit(ReadingHabitRequestDTO requestDTO) {
        Member member = getCurrentMember();

        ReadingHabit readingHabit = ReadingHabit.builder()
                .member(member)
                .targetTime(requestDTO.getTargetTime())
                .recurringDays(String.join(",", requestDTO.getDaysOfWeek()))
                .isActive(true)
                .build();

        readingHabitRepository.save(readingHabit);
        return ReadingHabitResponseDTO.builder()
                 .id(readingHabit.getId())
                 .targetTime(readingHabit.getTargetTime())
                 .recurringDays(stringToList(readingHabit.getRecurringDays()))
                .isActive(readingHabit.isActive())
                 .build();
    }

    @Transactional
    public ReadingHabitResponseDTO updateReadingHabit(Long id, ReadingHabitRequestDTO requestDTO) {
        Member member = getCurrentMember();
        ReadingHabit readingHabit = readingHabitRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new ResourceNotFoundException("해당 습관을 찾을 수 없습니다."));

        readingHabit.setTargetTime(requestDTO.getTargetTime());
        readingHabit.setRecurringDays(String.join(",", requestDTO.getDaysOfWeek()));

        return ReadingHabitResponseDTO.builder()
                .id(readingHabit.getId())
                .targetTime(readingHabit.getTargetTime())
                .recurringDays(stringToList(readingHabit.getRecurringDays()))
                .isActive(readingHabit.isActive())
                .build();
    }

    public List<ReadingHabitResponseDTO> getReadingHabits() {
        Member member = getCurrentMember();
        List<ReadingHabit> habits = readingHabitRepository.findAllByMember(member);

        return habits.stream()
                .map(habit -> ReadingHabitResponseDTO.builder()
                        .id(habit.getId())
                        .targetTime(habit.getTargetTime())
                        .recurringDays(stringToList(habit.getRecurringDays()))
                        .isActive(habit.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    public ReadingHabitResponseDTO getReadingHabit(Long id) {
        Member member = getCurrentMember();
        ReadingHabit readingHabit = readingHabitRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new ResourceNotFoundException("해당 습관을 찾을 수 없습니다."));
        return ReadingHabitResponseDTO.builder()
                .id(readingHabit.getId())
                .targetTime(readingHabit.getTargetTime())
                .recurringDays(stringToList(readingHabit.getRecurringDays()))
                .isActive(readingHabit.isActive())
                .build();
    }

    @Transactional
    public void deleteReadingHabit(Long id) {
        Member member = getCurrentMember();
        ReadingHabit habit = readingHabitRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new ResourceNotFoundException("삭제할 습관을 찾을 수 없습니다."));

        readingHabitRepository.delete(habit);
    }

    @Transactional
    public ReadingHabitResponseDTO updateHabitActiveStatus(Long habitId, HabitStatusUpdateRequestDTO requestDTO) {
        Member member = getCurrentMember();
        ReadingHabit readingHabit = readingHabitRepository.findByIdAndMember(habitId, member)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Habit not found"));
        readingHabit.setActive(requestDTO.isActive());

        return ReadingHabitResponseDTO.builder()
                .id(readingHabit.getId())
                .targetTime(readingHabit.getTargetTime())
                .recurringDays(stringToList(readingHabit.getRecurringDays()))
                .isActive(requestDTO.isActive())
                .build();
    }

    private Member getCurrentMember() {
        String userEmail = SecurityUtil.getCurrentUsername();
        return memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
    }

    private List<String> stringToList(String s) {
        return s != null && !s.isEmpty() ? Arrays.asList(s.split(",")) : List.of();
    }
}
