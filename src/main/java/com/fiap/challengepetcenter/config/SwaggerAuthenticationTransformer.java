package com.fiap.challengepetcenter.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SwaggerAuthenticationTransformer extends SwaggerIndexPageTransformer {
    public SwaggerAuthenticationTransformer(SwaggerUiConfigProperties config, SwaggerUiOAuthProperties oauth,
                                            SwaggerWelcomeCommon welcome, ObjectMapperProvider mapper) {
        super(config, oauth, welcome, mapper);
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource,
                              ResourceTransformerChain chain) throws IOException {
        Resource transformed = super.transform(request, resource, chain);
        if (!"swagger-initializer.js".equals(resource.getFilename())) {
            return transformed;
        }
        try (var script = new ClassPathResource("swagger/petcenter-auth.js").getInputStream();
             var initializer = transformed.getInputStream()) {
            String content = new String(script.readAllBytes(), StandardCharsets.UTF_8)
                    + "\n" + new String(initializer.readAllBytes(), StandardCharsets.UTF_8);
            return new TransformedResource(resource, content.getBytes(StandardCharsets.UTF_8));
        }
    }
}
