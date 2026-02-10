package lomtev.dev.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataBaseProperties {
    private final String connectionUrl;
    private final String username;
    private final String password;

    public DataBaseProperties(
            @Value("${db.connection-url}") String connectionUrl,
            @Value("${db.username}") String username,
            @Value("${db.password}") String password) {
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
