package domain;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Storage {
    private Connection databaseConnection;
    
    public Storage(String databasePath) throws IOException {
        var sqlite_file = new File(databasePath);

        if (!sqlite_file.exists()) {
            sqlite_file.createNewFile();
        }

        var url = "jdbc:sqlite:" + databasePath;

        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Connection to SQLite has been established.");
            this.databaseConnection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (this.databaseConnection != null) {
            this.initializeDatabase();
            System.out.println("Database migrations complete.");
        }
    }

    public void storeBuildResult(BuildResult buildResults) {
        String sql = """
            INSERT INTO history(build_nb, commit_sha, build_output)
            VALUES(NULL, ?, ?)
            """;

        try(var stmt = this.databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, buildResults.commitSHA);
            stmt.setString(2, buildResults.buildOutput);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Integer> getBuildIndexes() {
        String sql = """
                SELECT build_nb
                FROM history
                """;

        try (var stmt = this.databaseConnection.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);
            ArrayList<Integer> buildIndexes = new ArrayList<Integer>();

            while (resultSet.next()) {
                buildIndexes.add(resultSet.getInt(1)); // We get the index for each row
            }

            return buildIndexes;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new ArrayList<Integer>();
        }
    }

    public BuildResult getBuildResult(Integer buildIndex) throws SQLException {
        var sql = """
                SELECT * FROM history
                WHERE build_nb = (?);
                """;
        
        var stmt = this.databaseConnection.prepareStatement(sql);
        stmt.setInt(1, buildIndex);
        var resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            return new BuildResult(resultSet.getString("commit_sha"), resultSet.getString("build_output"));
        } else {
            throw new SQLException(String.format("Invalid build index: %d", buildIndex));
        }
    }

    private void initializeDatabase() {
        var sqlStatement = """
            CREATE TABLE IF NOT EXISTS history (
            build_nb INTEGER PRIMARY KEY,
            commit_sha TEXT NOT NULL,
            build_output TEXT NOT NULL
            );
            """;

        try(var stmt = this.databaseConnection.createStatement()) {
            stmt.execute(sqlStatement);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
