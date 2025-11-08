package com.team2002.capstone.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.team2002.capstone.domain.common.BaseEntity;
import com.team2002.capstone.domain.enums.GenderEnum;
import jakarta.persistence.*;
        import lombok.*;

        import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nickname;

    private String bio;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String birth;

    private String profileImageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followings = new HashSet<>(); // 내가 팔로우 하는 사람들

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followers = new HashSet<>(); // 나를 팔로우 하는 사람들

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-loans")
    private List<BookLoan> bookLoans = new ArrayList<>();


    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-favorites")
    @Builder.Default
    private List<FavoriteLibrary> favoriteLibraries = new ArrayList<>();


    public Profile() {
    }
}
