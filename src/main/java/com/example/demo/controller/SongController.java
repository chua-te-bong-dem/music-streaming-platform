package com.example.demo.controller;

import com.example.demo.dto.request.SongRequest;
import com.example.demo.dto.response.ResponseData;
import com.example.demo.dto.response.SongResponse;
import com.example.demo.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/song")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Long> addSong(@Valid @RequestBody SongRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(),
                "Add song success",
                songService.addSong(request));
    }

    @GetMapping("/{songId}")
    public ResponseData<SongResponse> getSong(@PathVariable Long songId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get song success",
                songService.getSong(songId));
    }

    @PutMapping("/{songId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Long> updateSong(@PathVariable Long songId,
                                         @Valid @RequestBody SongRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Update song success",
                songService.updateSong(songId, request));
    }

    @DeleteMapping("/{songId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Long> deleteSong(@PathVariable Long songId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Delete song success",
                songService.deleteSong(songId));
    }

    @GetMapping("/all")
    public ResponseData<?> getAllSongs(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "20", required = false) int pageSize,
            @RequestParam(defaultValue = "likeCount:desc", required = false) String sortBy) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get all songs success",
                songService.getAllSongs(pageNo, pageSize, sortBy));
    }

    @GetMapping("/find-by-name")
    public ResponseData<?> getSongsByName(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "20", required = false) int pageSize,
            @RequestParam(defaultValue = "likeCount:desc", required = false) String sortBy,
            @RequestParam String songName) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get songs by name success",
                songService.getSongsByName(pageNo, pageSize, sortBy, songName));
    }

    @GetMapping("/find-by-genre")
    public ResponseData<?> getSongsByGenre(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "20", required = false) int pageSize,
            @RequestParam(defaultValue = "likeCount:desc", required = false) String sortBy,
            @RequestParam Integer genreId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get songs by genre success",
                songService.getSongsByGenre(pageNo, pageSize, sortBy, genreId));
    }

    @GetMapping("/specification")
    public ResponseData<?> sortAndSpecificationSearch(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "likeCount:desc", required = false) String sortBy,
            @RequestParam(defaultValue = "id!=0", required = false) String search) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Specification Search",
                songService.sortAndSpecificationSearch(pageNo, pageSize, sortBy, search));
    }
}
