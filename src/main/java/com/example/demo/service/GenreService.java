package com.example.demo.service;

import com.example.demo.constant.GenreName;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SearchGenreResponse;

import java.util.List;

public interface GenreService {
    List<SearchGenreResponse> getAllGenres();
}
