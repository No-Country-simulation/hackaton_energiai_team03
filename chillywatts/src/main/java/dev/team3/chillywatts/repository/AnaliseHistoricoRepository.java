package dev.team3.chillywatts.repository;

import dev.team3.chillywatts.entity.AnaliseHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository da tabela analise_historico.
 * Estende JpaRepository que fornece operações CRUD automáticas (save, findById, findAll, deleteById).
 * Métodos customizados são criados por convenção de nomes do Spring Data JPA.
 */
@Repository
public interface AnaliseHistoricoRepository extends JpaRepository<AnaliseHistorico, Long> {

    /**
     * Busca todas as análises de um CNPJ específico.
     * O Spring Data gera automaticamente a query:
     *   SELECT * FROM analise_historico WHERE cnpj = ?
     */
    List<AnaliseHistorico> findByCnpj(String cnpj);
}
