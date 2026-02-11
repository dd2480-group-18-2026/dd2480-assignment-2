package tools;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

/**
 * Utility methods for cleaning up files and directories.
 */
public final class Cleanup {
    private Cleanup() {}

    /**
     * Deletes a file or directory recursively.
     *
     * <p>If the path does not exist or is {@code null}, this method does nothing.
     *
     * @param root the file or directory to delete
     * @throws IOException if an I/O error occurs during deletion
     */
    public static void deleteRecursively(Path root) throws IOException {
        if (root == null || Files.notExists(root)) return;

        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try { Files.deleteIfExists(p); }
                    catch (IOException e) { throw new RuntimeException(e); }
                });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException io) throw io;
            throw e;
        }
    }
}
