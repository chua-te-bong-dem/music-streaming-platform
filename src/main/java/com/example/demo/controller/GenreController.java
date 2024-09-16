package com.example.demo.controller;

import com.example.demo.constant.GenreName;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SearchGenreResponse;
import com.example.demo.dto.response.SearchSongResponse;
import com.example.demo.service.GenreService;
import com.example.demo.dto.response.ResponseData;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genre")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/all")
    public ResponseData<?> getAllGenres() {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get all genres success",
                genreService.getAllGenres());
    }
}
