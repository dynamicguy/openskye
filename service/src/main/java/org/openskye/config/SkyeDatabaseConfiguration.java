package org.openskye.config;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import lombok.Data;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Data
public class SkyeDatabaseConfiguration implements DatabaseConfiguration {

    protected static StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
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
        dsf.setDriverClass(skyeDatabaseConfiguration.getDriverClass());

        HashMap<String, String> props = new HashMap<>();
        props.put("connection.password", skyeDatabaseConfiguration.getPassword());
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

    public String getPassword() {
        if (!encryptor.isInitialized()) {
            encryptor.setPassword("openSkye12");
            FixedStringSaltGenerator saltGenerator = new FixedStringSaltGenerator();
            saltGenerator.setSalt("openSkye12");
            encryptor.setSaltGenerator(saltGenerator);
            encryptor.setAlgorithm("PBEWithMD5AndDES");
        }
        return encryptor.decrypt(password);
    }

    public void setPassword(String newPassword) {
        if (password == null) {
            //if newPassword is plaintext
            if (!isPasswordHashed(newPassword)) {
                password = hashPw(newPassword);
            } else {
                password = newPassword;
            }
        }
    }

    public String hashPw(String newPassword) {
        if (!encryptor.isInitialized()) {
            encryptor.setPassword("openSkye12");
            FixedStringSaltGenerator saltGenerator = new FixedStringSaltGenerator();
            saltGenerator.setSalt("openSkye12");
            encryptor.setSaltGenerator(saltGenerator);
            encryptor.setAlgorithm("PBEWithMD5AndDES");
        }
        return encryptor.encrypt(newPassword);
    }

    public boolean isPasswordHashed(String testPass) {
        if (!encryptor.isInitialized()) {
            encryptor.setPassword("openSkye12");
            FixedStringSaltGenerator saltGenerator = new FixedStringSaltGenerator();
            saltGenerator.setSalt("openSkye12");
            encryptor.setSaltGenerator(saltGenerator);
            encryptor.setAlgorithm("PBEWithMD5AndDES");
        }
        try {
            encryptor.decrypt(testPass);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

}
