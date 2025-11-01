package com.team2002.capstone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LibraryResponseDto {

    private Response response; // 최상위 "response" 키

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Libs libs; // "libs" 키
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Libs {
        private List<LibWrapper> lib; // "lib" 키 (이름이 lib이지만 실제로는 목록임)
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LibWrapper {
        private LibraryDto lib; // 최종 "lib" 객체
    }
}
