package domain;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

/**
 * Simple SQLite-backed storage for {@link BuildResult} history.
 */
public class Storage {
    private Connection databaseConnection;
    
    /**
     * Creates or opens a SQLite database at the given path and initializes the schema if needed.
     *
     * @param databasePath path to the SQLite database file
     * @throws IOException if the database file cannot be created
     */
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

    /**
     * Stores a build result in the database.
     *
     * @param buildResults the build result to store
     */
    public void storeBuildResult(BuildResult buildResults) {
        String sql = """
            INSERT INTO history(build_idx, date, commit_sha, build_output, success)
            VALUES(NULL, ?, ?, ?, ?)
            """;

        try(var stmt = this.databaseConnection.prepareStatement(sql)) {
            stmt.setString(1, buildResults.date.toInstant().toString());

            stmt.setString(2, buildResults.commitSHA);
            stmt.setString(3, buildResults.buildOutput);
            stmt.setBoolean(4, buildResults.success);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns the stored build indices.
     *
     * @return list of build indices (may be empty)
     */
    public ArrayList<Integer> getBuildIndexes() {
        String sql = """
                SELECT build_idx
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

    /**
     * Returns the build result for the given index.
     *
     * @param buildIndex build index
     * @return the corresponding build result
     * @throws SQLException if the index does not exist or the query fails
     */
    public BuildResult getBuildResult(Integer buildIndex) throws SQLException {
        var sql = """
                SELECT * FROM history
                WHERE build_idx = (?);
                """;
        
        var stmt = this.databaseConnection.prepareStatement(sql);
        stmt.setInt(1, buildIndex);
        var resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            Instant instant = Instant.parse(resultSet.getString("date"));
            Date date = Date.from(instant);

            return new BuildResult(resultSet.getString("commit_sha"), date, resultSet.getString("build_output"), resultSet.getBoolean("success"));
        } else {
            throw new SQLException(String.format("Invalid build index: %d", buildIndex));
        }
    }

    private void initializeDatabase() {
        var sqlStatement = """
            CREATE TABLE IF NOT EXISTS history (
            build_idx INTEGER PRIMARY KEY,
            date TEXT NOT NULL,
            commit_sha TEXT NOT NULL,
            build_output TEXT NOT NULL,
            success BOOLEAN NOT NULL
            );
            """;

        try(var stmt = this.databaseConnection.createStatement()) {
            stmt.execute(sqlStatement);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
