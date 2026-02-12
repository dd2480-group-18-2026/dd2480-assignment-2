# DD2480 Assignment 2: Continuous Integration

HEEEEEY!

This project implements a small-scale Continuous Integration server written in Java.

The system integrates with GitHub webhooks to compile and run tests on a project, and connects back to GitHub to display the result of the process in the form of a check.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Requirement

- Java 17 
- Apache Maven

## Configuration

The server currently only supports Linux.

The server uses a .env file to load parameters and secrets. It is expected to be placed in the folder the project is started from. It must follow this format
```bash
PORT=""
APP_ID=""
INSTALLATION_ID=""
GITHUB_CHECKS_BASE_URL="https://api.github.com/repos/"
PRIVATE_KEY_PATH=""
```

These environment variables must be set locally for the program to run (but not for building/testing).

The four last entries are configuration for the GitHub checks API. In order to use it, you need to create a GitHub App in your repository.

## Building and running the server

Maven doesn't output an executable .jar for this project. As such, we rely on Maven itself to start the server. 

### Locally 

#### Running the tests
```bash
mvn test
```

#### Running the server
```bash
mvn exec:java
```

### Docker

This project contains a Dockerfile to build a container. This makes it easy to deploy the project anywhere, including servers that don't have Maven installed. To build the container, do 
```bash
docker build -t <IMAGE_NAME> .
```

Then, you can start the container using 
```bash
docker run -p <HOST_PORT>:<CONTAINER_PORT> -v <ENV_FILE>:/app/.env <IMAGE_NAME>:latest
```

## Accessing build information

Build information is stored on a persistent SQLite database that is created and initialized by the server on startup. Of course, it needs to be mounted through Docker to persist through deployments.

The URL for accessing all the build information is /builds. This returns a JSON containing the IDs of the builds, and the URLs for each of them.

Each URL contains detailed information about a specific build such as the build date, the commit SHA, the build output and the success state of the build.

## Documentation

The documentation is made using Javadoc, and is hosted on GitHub pages [here](https://dd2480-group-18-2026.github.io/dd2480-assignment-2/).

## For grading

There's a live instance of our server at https://dd2480-assignment-2.onrender.com/.

### Compilation

Compilation is done by cloning the repo to a temporary local folder, switching to the branch of the current commit, then invoking mvn test on this folder. 
"Unit" testing compilation directly is not possible, as compiling projects is outside the scope of what a unit test does. However, we have tested the public
compilation methods with mocking. The compilation working is also directly tested whenever the server runs.

### Test execution

Test execution is done the same way as compilation, running "maven test" on a cloned version of the repo at the given commit. The unit test are the same as for compilation, testing using mocking rather than directly running a set of tests. The test execution working is also directly tested whenever the server runs. 

### Notification

## Contributions

### August (GitHub: augustyvdal)
- Implemented cleanup after CI and unit test for cleanup
- Deployed the CI server to Render using Docker
- Wrote Javadoc
- Reviewed PR's

### Felix (GitHub: seahoers)
- Added webhook request handling.
- Added GitHub checks integration with GitHubChecksClient.
- Implemented CiCoordinator to tie the entire project together.
- Reviewed some PRs.
  
### Eliott (GitHub: Telmo26)
- Setup the intial project structure.
- Implemented the SQLite database utilities.
- Implemented the "/builds" endpoint to fetch build information from the server.
- Wrote this README.
- Reviewed some PRs.

### Tim (GitHub: Uniquepotatoes)
- Implemented pulling and building remote repositories and reporting the results
- Implemented helper class for running processes
- Contributed to README.
- Wrote some Javadoc for the above
- Reviewed some PRs
