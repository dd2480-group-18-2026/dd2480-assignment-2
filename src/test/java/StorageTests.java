import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class StorageTests {
    @Test
    void storeBuildResults_storesResults() {
        try {
            Storage storage = new Storage("test1.sqlite");
            var res = new BuildResult("12345", "This build has succeeded");
            storage.storeBuildResult(res);

            var buildInfo = storage.getBuildResult(1);

            assertEquals(buildInfo.commitSHA, res.commitSHA);
            assertEquals(buildInfo.buildOutput, res.buildOutput);

            File testDB = new File("test1.sqlite");
            testDB.deleteOnExit();

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getBuildResult_failsWithIncorrectIndex() {
        try {
            Storage storage = new Storage("test2.sqlite");

            File testDB = new File("test2.sqlite");
            testDB.deleteOnExit();

            assertThrows(SQLException.class, () -> {
                storage.getBuildResult(12); // This index doesn't exist
            });
        } catch (IOException e) {
            fail();
        }
        
    }
}
