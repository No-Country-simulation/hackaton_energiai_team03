package dev.team3.chillywatts.repository;

import dev.team3.chillywatts.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository da tabela inventario.
 * Um CNPJ tem apenas 1 inventário (upsert pelo campo UNIQUE).
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    /** Busca inventário pelo CNPJ. Retorna vazio se não existe. */
    Optional<Inventario> findByCnpj(String cnpj);
}
