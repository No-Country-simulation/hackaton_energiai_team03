package dev.team3.chillywatts.service;

import dev.team3.chillywatts.entity.AnaliseHistorico;
import dev.team3.chillywatts.repository.AnaliseHistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsável pelo CRUD da tabela analise_historico.
 * Camada de acesso ao banco via {@link AnaliseHistoricoRepository}.
 */
@Service
public class HistoricoService {

    @Autowired
    private AnaliseHistoricoRepository analiseHistoricoRepository;

    /** Salva uma nova análise ou atualiza uma existente no banco. */
    public AnaliseHistorico salvar(AnaliseHistorico historico) { return analiseHistoricoRepository.save(historico); }

    /** Busca todas as análises de um CNPJ específico (usado pelo chatbot). */
    public List<AnaliseHistorico> buscarHistoricoPorCnpj(String cnpj) {
        return analiseHistoricoRepository.findByCnpj(cnpj);
    }

    /**
     * Atualiza nome e CNPJ de uma análise já salva.
     * Usado pelo chatbot depois que o usuário informa seus dados pessoais.
     * Lança RuntimeException se o ID não for encontrado
     */
    public AnaliseHistorico atualizarNomeCpnj(Long id, String nome, String cnpj) {
        AnaliseHistorico historico = analiseHistoricoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Análise não encontrada com id: " + id));
        historico.setNome(nome);
        historico.setCnpj(cnpj);
        return analiseHistoricoRepository.save(historico);
    }

    /** Lista todas as análises salvas no banco (sem filtro). */
    public List<AnaliseHistorico> listarTodosHistoricos() { return analiseHistoricoRepository.findAll();}
}
