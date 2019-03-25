package mockzebra;

import static com.esotericsoftware.minlog.Log.info;
import static com.esotericsoftware.minlog.Log.warn;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database implements ISocketListener
{

    private Connection connection;
    private String insertSql;

    Database(Config config) {
        try {
            info("Connecting to database ...");
            connection = DriverManager.getConnection(config.getDatabaseUrl(), config.getDatabaseUser(), config.getDatabasePassword());
            info("Connected. \\o/");
            executeSql("SET SESSION wait_timeout = 28800");// @todo get from config file (optional)
            executeSql("SET SESSION interactive_timeout = 28800");

            insertSql = "INSERT INTO " + config.getDatabaseTable() + " "
                    + "(" + config.getDatabaseZplDataColumn() + ") "
                    + " VALUES "
                    + "(?)";
        } catch (SQLException ex) {
            warn(ex.getMessage());
        }
    }

    @Override
    public void messageGot(int messageId, String workspace, String zplFile) {
        try {
            String zpl = getZpl(workspace, zplFile, StandardCharsets.UTF_8);
            info("Inserting " + zplFile + " into database ...");
            insertZpl(zpl);

        } catch (IOException ex) {
            warn("Zpl content could not be read from file:" + workspace + "/" + zplFile);
            warn(zplFile + ":" + ex.getMessage());
        }
    }

    void listen(Socket socket) {
        if (connection != null) {
            socket.setListener(this);
        } else {
            info("No active database connection! Database listener won't listen socket.");
        }
    }

    private String getZpl(String workspace, String zplFile, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(workspace + "/" + zplFile));
        return new String(encoded, encoding);
    }

    private void executeSql(String sql) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException ex) {
            warn("SQL EXECUTION ERROR", sql, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    private void insertZpl(String zpl) {
        try {
            PreparedStatement insertPs = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertPs.setString(1, zpl);
            info("Row Count: " + insertPs.executeUpdate());
            try (ResultSet generatedKeys = insertPs.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    info("Identity: " + generatedKeys.getLong(1));
                }
            }
        } catch (SQLException ex) {
            warn("Zpl couldn't be insert into table!", ex);
        }
    }

}
