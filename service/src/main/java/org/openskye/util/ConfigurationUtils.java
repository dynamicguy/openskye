package org.openskye.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import io.dropwizard.setup.Bootstrap;
import org.openskye.config.SkyeConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConfigurationUtils {
    /*
        Rewrite the yaml if the password has been modified
     */
    public static void ensureDatabasePasswordIsEncryptedOnDisk(SkyeConfiguration configuration, Bootstrap bootstrap, String argsPath) throws IOException {
        try (InputStream input = bootstrap.getConfigurationSourceProvider().open(checkNotNull(argsPath))) {
            ObjectMapper om = bootstrap.getObjectMapper().copy();
            YAMLParser yamlParser = new YAMLFactory().createParser(input);

            final JsonNode node = om.readTree(yamlParser);
            String oldPassword = node.get("database").get("password").asText();
            yamlParser.close();

            if (oldPassword.equals(configuration.getDatabaseConfiguration().getPassword())) {
                // Get encrypted password and set it in the in-memory configuration
                String newPassword = configuration.getDatabaseConfiguration().hashPw(oldPassword);
                configuration.getDatabaseConfiguration().setPassword(newPassword);

                // Works, but yaml comments are lost
                // ((ObjectNode) node.get("database")).put("password", newPassword);
                // YAMLGenerator yamlGenerator = yamlFactory.createGenerator(Files.newOutputStream(path));
                // yamlGenerator.setCodec(om);
                // yamlGenerator.writeTree(node);
                // yamlGenerator.close();

                // Find the line in the yaml config with the password
                Charset charset = StandardCharsets.UTF_8;
                Path path = Paths.get(argsPath);
                String content = new String(Files.readAllBytes(path), charset);
                String passwordString = "password: ";
                String passwordToEof = content.substring(content.lastIndexOf(passwordString), content.length());
                String passwordToEol = passwordToEof.substring(0, passwordToEof.indexOf(System.getProperty("line.separator")));

                // Overwrite the line in the yaml config with the new encrypted password
                String newContent = passwordString + newPassword;
                content = content.replace(passwordToEol, newContent);
                Files.write(path, content.getBytes(charset));
            }
        }
    }
}
