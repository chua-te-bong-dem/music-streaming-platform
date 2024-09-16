package com.example.demo.service;

import com.example.demo.dto.request.AlbumRequest;
import com.example.demo.dto.request.AlbumRequestForArtist;
import com.example.demo.dto.response.AlbumResponse;
import com.example.demo.dto.response.PageResponse;

public interface AlbumService {
    long addAlbum(String artistName, AlbumRequestForArtist albumRequest);
    AlbumResponse getAlbum(Long id);
    long updateAlbum(Long id, AlbumRequestForArtist albumRequest);
    long deleteAlbum(Long id);
    PageResponse<?> getAllAlbums(int pageNo, int pageSize, String sortBy);
    PageResponse<?> getAlbumsByName(int pageNo, int pageSize, String sortBy, String name);
    PageResponse<?> sortAndSpecificationSearch(int pageNo, int pageSize, String sortBy, String search);
}
