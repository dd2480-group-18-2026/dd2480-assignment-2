package servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import domain.BuildResult;
import domain.Storage;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Servlet providing access to stored build results.
 *
 * <p>Supports listing builds and retrieving individual build details.
 */
public class BuildServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Storage database;

    /**
     * Handles build listing and build detail requests.
     *
     * <p>If no path is provided, returns a list of builds. If a build ID is provided,
     * returns details for that build.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs while writing the response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String path = request.getPathInfo();

        if (path == null || path == "/") {
            returnBuildList(response);
        } else {
            int id = Integer.parseInt(path.substring(1));
            returnBuildDetails(id, response);
        }
    }

    private void returnBuildList(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);

        ArrayList<Integer> indexes = database.getBuildIndexes();

        ArrayList<BuildResource> builds = new ArrayList<>();
        for (Integer buildIdx : indexes) {
            builds.add(new BuildResource(buildIdx, "/builds/" + buildIdx));
        }

        objectMapper.writeValue(response.getWriter(), new Builds(builds));
    }

    private void returnBuildDetails(int id, HttpServletResponse response) throws IOException {
        try {
            BuildResult result = database.getBuildResult(id);
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), result);
            
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Creates a servlet backed by the given storage.
     *
     * @param database the storage used to retrieve build results
     */
    public BuildServlet(Storage database) {
        this.database = database;
    }

    static class Builds {
        public ArrayList<BuildResource> builds;

        Builds(ArrayList<BuildResource> builds) {
            this.builds = builds;
        }
    }

    static class BuildResource {
        public int id;
        public String url;

        BuildResource(int id, String url) {
            this.id = id;
            this.url = url;
        }
    }
}
