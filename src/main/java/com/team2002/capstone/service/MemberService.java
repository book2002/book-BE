package com.team2002.capstone.service;

import com.team2002.capstone.config.jwt.JwtTokenProvider;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.domain.Profile;
import com.team2002.capstone.domain.enums.AccountStatusEnum;
import com.team2002.capstone.domain.enums.ProviderEnum;
import com.team2002.capstone.dto.*;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.MemberRepository;
import com.team2002.capstone.repository.ProfileRepository;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional //readOnly = false -> default
    public MemberResponseDTO save(Member member) {
        validateDuplicateMember(member.getEmail());

        Member savedMember = memberRepository.save(member);

        return MemberResponseDTO.builder()
                .memberId(savedMember.getId())
                .role(savedMember.getRole())
                .status(savedMember.getStatus())
                .createdAt(savedMember.getCreatedAt())
                .build();
    }

    private void validateDuplicateMember(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다");
                }
        );
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {
        Member member = memberRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));

        if(member.getProvider() != ProviderEnum.LOCAL) {
            throw new IllegalStateException("구글 회원은 구글 로그인을 이용해주세요.");
        }

        if(!passwordEncoder.matches(requestDTO.getPassword(), member.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        Optional<Profile> memberProfile = profileRepository.findByMember(member);
        boolean isNewUser = memberProfile.isEmpty();

        JwtTokenDTO jwtTokenDTO = jwtTokenProvider.generateToken(member, isNewUser);

        return LoginResponseDTO.builder()
                .grantType(jwtTokenDTO.getGrantType())
                .accessToken(jwtTokenDTO.getAccessToken())
                .refreshToken(jwtTokenDTO.getRefreshToken())
                .isNewUser(isNewUser)
                .build();
    }

    @Transactional
    public void updatePassword(MemberUpdateRequestDTO requestDTO) {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        if(!passwordEncoder.matches(requestDTO.getOldPassword(), member.getPassword())) {
            throw new IllegalStateException("기존 비밀번호가 일치하지 않습니다.");
        }
        member.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
    }

    @Transactional
    public void inactivateMember() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        member.setStatus(AccountStatusEnum.INACTIVE);
    }

     /*
     *  관리자용
     */
    public List<AdminMemberResponseDTO> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(member -> {
                    Profile profile = member.getProfile();
                    return AdminMemberResponseDTO.from(member, profile);
                })
                .collect(Collectors.toList());
    }

    public AdminMemberResponseDTO getMemberDetail(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        Profile profile = member.getProfile();
        return AdminMemberResponseDTO.from(member, profile);
    }

    @Transactional
    public void suspendedMember(Member member) {
        member.setStatus(AccountStatusEnum.SUSPENDED);
    }

    @Transactional
    public void activeMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        member.setStatus(AccountStatusEnum.ACTIVE);
    }

    @Transactional
    public void deleteMember() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        Profile profile = member.getProfile();
        if (profile != null) {
            profileRepository.delete(profile);
        }

        memberRepository.delete(member);
    }

    @Transactional
    public void updateFcmToken(FcmTokenRequestDTO requestDTO) {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));

        member.setFcmToken(requestDTO.getFcmToken());
    }
}
