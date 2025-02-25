package hackthevalley.posepik_be.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Value("${server.servlet.context-path:}")
  private String contextPath;

  @Bean
  public OpenAPI customOpenAPI() {
    Server localServer = new Server();
    localServer.setUrl(contextPath);
    localServer.setDescription("Local Server");

    return new OpenAPI()
        .addServersItem(localServer)
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .info(
            new Info()
                .title("Remove Background API - Posepik")
                .version("1.0")
                .description(
                    """
                                    ## 📸 이미지 배경 제거 API 문서

                                    **인증**: JWT 토큰을 사용하세요.

                                    **파일 업로드**: `multipart/form-data`로 이미지를 업로드하세요.

                                    **응답 형식**: `image/png`

                                    """));
  }

  @Bean
  public GroupedOpenApi customGroupedOpenApi() {
    return GroupedOpenApi.builder()
        .group("api")
        .pathsToMatch("/api/**") // 명확하게 파일 업로드 경로 명시
        .build();
  }
}
