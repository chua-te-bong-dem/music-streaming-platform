package com.example.demo.dto.request;

import com.example.demo.constant.GenreName;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenreRequest {
    private GenreName name;
}
