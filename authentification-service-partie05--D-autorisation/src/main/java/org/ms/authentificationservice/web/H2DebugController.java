package org.ms.authentificationservice.web;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class H2DebugController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/debug/h2-datasource")
    public String showDataSourceInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataSource class: ").append(dataSource.getClass().getName()).append("\n");
        if (dataSource instanceof HikariDataSource hds) {
            sb.append("JdbcUrl: ").append(hds.getJdbcUrl()).append("\n");
            sb.append("Username: ").append(hds.getUsername()).append("\n");
        }
        try (Connection conn = dataSource.getConnection()) {
            sb.append("✅ Connexion DataSource OK.\n");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            int userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM APP_USER", Integer.class);
            sb.append("Nombre d'utilisateurs dans APP_USER : ").append(userCount).append("\n");
        } catch (SQLException e) {
            sb.append("❌ Échec connexion DataSource : ").append(e.getMessage()).append("\n");
        }
        return sb.toString().replace("\n", "<br>");
    }
}
