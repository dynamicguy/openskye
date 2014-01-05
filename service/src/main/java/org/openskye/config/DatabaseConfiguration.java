package org.openskye.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DatabaseConfiguration {

    @NotNull
    private String driverClass;
    private String user;
    private String password;
    private String dialect;
    @NotNull
    private String url;
    
    private String connectionProviderClass;
    
    // hibernate.c3p0.max_size
    private Integer poolMaxSize;
    // hibernate.c3p0.min_size
    private Integer poolMinSize;
    // hibernate.c3p0.timeout
    private Integer poolTimeOut;
    // hibernate.c3p0.idle_test_period
    private Integer poolIdleTestPeriod;
    // hibernate.c3p0.preferredTestQuery
    private String poolPreferredTestQuery;
    // hibernate.c3p0.testConnectionOnCheckout
    private String poolTestConnectionOnCheckout;
    
    private Integer poolConnectionMaxIdleTime;
    
    private Integer maxIdleTimeExcessConnections;
    
    private Integer unreturnedConnectionTimeout;

}
