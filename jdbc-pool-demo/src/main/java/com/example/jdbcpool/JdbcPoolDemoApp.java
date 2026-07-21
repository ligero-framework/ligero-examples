package com.example.jdbcpool;

import com.ligero.Ligero;
import com.ligero.jdbc.DataSources;
import com.ligero.jdbc.Jdbc;
import com.ligero.jdbc.RowMapper;

import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;

/**
 * A pooled DataSource (HikariCP) via DataSources.pooled(...), driving a small
 * Task CRUD with Jdbc. GET /pool-stats exposes the live pool gauges.
 */
public final class JdbcPoolDemoApp {

    public record Task(long id, String title, boolean done) { }

    private static final RowMapper<Task> AS_TASK =
        r -> new Task(r.getLong("id"), r.getString("title"), r.getBoolean("done"));

    public static void main(String[] args) throws Exception {
        HikariDataSource ds = DataSources.pooled(
            "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1", "sa", "", cfg -> {
                cfg.setMaximumPoolSize(10);
                cfg.setPoolName("demo-pool");
            });

        Jdbc db = new Jdbc(ds);
        db.update("create table tasks(id bigint auto_increment primary key, "
            + "title varchar(200), done boolean default false)");
        db.insert("insert into tasks(title) values (?)", "Read the pooling guide");

        Ligero app = Ligero.create(8080);

        app.get("/tasks", ctx -> ctx.json(db.query("select * from tasks order by id", AS_TASK)));

        app.post("/tasks", ctx -> {
            String title = String.valueOf(ctx.body(Map.class).get("title"));
            long id = db.insert("insert into tasks(title) values (?)", title);
            ctx.status(201).json(Map.of("id", id));
        });

        app.get("/pool-stats", ctx -> ctx.json(Map.of(
            "active", ds.getHikariPoolMXBean().getActiveConnections(),
            "idle", ds.getHikariPoolMXBean().getIdleConnections(),
            "total", ds.getHikariPoolMXBean().getTotalConnections())));

        app.onStop(ds::close);
        app.start();
    }
}
