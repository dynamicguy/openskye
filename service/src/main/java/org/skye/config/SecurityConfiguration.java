package org.skye.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Controls how Skye users are authenticated
 */

@Data
public class SecurityConfiguration {
    @NotNull
    private String model; // accepted values "BASIC_AUTH", "SSL", "ANON"
}
