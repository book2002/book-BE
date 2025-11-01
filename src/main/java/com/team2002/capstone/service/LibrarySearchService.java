package com.team2002.capstone.service;

import com.team2002.capstone.dto.LibraryDto;
import com.team2002.capstone.dto.LibraryResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibrarySearchService {

    private final WebClient webClient;

    private final String NARU_API_KEY = "30c1aec048afddf4aa40d1785eaf9e9af71d0a20ebc7352cbfcaee08dfed0e66";
    private final String NARU_API_BASE_URL = "http://data4library.kr";

    public LibrarySearchService(WebClient.Builder webClineBuilder) {
        this.webClient = webClineBuilder.baseUrl(NARU_API_BASE_URL).build();
    }

    public List<LibraryDto> searchLibraries(String keyword) {
        try {
            LibraryResponseDto response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/libSrch") // 외부 api주소
                            .queryParam("authKey", NARU_API_KEY)
                            .queryParam("keyword", keyword)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .bodyToMono(LibraryResponseDto.class)
                    .block();

            // 최종 도서관 목록만 추출
            if (response != null && response.getResponse() != null &&
                    response.getResponse().getLibs() != null &&
                    response.getResponse().getLibs().getLib() != null) {

                return response.getResponse().getLibs().getLib().stream()
                        .map(LibraryResponseDto.LibWrapper::getLib)
                        .collect(Collectors.toList());
            }

            return Collections.emptyList(); // 결과가 없으면 빈 리스트 반환

        } catch (Exception e) {
            System.err.println("도서관 정보나루 API 호출 중 에러 발생: " + e.getMessage());
            return Collections.emptyList();
        }
        }
    }


