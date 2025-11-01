package com.team2002.capstone.controller;


import com.team2002.capstone.dto.LibraryDto;
import com.team2002.capstone.service.LibrarySearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/libraries")
public class LibrarySearchController {
    private final LibrarySearchService librarySearchService;

    public LibrarySearchController(LibrarySearchService librarySearchService) {
        this.librarySearchService = librarySearchService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<LibraryDto>> searchLibraries(@RequestParam String keyword) {
        List<LibraryDto> libraries = librarySearchService.searchLibraries(keyword);
        return ResponseEntity.ok(libraries);
    }
}
