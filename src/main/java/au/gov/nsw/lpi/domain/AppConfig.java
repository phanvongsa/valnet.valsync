package au.gov.nsw.lpi.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    @Bean
    public DataSource dataSource(Environment env) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getProperty("db.driverClassName"));
        ds.setUrl(env.getProperty("db.url"));
        ds.setUsername(env.getProperty("db.username"));
        ds.setPassword(env.getProperty("db.password"));
        return ds;
    }

    @Bean
    public ServerConfig serverConfig(Environment env) {
        ServerConfig cfg = new ServerConfig();
        cfg.init(env);
        return cfg;
    }

    @Bean
    public PegaConfig pegaConfig(Environment env) {
        PegaConfig cfg = new PegaConfig();
        cfg.init(env);
        return cfg;
    }

}
