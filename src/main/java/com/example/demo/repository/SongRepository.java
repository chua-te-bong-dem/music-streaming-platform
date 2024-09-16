package com.example.demo.repository;

import com.example.demo.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long>, JpaSpecificationExecutor<Song> {
    boolean existsByName(String name);
    @EntityGraph(attributePaths = {"genres", "artists", "album"})
    @Query("SELECT s FROM Song s " +
            "WHERE s.id = :id")
    Optional<Song> findByIdWithAllFields(@Param("id") Long id);
    @Query("SELECT s.id FROM Song s")
    Page<Long> findAllIds(Pageable pageable);
    @EntityGraph(attributePaths = {"genres", "artists", "album"})
    @Query("SELECT s FROM Song s " +
            "WHERE s.id IN :ids")
    List<Song> findAllByIdsAndSort(@Param("ids") List<Long> ids, Sort sort);
    @Query("SELECT s.id FROM Song s " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Long> findAllIdsByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT s.id FROM Song s JOIN s.genres g WHERE g.id = :genreId")
    Page<Long> findAllIdsByGenre(@Param("genreId") Integer genreId, Pageable pageable);
}
