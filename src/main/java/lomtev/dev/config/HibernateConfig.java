package lomtev.dev.config;

import lomtev.dev.model.Account;
import lomtev.dev.model.User;
import lomtev.dev.properties.DataBaseProperties;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    private final DataBaseProperties dataBaseProperties;

    public HibernateConfig(DataBaseProperties dataBaseProperties) {
        this.dataBaseProperties = dataBaseProperties;
    }

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class)
                .addPackage("lomtev.dev")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", dataBaseProperties.getConnectionUrl())
                .setProperty("hibernate.connection.username", dataBaseProperties.getUsername())
                .setProperty("hibernate.connection.password", dataBaseProperties.getPassword())
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                .setProperty("hibernate.highlight_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "update");
        return configuration.buildSessionFactory();
    }
}
