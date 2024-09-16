package com.example.demo.mapper;

import com.example.demo.dto.request.SongRequest;
import com.example.demo.dto.request.SongRequestForArtist;
import com.example.demo.dto.response.SearchSongResponse;
import com.example.demo.dto.response.SongResponse;
import com.example.demo.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SongMapper {
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "album", ignore = true)
    Song toSong(SongRequest request);
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "album", ignore = true)
    void updateSong(@MappingTarget Song song, SongRequest request);
    SongResponse toSongResponse(Song song);
    SearchSongResponse toSearchSongResponse(Song song);
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "album", ignore = true)
    Song toSong(SongRequestForArtist songRequest);
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "album", ignore = true)
    void updateSong(@MappingTarget Song song, SongRequestForArtist songRequest);
}
