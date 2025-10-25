package com.team2002.capstone.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.team2002.capstone.dto.BookDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class BookShelfItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String thumbnail;

    // --- BookShelf와의 관계 설정 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    @JsonBackReference
    private BookShelf bookShelf;
    // ----------------------------

    @Enumerated(EnumType.STRING)
    private ReadState state;

    private Integer currentPage;
    private Integer totalPage;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bookShelfItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    public enum ReadState {
        WANT_TO_READ, READING, COMPLETED
    } // 읽고싶은, 읽는 중, 다 읽은

    @OneToMany(mappedBy = "bookShelfItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MemorableSentence> memorableSentences = new ArrayList<>();

    public BookShelfItem(String isbn, String title, String author, String thumbnail,
                         BookShelf bookShelf, ReadState initialState, Integer currentPage, Integer totalPage) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.thumbnail = thumbnail;
        this.bookShelf = bookShelf;
        this.createdAt = LocalDateTime.now();
        updateStateAndPages(initialState, currentPage, totalPage);
    }

    public void updateStateAndPages(ReadState newState, Integer currentPage, Integer totalPage) {
        // 유효성검사
        if (totalPage != null && totalPage <= 0) {
            throw new IllegalArgumentException("입력한 전체 페이지 수는 0보다 커야합니다.");
        }
        if (currentPage != null && currentPage < 0) {
            throw new IllegalArgumentException("입력한 현재 페이지 수는 0보다 커야합니다.");
        }
        if (totalPage != null && currentPage != null && currentPage > totalPage) {
            throw new IllegalArgumentException("현재 페이지 수는 전체 페이지 수보다 클 수 없습니다.");
        }

        if (newState == ReadState.READING) { // 읽고 있는 중 목록 선택 시
            if (currentPage == null || totalPage == null) {
                throw new IllegalArgumentException("'읽고 있는 중' 책장에는 현재 페이지와 전체 페이지 입력이 필요합니다.");
            }
            this.state = ReadState.READING;
            this.currentPage = currentPage;
            this.totalPage = totalPage;

            if (Objects.equals(this.currentPage, this.totalPage)) {
                this.state = ReadState.COMPLETED;
            }
        } else if (newState == ReadState.WANT_TO_READ) { // 읽고 싶은 책 목록 선택 시
            this.state = ReadState.WANT_TO_READ;
            this.currentPage = 0;
            if (totalPage != null) this.totalPage = totalPage;
        } else if (newState == ReadState.COMPLETED) { //  다 읽은 책 목록 선택 시
            this.state = ReadState.COMPLETED;
            if (totalPage != null) {
                this.totalPage = totalPage;
                this.currentPage = this.totalPage;
            } else {
                this.totalPage = null;
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 상태입니다.");
        }
    }

        public void updateProgress (Integer newCurrentPage){
            if (this.state == ReadState.WANT_TO_READ) {
                throw new IllegalStateException("'읽고 싶은 책'은 진행률을 업데이트할 수 없습니다. 상태를 먼저 변경해주세요.");
            }
            if (this.totalPage == null || this.totalPage <= 0) {
                throw new IllegalStateException("전체 페이지 수가 설정되지 않아 진행률을 업데이트할 수 없습니다.");
            }
            if (newCurrentPage == null || newCurrentPage < 0) {
                throw new IllegalArgumentException("현재 페이지 수는 0 이상이어야 합니다.");
            }

            this.currentPage = Math.min(newCurrentPage, this.totalPage); // 전체 페이지 초과방지

            if (Objects.equals(this.currentPage, this.totalPage)) {
                this.state = ReadState.COMPLETED;
            } else {
                this.state = ReadState.READING;
            }
        }

        // 전체페이지 업데이트
        public void updateTotalPage (Integer newTotalPage){
            if (newTotalPage == null || newTotalPage <= 0) {
                throw new IllegalArgumentException("전체 페이지 수는 0보다 커야 합니다.");
            }
            this.totalPage = newTotalPage;
            updateProgress(this.currentPage);
        }
    }


