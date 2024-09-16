package com.example.demo.repository;

import com.example.demo.constant.GenreName;
import com.example.demo.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Optional<Genre> findByName(GenreName name);
    @Query("SELECT g FROM Genre g WHERE g.name IN :names")
    Set<Genre> findByNameIn(@Param("names") Set<GenreName> names);
}
