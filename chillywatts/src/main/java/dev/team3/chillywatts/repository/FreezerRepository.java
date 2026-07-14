package dev.team3.chillywatts.repository;

import dev.team3.chillywatts.freezer.Freezer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreezerRepository extends JpaRepository<Freezer, Long> {

    Optional<Freezer> findByMarcaContainingIgnoreCase(String marca);
}
