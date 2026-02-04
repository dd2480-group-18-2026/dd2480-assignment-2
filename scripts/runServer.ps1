$port = $env:PORT
if (-not $port) { $port = "8098" }

Write-Host "Starting CI server on port $port"
mvn exec:java
