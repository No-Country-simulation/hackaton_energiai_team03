package dev.team3.chillywatts;

import dev.team3.chillywatts.dto.AnaliseEnergeticaRequestDTO;
import dev.team3.chillywatts.dto.AnaliseEnergeticaResponseDTO;
import dev.team3.chillywatts.dto.FreezerItemDTO;
import dev.team3.chillywatts.enums.*;
import dev.team3.chillywatts.service.AnaliseEnergeticaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CenariosSImuladosTests {

    @Autowired
    private AnaliseEnergeticaService analiseService;

    @MockitoBean
    private RestTemplate restTemplate;

    private FreezerItemDTO criarFreezer(String marca, TipoFreezer tipo, TecnologiaFreezer tecnologia, EstadoBorracha borracha, int qtd) {
        return new FreezerItemDTO(marca, tipo, tecnologia, borracha, qtd);
    }

    private void configurarFallbackJava(){
        when(restTemplate.postForObject(anyString(), any(), any(Class.class)))
                .thenThrow(new RuntimeException("ML indsponível"));
    }

    @Test
    void testCasoEficiente() {
        configurarFallbackJava();

        AnaliseEnergeticaRequestDTO request = new AnaliseEnergeticaRequestDTO(    180.0,
                false,
                HorasAltoConsumo.BAIXO,
                EpocaAno.VERAO,
                List.of(
                        criarFreezer("Metalfrio", TipoFreezer.EXIBICAO, TecnologiaFreezer.INVERTER, EstadoBorracha.INTEGRA, 2),
                        criarFreezer("Gelopar", TipoFreezer.EXIBICAO, TecnologiaFreezer.INVERTER, EstadoBorracha.INTEGRA, 2)
                )
        );

        AnaliseEnergeticaResponseDTO response = analiseService.processarAnaliseEnergetica(request);

        assertEquals(CategoriaEnergetica.EFICIENTE, response.getCategoria());
        assertEquals(135.0, response.getCustoEstimadoMensal());
        assertFalse(response.getRecomendacoes().isEmpty());
        assertTrue(response.getRecomendacoes().get(0).contains("Eficiente"));
    }

    @Test
    void testCasoModerado() {
        configurarFallbackJava();

        AnaliseEnergeticaRequestDTO request = new AnaliseEnergeticaRequestDTO(
                450.0,
                true,
                HorasAltoConsumo.MEDIO,
                EpocaAno.VERAO,
                List.of(
                        criarFreezer("Metalfrio", TipoFreezer.EXIBICAO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.INTEGRA, 3),
                        criarFreezer("Gelopar", TipoFreezer.EXIBICAO, TecnologiaFreezer.INVERTER, EstadoBorracha.INTEGRA, 2),
                        criarFreezer("Friginox", TipoFreezer.EXIBICAO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.GASTA, 1)
                )
        );

        AnaliseEnergeticaResponseDTO response = analiseService.processarAnaliseEnergetica(request);

        assertEquals(CategoriaEnergetica.MODERADO, response.getCategoria());
        assertEquals(337.5, response.getCustoEstimadoMensal());
        assertFalse(response.getRecomendacoes().isEmpty());
        assertTrue(response.getRecomendacoes().get(0).contains("Moderado"));
    }

    @Test
    void testCasoIneficiente() {
        configurarFallbackJava();

        AnaliseEnergeticaRequestDTO request = new AnaliseEnergeticaRequestDTO(
                1200.0,
                true,
                HorasAltoConsumo.ALTO,
                EpocaAno.VERAO,
                List.of(
                        criarFreezer("Metalfrio", TipoFreezer.EXIBICAO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.GASTA, 2),
                        criarFreezer("Metalfrio", TipoFreezer.ARMAZENAMENTO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.GASTA, 1),
                        criarFreezer("Gelopar", TipoFreezer.ARMAZENAMENTO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.GASTA, 2),
                        criarFreezer("Electrolux", TipoFreezer.EXIBICAO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.INTEGRA, 2),
                        criarFreezer("Invar", TipoFreezer.ARMAZENAMENTO, TecnologiaFreezer.INVERTER, EstadoBorracha.INTEGRA, 1)
                )
        );

        AnaliseEnergeticaResponseDTO response = analiseService.processarAnaliseEnergetica(request);

        assertEquals(CategoriaEnergetica.INEFICIENTE, response.getCategoria());
        assertEquals(900.0, response.getCustoEstimadoMensal());
        assertFalse(response.getRecomendacoes().isEmpty());
        assertTrue(response.getRecomendacoes().get(0).contains("Ineficiente"));
    }

    @Test
    void testSazonalidadeInverno() {
        configurarFallbackJava();

        AnaliseEnergeticaRequestDTO request = new AnaliseEnergeticaRequestDTO(
                300.0,
                false,
                HorasAltoConsumo.MEDIO,
                EpocaAno.INVERNO,
                List.of(
                        criarFreezer("Metalfrio", TipoFreezer.ARMAZENAMENTO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.INTEGRA, 2)
                )
        );

        AnaliseEnergeticaResponseDTO response = analiseService.processarAnaliseEnergetica(request);

        assertEquals(CategoriaEnergetica.MODERADO, response.getCategoria());
        assertTrue(response.getRecomendacoes().stream()
                .anyMatch(r -> r.contains("inverno")));
    }

    @Test
    void testSazonalidadeVerao() {
        configurarFallbackJava();

        AnaliseEnergeticaRequestDTO request = new AnaliseEnergeticaRequestDTO(
                500.0,
                false,
                HorasAltoConsumo.ALTO,
                EpocaAno.VERAO,
                List.of(
                        criarFreezer("Metalfrio", TipoFreezer.ARMAZENAMENTO, TecnologiaFreezer.CONVENCIONAL, EstadoBorracha.INTEGRA, 2)
                )
        );

        AnaliseEnergeticaResponseDTO response = analiseService.processarAnaliseEnergetica(request);

        assertTrue(response.getRecomendacoes().stream()
                .anyMatch(r -> r.contains("verão")));
    }


}
