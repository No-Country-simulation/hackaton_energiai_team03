package dev.team3.chillywatts.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI.
 * Define as informações que aparecem na documentação interativa em /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

    /** Configura o Swagger com título, descrição, versão e contato da equipe. */
    @Bean
    public OpenAPI chillwattsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChillyWatts API")
                        .description("API de Análise Energética para Sorveterias - MVP do Hackathon")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe ChillyWatts")
                                .email("contato@chillywatts.com")));
    }
}
