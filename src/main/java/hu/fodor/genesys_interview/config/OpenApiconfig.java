package hu.fodor.genesys_interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiconfig {
    @Bean
    public OpenAPI userManagementAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("User management API")
                        .description("Simple REST API for user CRUD")
                        .version("1.0")
                );
    }
}
