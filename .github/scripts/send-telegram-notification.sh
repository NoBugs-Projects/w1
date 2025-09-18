#!/bin/bash

# Get test results from Allure
if [ -d "target/allure-results" ]; then
    TOTAL_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -v "^$" | wc -l)
    PASSED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "passed" || echo "0")
    FAILED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "failed" || echo "0")
else
    TOTAL_TESTS=0
    PASSED_TESTS=0
    FAILED_TESTS=0
fi

# Ensure variables are numbers
TOTAL_TESTS=$(echo "${TOTAL_TESTS:-0}" | tr -d '\n' | xargs)
PASSED_TESTS=$(echo "${PASSED_TESTS:-0}" | tr -d '\n' | xargs)
FAILED_TESTS=$(echo "${FAILED_TESTS:-0}" | tr -d '\n' | xargs)

# Get API coverage
if [ -f "swagger-coverage-results.json" ]; then
    API_COVERAGE=$(jq -r '.conditionCounter.covered // 0' swagger-coverage-results.json)
    API_TOTAL=$(jq -r '.conditionCounter.all // 0' swagger-coverage-results.json)
    if [ "$API_TOTAL" -gt 0 ]; then
        API_PERCENT=$((API_COVERAGE * 100 / API_TOTAL))
    else
        API_PERCENT=0
    fi
else
    API_PERCENT=0
fi

# Calculate success rate
if [ "$TOTAL_TESTS" -gt 0 ]; then
    SUCCESS_RATE=$((PASSED_TESTS * 100 / TOTAL_TESTS))
else
    SUCCESS_RATE=0
fi

# Determine status
if [ "$JOB_STATUS" == "success" ]; then
    STATUS_COLOR="🟢"
    STATUS_TEXT="SUCCESS"
else
    STATUS_COLOR="🔴"
    STATUS_TEXT="FAILED"
fi

# Create descriptive text for zero values
if [ "$TOTAL_TESTS" -eq 0 ]; then
    TOTAL_TESTS_TEXT="No tests found"
else
    TOTAL_TESTS_TEXT="$TOTAL_TESTS"
fi

if [ "$PASSED_TESTS" -eq 0 ]; then
    PASSED_TESTS_TEXT="No tests passed"
else
    PASSED_TESTS_TEXT="$PASSED_TESTS"
fi

if [ "$FAILED_TESTS" -eq 0 ]; then
    FAILED_TESTS_TEXT="No failures"
else
    FAILED_TESTS_TEXT="$FAILED_TESTS"
fi

# Build message
MESSAGE="🚀 *CI/CD Pipeline Completed!*

📊 *Test Statistics:*
• Total tests: $TOTAL_TESTS_TEXT
• Passed: $PASSED_TESTS_TEXT ✅
• Failed: $FAILED_TESTS_TEXT ❌
• Success rate: ${SUCCESS_RATE}%
• API coverage: ${API_PERCENT}%

🔗 *Links:*
• Repository: https://github.com/$GITHUB_REPOSITORY
• Commit: https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA
• Report: https://$GITHUB_REPOSITORY_OWNER.github.io/$GITHUB_EVENT_REPOSITORY_NAME/

📦 *Docker Hub:*
• Repository: https://hub.docker.com/r/$DOCKER_USERNAME/nbank/tags
• Tag: $GITHUB_SHA

$STATUS_COLOR *Status:* $STATUS_TEXT"

# Send to Telegram
curl -s -X POST "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage" \
  -H "Content-Type: application/json" \
  -d "{
    \"chat_id\": \"$TELEGRAM_CHAT_ID\",
    \"text\": \"$MESSAGE\",
    \"parse_mode\": \"Markdown\"
  }"
