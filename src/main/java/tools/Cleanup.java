package tools;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public final class Cleanup {
    private Cleanup() {}

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
