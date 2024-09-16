package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "song")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Song extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "duration")
    private int duration;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "song_url", length = 512)
    private String songUrl;

    @Column(name = "like_count")
    private long likeCount = 0;

    @ManyToMany(mappedBy = "songs")
    private Set<Genre> genres;

    @ManyToMany
    @JoinTable(
            name = "artist_songs",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id"))
    private Set<Artist> artists = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToMany
    @JsonIgnore
    private Set<Playlist> playlists = new HashSet<>();

    @OneToMany(mappedBy = "song")
    @JsonIgnore
    private Set<ListeningHistory> listeningHistories = new HashSet<>();

    public void saveGenre(Genre genre) {
        if (genre != null) {
            if (this.genres == null) {
                this.genres = new HashSet<>();
            }
            if (genre.getSongs() == null) {
                genre.setSongs(new HashSet<>());
            }
            this.genres.add(genre);
            genre.getSongs().add(this);
        }
    }

    public void saveArtist(Artist artist) {
        if (artist != null) {
            if (this.artists == null) {
                this.artists = new HashSet<>();
            }
            if (artist.getSongs() == null) {
                artist.setSongs(new HashSet<>());
            }
            this.artists.add(artist);
            artist.getSongs().add(this);
        }
    }

    public void saveAlbum(Album album) {
        if (album != null) {
            if (album.getSongs() == null) {
                album.setSongs(new HashSet<>());
            }
            this.album = album;
            album.getSongs().add(this);
        }
    }
}
