package com.example.demo.dto.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class ArtistResponse implements Serializable {
    private String name;
    private String bio;
    private String imageUrl;
    private Long followers;
    private List<Long> songIds;
    private List<String> songNames;
    private List<Long> albumIds;
    private List<String> albumNames;
}
