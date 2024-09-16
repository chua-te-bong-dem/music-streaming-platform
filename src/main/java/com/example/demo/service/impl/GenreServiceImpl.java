package com.example.demo.service.impl;

import com.example.demo.dto.response.SearchGenreResponse;
import com.example.demo.model.Genre;
import com.example.demo.repository.GenreRepository;
import com.example.demo.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<SearchGenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();

        return genres.stream()
                .map(genre -> SearchGenreResponse.builder()
                        .id(genre.getId())
                        .name(genre.getName())
                        .build())
                .toList();
    }


}
