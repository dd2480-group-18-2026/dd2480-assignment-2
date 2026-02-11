package github.clients;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

public class GitHubAuth {

    private static final OkHttpClient client = new OkHttpClient();
    private long appId;
    private long installationId;
    private PrivateKey privateKey;
    private String installationToken;
    private Instant tokenExpiresAt;

    protected GitHubAuth(long appId, long installationId, String privateKeyPath) throws IOException {
        this.appId = appId;
        this.installationId = installationId;
        this.privateKey = loadPrivateKey(privateKeyPath);
    }

    protected String getInstallationToken() throws Exception {

        if (installationToken != null && Instant.now().isBefore(tokenExpiresAt.minusSeconds(60))) {
            return installationToken;
        }

        requestInstallationToken();
        return installationToken;
    }

    private String requestInstallationToken() throws IOException, JsonProcessingException, JsonMappingException {
        String jwt =  getJwt(appId, privateKey);
        Request request = new Request.Builder()
                .url("https://api.github.com/app/installations/" + installationId + "/access_tokens")
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + jwt)
                .addHeader("Accept", "application/vnd.github+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Auth failed: " + response.body().string());
            }
            JsonNode body = new ObjectMapper().readTree(response.body().string());
            installationToken = body.get("token").asText();
            tokenExpiresAt = Instant.parse(body.get("expires_at").asText());
            return installationToken;
        }
    }

    private static PrivateKey loadPrivateKey(String privateKeyPath) throws IOException {
        PEMParser pemParser = new PEMParser(new FileReader(privateKeyPath));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        Object object = pemParser.readObject();
        KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
        PrivateKey privateKey = kp.getPrivate();
        pemParser.close();
        return privateKey;
    }

    private static String getJwt(long appId, PrivateKey privateKey) {
        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) privateKey);

        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(String.valueOf(appId))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(540))) // max 10 min
                .sign(algorithm);
    }
}
