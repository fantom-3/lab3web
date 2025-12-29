package config;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String url;
    private String user;
    private String password;

    public DbConfig() {}


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection openConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {}

        if (url == null || url.isBlank()) {
            throw new SQLException("DB url is empty (check beans-config.xml dbConfig.url)");
        }
        if (user == null) user = "";
        if (password == null) password = "";

        return DriverManager.getConnection(url.trim(), user, password);
    }
}