package dev.team3.chillywatts.repository;

import dev.team3.chillywatts.entity.Freezer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreezerRepository extends JpaRepository<Freezer, Long> {

    List<Freezer> findByMarcaContainingIgnoreCase(String marca);
}
