set -e

PORT="${PORT:-8098}"

if curl -fs "http://localhost:$PORT/" >/dev/null; then
  echo "OK: server responds on /"
else
  echo "FAIL: server not reachable on http://localhost:$PORT/"
  exit 1
fi