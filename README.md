Test server
# DD2480 Assignment 2: Continuous Integration

This project implements a small-scale Continuous Integration server written in Java.

The system integrates with GitHub webhooks to compile and run tests on a project, and connects back to GitHub to display the result of the process in the form of a check.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Requirement

- Java 17 
- Apache Maven

## Configuration

The server uses a .env file to load parameters and secrets. It is expected to be placed in the folder the project is started from. It must follow this format
```bash
PORT=""
APP_ID=""
INSTALLATION_ID=""
GITHUB_CHECKS_BASE_URL="https://api.github.com/repos/"
PRIVATE_KEY_PATH=""
```

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

## Documentation

The documentation is made using Javadoc, and is hosted on GitHub pages [here](https://dd2480-group-18-2026.github.io/dd2480-assignment-2/).

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
- Reviewed some PRs
