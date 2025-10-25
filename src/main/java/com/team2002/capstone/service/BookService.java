package com.team2002.capstone.service;

import com.team2002.capstone.domain.BookShelf;
import com.team2002.capstone.domain.BookShelfItem;
import com.team2002.capstone.domain.Review;
import com.team2002.capstone.dto.*;
import com.team2002.capstone.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.team2002.capstone.repository.BookShelfItemRepository;
import com.team2002.capstone.repository.BookShelfRepository;
import com.team2002.capstone.domain.MemorableSentence;
import com.team2002.capstone.repository.MemorableSentenceRepository;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookShelfRepository bookShelfRepository;
    private final BookShelfItemRepository bookShelfItemRepository;
    private final WebClient webClient;
    private final ReviewRepository reviewRepository;
    private final MemorableSentenceRepository memorableSentenceRepository;
    // 카카오 REST API 키를 입력
    private final String KAKAO_REST_API_KEY = "a3447f2a4204dc00c0f3f2f6ca9a7efb";

    public BookService(WebClient.Builder webClientBuilder,
                       BookShelfRepository bookShelfRepository,
                       BookShelfItemRepository bookShelfItemRepository, ReviewRepository reviewRepository,
                       MemorableSentenceRepository memorableSentenceRepository) {
        this.webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();
        this.bookShelfRepository = bookShelfRepository;
        this.bookShelfItemRepository = bookShelfItemRepository;
        this.reviewRepository = reviewRepository;
        this.memorableSentenceRepository = memorableSentenceRepository;
    }

    // API에서 받은 DTO를 그대로 반환
    public List<BookDto> searchBooks(String query) {
        KakaoBookSearchResponseDto responseDto = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v3/search/book").queryParam("query", query).build())
                .header("Authorization", "KakaoAK " + KAKAO_REST_API_KEY)
                .retrieve()
                .bodyToMono(KakaoBookSearchResponseDto.class)
                .block();

        // API 응답에서 책 목록(DTO 리스트)만 추출하여 반환
        return responseDto.getDocuments();
    }

    public List<BookDto> getRecommendedBooks() {
        return searchBooks("베스트셀러");
    }

    public List<BookDto> getNewReleases() {
        return searchBooks("신간");
    }

    public List<BookDto> getBestsellersByAge(int age) {
        int ageGroup = (age / 10) * 10;
        String query = ageGroup + "대";

        return searchBooks(query);
    }

    public List<BookDto> getBestsellersByBirthDate(String birthDate) {
        int age = calculateAge(birthDate);
        int ageGroup = (age / 10) * 10;
        return getBestsellersByAge(ageGroup);
    }

    private int calculateAge(String birthDate) {
        if (birthDate == null || birthDate.length() != 8) {
            return 20; // 형식이 맞지 않으면 기본값 20세로 처리
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate birth = LocalDate.parse(birthDate, formatter);
            LocalDate today = LocalDate.now();
            return (int) ChronoUnit.YEARS.between(birth, today);
        } catch (Exception e) {

            System.err.println("잘못된 날짜 형식입니다: " + birthDate);
            return 20; // 오류 발생 시 기본값 20세로 처리
        }
    }

    //BookShelf
    @Transactional
    // 반환 타입을 DTO로 변경
    public BookShelfItemDto saveBookToMyShelf(BookSaveRequestDto requestDto) {
        BookShelf defaultShelf = bookShelfRepository.findById(1L)
                .orElseGet(() -> bookShelfRepository.save(new BookShelf("내 책장")));

        bookShelfItemRepository.findByIsbnAndBookShelf(requestDto.getIsbn(), defaultShelf)
                .ifPresent(item -> {
                    throw new IllegalStateException("이미 책장에 추가된 책입니다.");
                });

        // ▼▼▼ 올바른 생성자 호출 (8개 인자) ▼▼▼
        BookShelfItem newItem = new BookShelfItem(
                requestDto.getIsbn(),
                requestDto.getTitle(),
                (requestDto.getAuthors() != null) ? String.join(", ", requestDto.getAuthors()) : null, // authors 처리 (null 가능하게)
                requestDto.getThumbnail(),
                defaultShelf,
                requestDto.getState(),
                requestDto.getCurrentPage(),
                requestDto.getTotalPage()
        );
        BookShelfItem savedItem = bookShelfItemRepository.save(newItem);
        // Entity -> Response DTO로 변환하여 반환
        return new BookShelfItemDto(savedItem);
    }

    public List<BookShelfItemDto> getMyShelfItems() {
        // ID가 1인 책장
        return bookShelfRepository.findById(1L)
                .map(bookShelfItemRepository::findByBookShelf)
                .orElse(Collections.emptyList()).stream()
                .map(BookShelfItemDto::new)
                .collect(Collectors.toList());
    }
    public void deleteBookFromMyShelf(Long itemId) {
        bookShelfItemRepository.deleteById(itemId);
    }

    // Review
    @Transactional
    public ReviewResponseDto saveReview(ReviewSaveRequestDto reviewDto) {
        BookShelfItem bookShelfItem = bookShelfItemRepository.findById(reviewDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다. itemId=" + reviewDto.getItemId()));
        Review newReview = new Review(reviewDto, bookShelfItem);
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
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다. reviewId=" + reviewId));

        review.update(requestDto); // Entity의 update 메소드도 이 DTO를 받도록 수정 필요
        return new ReviewResponseDto(review);
    }

    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("존재하지 않는 리뷰입니다. reviewId=" + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    // MemorableSentence
    @Transactional
    public MemorableSentence saveMemorableSentence(MemorableSentenceDto dto) {
        BookShelfItem bookShelfItem = bookShelfItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다. itemId=" + dto.getItemId()));
        MemorableSentence newSentence = new MemorableSentence(dto, bookShelfItem);
        return memorableSentenceRepository.save(newSentence);
    }

    public List<MemorableSentence> getMemorableSentencesByItemId(Long itemId) {
        return memorableSentenceRepository.findByBookShelfItem_ItemId(itemId);
    }

    @Transactional
    public MemorableSentence updateMemorableSentence(Long sentenceId, MemorableSentenceDto dto) {
        MemorableSentence sentence = memorableSentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문장입니다. sentenceId=" + sentenceId));
        sentence.update(dto);
        return sentence;
    }

    public void deleteMemorableSentence(Long sentenceId) {
        if (!memorableSentenceRepository.existsById(sentenceId)) {
            throw new IllegalArgumentException("존재하지 않는 문장입니다. sentenceId=" + sentenceId);
        }
        memorableSentenceRepository.deleteById(sentenceId);
    }

    // BookProgress
    @Transactional
    // 반환 타입을 DTO로 변경
    public BookShelfItemDto updateBookProgress(Long itemId, ProgressUpdateRequestDto dto) {
        BookShelfItem item = bookShelfItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다. itemId=" + itemId));
        item.updateProgress(dto.getCurrentPage());
        // Entity -> Response DTO로 변환하여 반환
        return new BookShelfItemDto(item);
    }

    public BookShelfItemDto updateBookState(Long itemId, BookStateUpdateRequestDto dto) {
        BookShelfItem item = bookShelfItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다. itemId=" + itemId));
        // Entity 내부 메소드 호출 (public으로 변경했다고 가정)
        item.updateStateAndPages(dto.getNewState(), dto.getCurrentPage(), dto.getTotalPage());
        // Entity -> Response DTO로 변환하여 반환
        BookShelfItem updatedItem = bookShelfItemRepository.save(item);
        return new BookShelfItemDto(item);
    }

}

