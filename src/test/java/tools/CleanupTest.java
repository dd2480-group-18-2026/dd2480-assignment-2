package tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CleanupTest {

    @TempDir
    Path tempDir;

    @Test
    void deleteRecursively_whenDirectoryTreeExists_deletesAllFilesAndDirectories() throws Exception {
        Path root = Files.createDirectory(tempDir.resolve("workspace"));
        Path nested = Files.createDirectories(root.resolve("a/b/c"));
        Files.writeString(root.resolve("root.txt"), "hello");
        Files.writeString(nested.resolve("nested.txt"), "world");

        assertTrue(Files.exists(root));
        assertTrue(Files.exists(nested.resolve("nested.txt")));

        Cleanup.deleteRecursively(root);

        assertFalse(Files.exists(root), "Root directory should be deleted");
    }

    @Test
    void deleteRecursively_whenPathDoesNotExist_doesNothing() throws Exception {
        Path missing = tempDir.resolve("does-not-exist");
        assertFalse(Files.exists(missing));

        Cleanup.deleteRecursively(missing);
    }

    @Test
    void deleteRecursively_whenPathIsNull_doesNothing() throws Exception {
        Cleanup.deleteRecursively(null);
    }
}
