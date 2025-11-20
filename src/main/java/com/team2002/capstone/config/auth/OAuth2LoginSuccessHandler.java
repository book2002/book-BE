package com.team2002.capstone.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2002.capstone.config.jwt.JwtTokenProvider;
import com.team2002.capstone.domain.Member;
import com.team2002.capstone.dto.JwtTokenDTO;
import com.team2002.capstone.dto.LoginResponseDTO;
import com.team2002.capstone.repository.ProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ProfileRepository profileRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
        CustomOauth2UserDetails userDetails = (CustomOauth2UserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        boolean isNewUser = profileRepository.findByMember(member).isEmpty();

        JwtTokenDTO jwtTokenDTO = jwtTokenProvider.generateToken(member, isNewUser);
        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .grantType(jwtTokenDTO.getGrantType())
                .accessToken(jwtTokenDTO.getAccessToken())
                .refreshToken(jwtTokenDTO.getRefreshToken())
                .isNewUser(isNewUser)
                .build();

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(loginResponseDTO));
        response.getWriter().flush();
        } catch (IOException e) {
            log.error("FATAL ERROR IN OAUTH SUCCESS HANDLER", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": 500, \"message\": \"서버 내부 오류: 관리자에게 문의하세요.\"}");
            response.getWriter().flush();
        }
    }
}
