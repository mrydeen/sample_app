package com.smartgridz.config;

import com.smartgridz.domain.entity.FileType;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.io.IOException;

/**
 * This class should only be used during development to help code the thymeleaf UI.  It
 * will make it so that the files are not cached so when you make a change on the backend,
 * it will pick up the latest changes.
 */
@Configuration
@Profile("local")
public class LocalDevConfig {

    /**
     * Point the template engine to the actual templates.  Thus when you make
     * changes, they will show up in the UI immediatly.
     *
     * @param templateEngine
     * @throws IOException
     */
    public LocalDevConfig(final TemplateEngine templateEngine) throws IOException {
        File sourceRoot = new ClassPathResource("application.properties").getFile().getParentFile();
        while (sourceRoot.listFiles((dir, name) -> name.equals("mvnw")).length != 1) {
            sourceRoot = sourceRoot.getParentFile();
        }

        final FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setPrefix(sourceRoot.getPath() + "/src/main/resources/templates/");
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setCacheable(false);
        fileTemplateResolver.setCharacterEncoding("UTF-8");
        fileTemplateResolver.setCheckExistence(true);

        templateEngine.setTemplateResolver(fileTemplateResolver);
    }
}
