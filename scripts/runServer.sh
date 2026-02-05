set -e

PORT="${PORT:-8098}"
echo "Starting CI server on port $PORT"
mvn exec:java