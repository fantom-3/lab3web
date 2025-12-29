package dao;

import config.DbConfig;
import model.ShotResult;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShotResultDao {

    private final DbConfig dbConfig;

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS shot_results (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "x DOUBLE PRECISION NOT NULL," +
                    "y DOUBLE PRECISION NOT NULL," +
                    "r DOUBLE PRECISION NOT NULL," +
                    "hit BOOLEAN NOT NULL," +
                    "check_time TIMESTAMP NOT NULL," +
                    "exec_time_ns BIGINT NOT NULL," +
                    "session_id TEXT NOT NULL" +
                    ")";

    private static final String INSERT_SQL =
            "INSERT INTO shot_results (x, y, r, hit, check_time, exec_time_ns, session_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_SESSION_SQL =
            "SELECT x, y, r, hit, check_time, exec_time_ns, session_id " +
                    "FROM shot_results " +
                    "WHERE session_id = ? " +
                    "ORDER BY id DESC";

    private static final String DELETE_BY_SESSION_SQL =
            "DELETE FROM shot_results WHERE session_id = ?";

    public ShotResultDao(DbConfig dbConfig) {
        if (dbConfig == null) {
            throw new IllegalArgumentException("dbConfig is null");
        }
        this.dbConfig = dbConfig;
        ensureTableExists();
    }

    public void ensureTableExists() {
        try (Connection c = dbConfig.openConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create/check table shot_results: " + e.getMessage(), e);
        }
    }

    public void save(ShotResult r) {
        if (r == null) return;

        try (Connection c = dbConfig.openConnection();
             PreparedStatement ps = c.prepareStatement(INSERT_SQL)) {

            ps.setDouble(1, r.getX());
            ps.setDouble(2, r.getY());
            ps.setDouble(3, r.getR());
            ps.setBoolean(4, r.isHit());

            LocalDateTime time = r.getCheckTime();
            if (time == null) time = LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(time));

            ps.setLong(6, r.getExecTimeNs());

            String sessionId = r.getSessionId();
            if (sessionId == null) sessionId = "unknown";
            ps.setString(7, sessionId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert into shot_results: " + e.getMessage(), e);
        }
    }

    public List<ShotResult> findAllBySession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = "unknown";
        }

        List<ShotResult> list = new ArrayList<>();
        try (Connection c = dbConfig.openConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_SESSION_SQL)) {

            ps.setString(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ShotResult r = new ShotResult();
                    r.setX(rs.getDouble("x"));
                    r.setY(rs.getDouble("y"));
                    r.setR(rs.getDouble("r"));
                    r.setHit(rs.getBoolean("hit"));

                    Timestamp ts = rs.getTimestamp("check_time");
                    if (ts != null) {
                        r.setCheckTime(ts.toLocalDateTime());
                    }

                    r.setExecTimeNs(rs.getLong("exec_time_ns"));
                    r.setSessionId(rs.getString("session_id"));

                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to select from shot_results: " + e.getMessage(), e);
        }

        return list;
    }

    public void deleteBySession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = "unknown";
        }

        try (Connection c = dbConfig.openConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_BY_SESSION_SQL)) {

            ps.setString(1, sessionId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete by session_id: " + e.getMessage(), e);
        }
    }
}