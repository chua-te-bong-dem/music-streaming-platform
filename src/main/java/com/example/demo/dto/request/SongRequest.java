package com.example.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Builder
public class SongRequest implements Serializable {

    @NotBlank(message = "Last name can not be blank!")
    private String name;

    @Min(value = 60, message = "Song's duration must be at least 1 minutes")
    private Integer duration;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private String songUrl;

    @NotEmpty(message = "Song's genre must have at least 1")
    private Set<GenreRequest> genres;

    @NotEmpty(message = "Song's artist must have at least 1")
    private Set<ArtistRequest> artists;

    private AlbumRequest album;
}
