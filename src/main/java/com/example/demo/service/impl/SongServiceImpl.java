package com.example.demo.service.impl;

import com.example.demo.constant.GenreName;
import com.example.demo.dto.request.ArtistRequest;
import com.example.demo.dto.request.GenreRequest;
import com.example.demo.dto.request.SongRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SearchSongResponse;
import com.example.demo.dto.response.SongResponse;
import com.example.demo.exception.InvalidDataException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.SongMapper;
import com.example.demo.model.Album;
import com.example.demo.model.Artist;
import com.example.demo.model.Genre;
import com.example.demo.model.Song;
import com.example.demo.repository.*;
import com.example.demo.repository.search.SongSearchRepository;
import com.example.demo.repository.specification.SearchOperator;
import com.example.demo.repository.specification.SongSpecificationBuilder;
import com.example.demo.service.SongService;
import com.example.demo.utils.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongSearchRepository songSearchRepository;

    @Override
    @Transactional
    public long addSong(SongRequest request) {
        Song song = songMapper.toSong(request);

        Set<GenreName> genreNames = request.getGenres().stream()
                .map(GenreRequest::getName)
                .collect(Collectors.toSet());

        Set<Genre> genres = genreRepository.findByNameIn(genreNames);
        if (genres.size() != genreNames.size()) {
            throw new ResourceNotFoundException("One or more genres were not found");
        }

        Set<String> artistNames = request.getArtists().stream()
                .map(ArtistRequest::getName)
                .collect(Collectors.toSet());

        Set<Artist> artists = artistRepository.findByNameIn(artistNames);
        if (artists.size() != artistNames.size()) {
            throw new ResourceNotFoundException("One or more artists were not found");
        }

        Album album = albumRepository.findByName(request.getAlbum().getName())
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        genres.forEach(song::saveGenre);
        artists.forEach(song::saveArtist);
        song.saveAlbum(album);

        songRepository.save(song);

        return song.getId();
    }

    @Override
    public SongResponse getSong(Long id) {
        Song song = songRepository.findByIdWithAllFields(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // Song -> SongResponse
        List<Integer> genreIds = song.getGenres().stream()
                .map(Genre::getId)
                .toList();
        List<GenreName> genreNames = song.getGenres().stream()
                .map(Genre::getName)
                .toList();
        List<Long> artistIds = song.getArtists().stream()
                .map(Artist::getId)
                .toList();
        List<String> artistNames = song.getArtists().stream()
                .map(Artist::getName)
                .toList();
        Long albumId = song.getAlbum().getId();
        String albumName = song.getAlbum().getName();

        SongResponse songResponse = songMapper.toSongResponse(song);
        songResponse.setGenreIds(genreIds);
        songResponse.setGenreNames(genreNames);
        songResponse.setArtistIds(artistIds);
        songResponse.setArtistNames(artistNames);
        songResponse.setAlbumId(albumId);
        songResponse.setAlbumName(albumName);

        return songResponse;
    }

    @Override
    @Transactional
    public long updateSong(Long id, SongRequest request) {
        Song song = songRepository.findByIdWithAllFields(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + id));

        String requestSongName = request.getName();

        if (!songRepository.existsByName(requestSongName))
            throw new ResourceNotFoundException("Song not found with name: " + requestSongName);

        if (!requestSongName.equals(song.getName()))
            throw new InvalidDataException("Request song name does not match with song name with id: " + id);

        songMapper.updateSong(song, request);

        // Delete genres, artists, album
        song.getGenres().forEach(genre -> genre.getSongs().remove(song));
        song.getGenres().clear();
        song.getArtists().forEach(artist -> artist.getSongs().remove(song));
        song.getArtists().clear();
        if (song.getAlbum() != null) {
            song.getAlbum().setSongs(null);
            song.setAlbum(null);
        }

        Set<GenreName> genreNames = request.getGenres().stream()
                .map(GenreRequest::getName)
                .collect(Collectors.toSet());

        Set<Genre> genres = genreRepository.findByNameIn(genreNames);
        if (genres.size() != genreNames.size()) {
            throw new ResourceNotFoundException("One or more genres were not found");
        }

        Set<String> artistNames = request.getArtists().stream()
                .map(ArtistRequest::getName)
                .collect(Collectors.toSet());

        Set<Artist> artists = artistRepository.findByNameIn(artistNames);
        if (artists.size() != artistNames.size()) {
            throw new ResourceNotFoundException("One or more artists were not found");
        }

        Album album = albumRepository.findByName(request.getAlbum().getName())
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));

        genres.forEach(song::saveGenre);
        artists.forEach(song::saveArtist);
        song.saveAlbum(album);

        return song.getId();
    }

    @Override
    public long deleteSong(Long id) {
        if (songRepository.existsById(id))
            songRepository.deleteById(id);
        return id;
    }

    @Override
    public PageResponse<?> getAllSongs(int pageNo, int pageSize, String sortBy) {
        Sort sort = SortUtil.resolveSortBy(sortBy, "likeCount");

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Long> ids = songRepository.findAllIds(pageable);

        List<Song> songs = songRepository.findAllByIdsAndSort(ids.toList(), sort);

        List<SearchSongResponse> searchSongResponses = songsToSearchSongResponses(songs);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(ids.getTotalPages())
                .items(searchSongResponses)
                .build();
    }

    @Override
    public PageResponse<?> getSongsByName(int pageNo, int pageSize, String sortBy, String name) {
        Sort sort = SortUtil.resolveSortBy(sortBy, "likeCount");

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Long> ids = songRepository.findAllIdsByName(name, pageable);

        List<Song> songs = songRepository.findAllByIdsAndSort(ids.toList(), sort);

        List<SearchSongResponse> searchSongResponses = songsToSearchSongResponses(songs);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(ids.getTotalPages())
                .items(searchSongResponses)
                .build();
    }

    @Override
    public PageResponse<?> getSongsByGenre(int pageNo, int pageSize, String sortBy, Integer genreId) {
        Sort sort = SortUtil.resolveSortBy(sortBy, "likeCount");

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Long> ids = songRepository.findAllIdsByGenre(genreId, pageable);

        List<Song> songs = songRepository.findAllByIdsAndSort(ids.getContent(), sort);

        List<SearchSongResponse> searchSongResponses = songsToSearchSongResponses(songs);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(ids.getTotalPages())
                .items(searchSongResponses)
                .build();
    }

    @Override
    public PageResponse<?> sortAndSpecificationSearch(int pageNo, int pageSize, String sortBy, String search) {

        SongSpecificationBuilder builder = new SongSpecificationBuilder();

        if (StringUtils.hasLength(search)) {
            Pattern pattern = Pattern.compile("([,']?)(\\w+)(!:|!~|!=|>=|<=|[:~=><])(\\w+)");
            Matcher matcher = pattern.matcher(search);

            while (matcher.find()) {
                String andOrLogic = matcher.group(1);
                String key = matcher.group(2);
                String operator = matcher.group(3);
                String value = matcher.group(4);

                if (andOrLogic != null && (andOrLogic.equals(SearchOperator.AND_OPERATOR) ||
                        andOrLogic.equals(SearchOperator.OR_OPERATOR))) {
                    builder.with(andOrLogic, key, operator, value, null, null);
                } else {
                    builder.with(key, operator, value, null, null);
                }
            }
        }

        Sort sort = SortUtil.resolveSortBy(sortBy, "likeCount");

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Long> ids = songSearchRepository.findIdsBySpecification(builder.build(), pageable);

        List<Song> songs = songRepository.findAllByIdsAndSort(ids.getContent(), sort);

        List<SearchSongResponse> searchSongResponses = songsToSearchSongResponses(songs);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(ids.getTotalPages())
                .items(searchSongResponses)
                .build();
    }

    // List<Song> -> List<SearchSongResponse>
    private List<SearchSongResponse> songsToSearchSongResponses(List<Song> songs) {
        return songs.stream()
                .map(song -> {
                    List<Long> artistIds = song.getArtists().stream()
                            .map(Artist::getId)
                            .toList();
                    List<String> artistNames = song.getArtists().stream()
                            .map(Artist::getName)
                            .toList();

                    SearchSongResponse searchSongResponse = songMapper.toSearchSongResponse(song);
                    searchSongResponse.setArtistIds(artistIds);
                    searchSongResponse.setArtistNames(artistNames);

                    return searchSongResponse;
                })
                .toList();
    }
}
