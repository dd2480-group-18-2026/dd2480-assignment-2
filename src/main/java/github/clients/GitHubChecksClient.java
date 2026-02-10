package github.clients;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GitHubChecksClient {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private final GitHubAuth auth;

    /**
     * Constructor for GitHubChecksClient
     * @param appId
     * @param installationId
     * @param privateKeyPath
     * @throws IOException
     */
    public GitHubChecksClient(
        long appId, 
        long installationId, 
        String privateKeyPath, 
        String baseUrl
    ) throws IOException {
        this.auth = new GitHubAuth(appId, installationId, privateKeyPath);
        this.baseUrl = baseUrl;
    }

    GitHubChecksClient(
        GitHubAuth auth,
        String baseUrl
    ) {
        this.auth = auth;
        this.baseUrl = baseUrl;
    }


    /**
     * This method creates a check run for the given commit head SHA.
     * @param owner
     * @param repo
     * @param name
     * @param headSha
     * @return JSON String representation of the response body.
     * @throws Exception
     */
    public String createCheckRun(
            String owner,
            String repo,
            String name,
            String headSha
    ) throws Exception {

        Map<String, Object> payload = Map.of(
                "name", name,
                "head_sha", headSha,
                "status", "in_progress"
        );
        String url = baseUrl + owner + "/" + repo + "/check-runs";
        System.out.println("URL: " + url);
        String token = auth.getInstallationToken();
        return post(url, payload, token);
    }
    
    /**
     * This method updates the check run with the given check run ID.
     * @param owner
     * @param repo
     * @param checkStatus
     * @param conclusion
     * @param checkRunId
     * @return JSON String representation of the response body.
     * @throws Exception
     */
    public String updateCheckRun(
            String owner,
            String repo,
            CheckStatus status,
            CheckConclusion conclusion,
            BigInteger checkRunId
    ) throws Exception {

        Map<String, Object> payload = Map.of(
                "status", status.toString(),
                "conclusion", conclusion.toString()
        );
        String url = baseUrl + owner + "/" + repo + "/check-runs/" + checkRunId;
        String token = auth.getInstallationToken();
        return patch(url, payload, token);
    }

    private String post(String url, Map<String, Object> payload, String token) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(
                mapper.writeValueAsBytes(payload),
                MediaType.parse("application/json")
            ))
            .addHeader("Authorization", "token " + token)
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();
        return sendRequest(request);
    }

    private String patch(String url, Map<String, Object> payload, String token) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .patch(RequestBody.create(
                mapper.writeValueAsBytes(payload),
                MediaType.parse("application/json")
            ))
            .addHeader("Authorization", "token " + token)
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();

        return sendRequest(request);
    }

    private String sendRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.body().string());
            }
            return response.body().string();
        }
    }
}

