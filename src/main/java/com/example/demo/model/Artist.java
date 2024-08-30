package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "artist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Artist extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "bio", length = 2048)
    private String bio;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @ManyToMany
    @JsonIgnore
    private Set<Song> songs = new HashSet<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Album> albums = new HashSet<>();

}
