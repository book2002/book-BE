package com.team2002.capstone.service;

import com.team2002.capstone.domain.*;
import com.team2002.capstone.dto.*;
import com.team2002.capstone.exception.ResourceNotFoundException;
import com.team2002.capstone.repository.*;
import com.team2002.capstone.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.ZonedDateTime;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookShelfRepository bookShelfRepository;
    private final BookShelfItemRepository bookShelfItemRepository;
    private final WebClient.Builder webClientBuilder;
    private final ReviewRepository reviewRepository;
    private final MemorableSentenceRepository memorableSentenceRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;


    // 카카오 REST API 키를 입력
    private final String KAKAO_REST_API_KEY = "a3447f2a4204dc00c0f3f2f6ca9a7efb";

    /** public BookService(WebClient.Builder webClientBuilder,
                       BookShelfRepository bookShelfRepository,
                       BookShelfItemRepository bookShelfItemRepository, ReviewRepository reviewRepository,
                       MemorableSentenceRepository memorableSentenceRepository,
                       MemberRepository memberRepository,
                       ProfileRepository profileRepository) {
        this.webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();
        this.bookShelfRepository = bookShelfRepository;
        this.bookShelfItemRepository = bookShelfItemRepository;
        this.reviewRepository = reviewRepository;
        this.memorableSentenceRepository = memorableSentenceRepository;
        this.memberRepository = memberRepository;
        this.profileRepository = profileRepository;
    } **/

    public List<BookDto> searchBooks(String query) {
        return searchBooks(query, "accuracy");
    }

    // 2. 정렬 기준을 포함한 검색 (실제 API 호출 수행)
    public List<BookDto> searchBooks(String query, String sort) {
        WebClient webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();

        try {
            KakaoBookSearchResponseDto responseDto = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v3/search/book")
                            .queryParam("query", query)
                            .queryParam("sort", sort) // 정렬 파라미터 적용
                            .queryParam("size", 50)   // 필터링을 위해 넉넉하게 50개 가져옴
                            .build())
                    .header("Authorization", "KakaoAK " + KAKAO_REST_API_KEY)
                    .retrieve()
                    .bodyToMono(KakaoBookSearchResponseDto.class)
                    .block();

            return (responseDto != null && responseDto.getDocuments() != null) ?
                    responseDto.getDocuments() : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("### API 호출 과정에서 예외 발생: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    public List<BookDto> getRecommendedBooks() {
        return searchBooks("베스트셀러");
    }

    public List<BookDto> getNewReleases() {
        // 복잡한 날짜 필터링 없이, 단순히 "신간" 키워드로 최신순(latest) 정렬하여 반환합니다.
        return searchBooks("소설", "latest");
    }

    //BookShelf
    @Transactional
    public BookShelfItemDto saveBookToMyShelf(BookSaveRequestDto requestDto) {
        Profile profile = getCurrentProfile();
        BookShelf userShelf = bookShelfRepository.findByProfile(profile)
                .orElseGet(() -> bookShelfRepository.save(new BookShelf("내 책장", profile))); // (BookShelf 생성자 순서 (String, Profile) 가정)

        bookShelfItemRepository.findByIsbnAndBookShelf(requestDto.getIsbn(), userShelf)
                .ifPresent(item -> {
                    throw new IllegalStateException("이미 책장에 추가된 책입니다.");
                });

        BookShelfItem newItem = new BookShelfItem(
                requestDto.getIsbn(),
                requestDto.getTitle(),
                (requestDto.getAuthors() != null) ? String.join(", ", requestDto.getAuthors()) : null,
                requestDto.getThumbnail(),
                userShelf,
                requestDto.getState(),
                requestDto.getCurrentPage(),
                requestDto.getTotalPage()
        );
        BookShelfItem savedItem = bookShelfItemRepository.save(newItem);
        return new BookShelfItemDto(savedItem);
    }

    public List<BookShelfItemDto> getMyShelfItems() {
        Profile profile = getCurrentProfile();
        BookShelf userShelf = bookShelfRepository.findByProfile(profile)
                .orElseGet(() -> {

                    return bookShelfRepository.save(new BookShelf("내 책장", profile));
                });

        return bookShelfItemRepository.findByBookShelf(userShelf).stream()
                .map(BookShelfItemDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBookFromMyShelf(Long itemId) {
        Profile profile = getCurrentProfile();
        BookShelfItem item = bookShelfItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("삭제할 책을 찾을 수 없습니다."));

        if (!Objects.equals(item.getBookShelf().getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 책을 삭제할 권한이 없습니다.");
        }

        bookShelfItemRepository.deleteById(itemId);
    }

    // Review
    @Transactional
    public ReviewResponseDto saveReview(ReviewSaveRequestDto reviewDto) {
        Profile profile = getCurrentProfile();
        BookShelfItem bookShelfItem = bookShelfItemRepository.findById(reviewDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다. itemId=" + reviewDto.getItemId()));
        Review newReview = new Review(reviewDto, bookShelfItem,profile);
        Review savedReview = reviewRepository.save(newReview);
        return new ReviewResponseDto(savedReview);
    }

    public List<ReviewResponseDto> getReviewsByItemId(Long itemId) {
        return reviewRepository.findByBookShelfItem_ItemId(itemId).stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewUpdateRequestDto requestDto) { // DTO 변경
        Profile profile = getCurrentProfile();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
        if (!Objects.equals(review.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 리뷰를 수정할 권한이 없습니다.");
        }
        review.update(requestDto);
        return new ReviewResponseDto(review);
    }

    public void deleteReview(Long reviewId) {
        Profile profile = getCurrentProfile();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("삭제할 리뷰를 찾을 수 없습니다."));
        if (!Objects.equals(review.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 리뷰를 삭제할 권한이 없습니다.");
        }
        reviewRepository.deleteById(reviewId);
    }

    public List<ReviewResponseDto> getMyReviews() {
        Profile profile = getCurrentProfile();
        List<Review> myReviews = reviewRepository.findByProfile(profile);
        return myReviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    // MemorableSentence
    @Transactional
    public MemorableSentenceResponseDto saveMemorableSentence(MemorableSentenceSaveRequestDto dto) {
        Profile profile = getCurrentProfile();
        BookShelfItem bookShelfItem = bookShelfItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        MemorableSentence newSentence = new MemorableSentence(dto, bookShelfItem, profile);
        MemorableSentence savedSentence = memorableSentenceRepository.save(newSentence);
        return new MemorableSentenceResponseDto(savedSentence);
    }

    public List<MemorableSentenceResponseDto> getMemorableSentencesByItemId(Long itemId) {
        return memorableSentenceRepository.findByBookShelfItem_ItemId(itemId).stream()
                .map(MemorableSentenceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemorableSentenceResponseDto updateMemorableSentence(Long sentenceId, MemorableSentenceSaveRequestDto dto) {
        Profile profile = getCurrentProfile();
        MemorableSentence sentence = memorableSentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문장입니다."));
        if (!Objects.equals(sentence.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 문장을 수정할 권한이 없습니다.");
        }
        sentence.update(dto);
        return new MemorableSentenceResponseDto(sentence);
    }


    @Transactional
    public void deleteMemorableSentence(Long sentenceId) {
        Profile profile = getCurrentProfile();
        MemorableSentence sentence = memorableSentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new ResourceNotFoundException("삭제할 문장을 찾을 수 없습니다."));

        if (!Objects.equals(sentence.getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 문장을 삭제할 권한이 없습니다.");
        }
        memorableSentenceRepository.deleteById(sentenceId);
    }

    // BookProgress
    @Transactional
    public BookShelfItemDto updateBookProgress(Long itemId, ProgressUpdateRequestDto dto) {
        Profile profile = getCurrentProfile();
        BookShelfItem item = bookShelfItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        if (!Objects.equals(item.getBookShelf().getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 책의 진행률을 수정할 권한이 없습니다.");
        }
        item.updateProgress(dto.getCurrentPage());
        return new BookShelfItemDto(item);
    }

    @Transactional
    public BookShelfItemDto updateBookState(Long itemId, BookStateUpdateRequestDto dto) {
        Profile profile = getCurrentProfile();
        BookShelfItem item = bookShelfItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        if (!Objects.equals(item.getBookShelf().getProfile().getId(), profile.getId())) {
            throw new IllegalStateException("이 책의 상태를 수정할 권한이 없습니다.");
        }
        item.updateStateAndPages(dto.getNewState(), dto.getCurrentPage(), dto.getTotalPage());
        return new BookShelfItemDto(item);
    }

    private Profile getCurrentProfile() {
        String userEmail = SecurityUtil.getCurrentUsername();
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
        return profileRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("현재 로그인한 사용자의 프로필을 찾을 수 없습니다."));
    }

}

