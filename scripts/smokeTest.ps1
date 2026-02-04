try {
    Invoke-WebRequest -UseBasicParsing "http://localhost:8098/" | Out-Null
    Write-Host "OK: server responds on /"
} catch {
    Write-Host "FAIL: server not reachable on http://localhost:8098/"
    exit 1
}
