package com.example.demo.mapper;

import com.example.demo.dto.response.ArtistResponse;
import com.example.demo.dto.response.SearchArtistResponse;
import com.example.demo.model.Artist;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    ArtistResponse toArtistResponse(Artist artist);
    SearchArtistResponse toSearchArtistResponse(Artist artist);
}
