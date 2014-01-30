package org.openskye.config;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Data
public class SkyeDatabaseConfiguration implements DatabaseConfiguration {

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

    @Override
    public DataSourceFactory getDataSourceFactory(Configuration configuration) {
        DataSourceFactory dsf = new DataSourceFactory();
        SkyeDatabaseConfiguration skyeDatabaseConfiguration = ((SkyeConfiguration) configuration).getDatabaseConfiguration();
        dsf.setUrl(skyeDatabaseConfiguration.getUrl());
        dsf.setUser(skyeDatabaseConfiguration.getUser());
        dsf.setPassword(skyeDatabaseConfiguration.getPassword());
        dsf.setDriverClass(skyeDatabaseConfiguration.getDriverClass());

        HashMap<String, String> props = new HashMap<>();
        props.put("hibernate.dialect", skyeDatabaseConfiguration.getDialect());
        props.put("hibernate.connection.provider_class", skyeDatabaseConfiguration.getConnectionProviderClass());
        props.put("hibernate.c3p0.max_size", String.valueOf(skyeDatabaseConfiguration.getPoolMaxSize()));
        props.put("hibernate.c3p0.min_size", String.valueOf(skyeDatabaseConfiguration.getPoolMinSize()));
        props.put("hibernate.c3p0.timeout", String.valueOf(skyeDatabaseConfiguration.getPoolTimeOut()));
        props.put("hibernate.c3p0.idle_test_period", String.valueOf(skyeDatabaseConfiguration.getPoolIdleTestPeriod()));
        props.put("hibernate.c3p0.preferredTestQuery", String.valueOf(skyeDatabaseConfiguration.getPoolPreferredTestQuery()));
        props.put("hibernate.c3p0.testConnectionOnCheckout", String.valueOf(skyeDatabaseConfiguration.getPoolTestConnectionOnCheckout()));
        props.put("hibernate.c3p0.poolConnectionMaxIdleTime", String.valueOf(skyeDatabaseConfiguration.getPoolConnectionMaxIdleTime()));
        props.put("hibernate.c3p0.maxIdleTimeExcessConnections", String.valueOf(skyeDatabaseConfiguration.getMaxIdleTimeExcessConnections()));
        props.put("hibernate.c3p0.unreturnedConnectionTimeout", String.valueOf(skyeDatabaseConfiguration.getUnreturnedConnectionTimeout()));
        dsf.setProperties(props);
        return dsf;
    }
}
