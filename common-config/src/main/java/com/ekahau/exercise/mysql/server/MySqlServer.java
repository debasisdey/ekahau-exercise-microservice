package com.ekahau.exercise.mysql.server;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.joining;
import static java.util.Arrays.asList;

public class MySqlServer {

    public final int configuredPort = 3306;

    private DB db;

    private final DBConfigurationBuilder configBuilder;

    public File appDir;

    private static final String username = "ekahau_exercise";

    private static final String password = "ekahau_exercise";

    private static final String schemaName = "ekahau_exercise";



    public MySqlServer() {
        this(Integer.valueOf(System.getProperty("mysql.port", "0")));
    }

    public MySqlServer(int configuredPort) {
        String path = System.getProperty("java.io.tmpdir") + "/mysql-" + UUID.randomUUID();
        this.deleteDirectory(path);
        this.appDir = new File(path);
        this.configBuilder = DBConfigurationBuilder.newBuilder();
        this.configBuilder.setPort((configuredPort > 0) ? configuredPort : this.configuredPort);
        this.configBuilder.setDataDir(this.appDir.getAbsolutePath());
        this.configBuilder.addArg("--character-set-server=latin1");
    }

    public static void main(String... args) {
        MySqlServer server = new MySqlServer();
        server.start();
    }

    public void start() {
        int attempt = 0;
        boolean started = false;

        while (!started && attempt < 10) {
            try {
                this.tryStart();
                started = true;
            }
            catch (ManagedProcessException mpe) {
                ++attempt;
                System.out.println("Unable to start mariadb #" + attempt + ": " + mpe.getMessage());
            }
        }

    }

    public Integer getActualPort() {
        return this.configBuilder.getPort();
    }

    public void stop() {
        System.out.println("Mysql server shutting down..");

        try {
            if (this.db != null) {
                this.db.stop();
            }
        }
        catch (ManagedProcessException mpe) {
            throw new RuntimeException(mpe);
        }

        this.deleteDirectory(this.appDir.getAbsolutePath());
    }

    private String getStatement(String tableName) throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("sqls/" + tableName + ".sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                    .collect(joining(System.lineSeparator()));
        }
    }

    private void tryStart() throws ManagedProcessException {
        this.db = DB.newEmbeddedDB(this.configBuilder.build());
        this.db.start();
        this.createDataBases(asList(schemaName));
        System.out.println("\tmysql  --database "
                + schemaName
                + " -u"
                + username
                + " -p"
                + password
                + " -h 127.0.0.1 -P"
                + this.getActualPort());

        Arrays.stream(Tables.values())
                .map(Tables::getTableName)
                .forEach(tableName -> {
                    try {
                        createOrTruncateTable(tableName);
                    }
                    catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void deleteDirectory(String file) {
        FileUtils.deleteQuietly(new File(file));
    }

    private void createOrTruncateTable(String tableName) throws SQLException, IOException {
        Connection conn =
                DriverManager.getConnection("jdbc:mysql://localhost:3306/ekahau_exercise", username, password);
        Statement statement = conn.createStatement();
        statement.addBatch(getStatement(tableName));
        statement.executeBatch();
    }

    private void createDataBases(List<String> schemaNames) {
        schemaNames.forEach(database -> {
            String url = this.configBuilder.getURL(database) + "?createDatabaseIfNotExist=true";
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url, username, password);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
