package com.example.demo.service;

import com.example.demo.dto.request.SongRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SongResponse;

public interface SongService {
    long addSong(SongRequest request);
    SongResponse getSong(Long id);
    long updateSong(Long id, SongRequest request);
    long deleteSong(Long id);
    PageResponse<?> getAllSongs(int pageNo, int pageSize, String sortBy);
    PageResponse<?> getSongsByName(int pageNo, int pageSize, String sortBy, String name);
    PageResponse<?> getSongsByGenre(int pageNo, int pageSize, String sortBy, Integer genreId);
    PageResponse<?> sortAndSpecificationSearch(int pageNo, int pageSize, String sortBy, String search);
}
