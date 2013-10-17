package org.openskye.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DatabaseConfiguration {

    @NotNull
    private String driverClass;
    private String user;
    private String password;
    @NotNull
    private String url;

}
