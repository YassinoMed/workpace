package org.ms.authentificationservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Console SQL de remplacement pour H2.
 * Utilise directement le DataSource Spring — pas de problème de credentials.
 * Accès : GET  /sql/tables        → liste les tables
 *         GET  /sql/query?q=...   → exécute une requête SELECT
 */
@RestController
@RequestMapping("/sql")
public class H2ConsoleController {

    private final JdbcTemplate jdbc;

    @Autowired
    public H2ConsoleController(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @GetMapping("/tables")
    public List<Map<String, Object>> tables() {
        return jdbc.queryForList(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'"
        );
    }

    @GetMapping("/query")
    public List<Map<String, Object>> query(@RequestParam String q) {
        return jdbc.queryForList(q);
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return jdbc.queryForList("SELECT ID, USERNAME FROM APP_USER");
    }

    @GetMapping("/roles")
    public List<Map<String, Object>> roles() {
        return jdbc.queryForList("SELECT * FROM APP_ROLE");
    }

    @GetMapping("/user-roles")
    public List<Map<String, Object>> userRoles() {
        return jdbc.queryForList(
            "SELECT U.USERNAME, R.ROLE_NAME FROM APP_USER U " +
            "JOIN APP_USER_ROLES UR ON U.ID = UR.APP_USER_ID " +
            "JOIN APP_ROLE R ON R.ID = UR.ROLES_ID"
        );
    }
}
