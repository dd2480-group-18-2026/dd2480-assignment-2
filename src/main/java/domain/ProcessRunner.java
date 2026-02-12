package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class ProcessRunner implements IProcessRunner {

    @Override
    public ProcessResult runProcess(String[] command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        System.out.println("Running command: " + String.join(" ", command));

        Process process = builder.start();

        StringBuilder output = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }


        int exit = process.waitFor();

        return new ProcessResult(output.toString(), exit == 0);
    }
}
