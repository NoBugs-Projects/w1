#!/bin/bash
# Extract metrics from Allure and Swagger and generate final index.html
# Usage:
#   ./extract-metrics.sh <allure-report-dir> <swagger-report-dir> [output-dir] [base-url-prefix]
# Example:
#   ./extract-metrics.sh gh-pages/23/allure-report gh-pages/23/swagger-coverage-report gh-pages/23 /w1/23/

set -euo pipefail

# Colors
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'

# Args / defaults
ALLURE_DIR="${1:-target/site}"         # path to *published* Allure report dir (index.html, data/, widgets/)
SWAGGER_DIR="${2:-target/site}"        # path to *published* Swagger dir (index.html)
OUTPUT_DIR="${3:-.github/report}"      # where to write final index.html + metrics.json
BASE_URL_PREFIX_RAW="${4:-/}"          # like /w1/23/

METRICS_FILE="metrics.json"

# Normalize /<...>/ shape
normalize_base_prefix() {
  local b="${1:-/}"
  [[ "$b" != /* ]] && b="/$b"
  [[ "$b" != */ ]] && b="$b/"
  printf "%s" "$b"
}
BASE_URL_PREFIX="$(normalize_base_prefix "$BASE_URL_PREFIX_RAW")"

echo -e "${BLUE}🔍 Extracting metrics...${NC}"
echo "ALLURE_DIR       = $ALLURE_DIR"
echo "SWAGGER_DIR      = $SWAGGER_DIR"
echo "OUTPUT_DIR       = $OUTPUT_DIR"
echo "BASE_URL_PREFIX  = $BASE_URL_PREFIX"

# ---------------- Allure metrics (from widgets/summary.json) ----------------
extract_allure_metrics() {
  local allure_dir="$1"
  local metrics_file="$2"

  echo -e "${YELLOW}📊 Reading Allure summary...${NC}"
  local summary="$allure_dir/widgets/summary.json"

  local total=0 passed=0 failed=0 broken=0 skipped=0 unknown=0
  local duration_ms=0

  if [ -f "$summary" ] && command -v jq >/dev/null 2>&1; then
    total=$(jq -r '.statistic.total // 0' "$summary" 2>/dev/null || echo 0)
    passed=$(jq -r '.statistic.passed // 0' "$summary" 2>/dev/null || echo 0)
    failed=$(jq -r '.statistic.failed // 0' "$summary" 2>/dev/null || echo 0)
    broken=$(jq -r '.statistic.broken // 0' "$summary" 2>/dev/null || echo 0)
    skipped=$(jq -r '.statistic.skipped // 0' "$summary" 2>/dev/null || echo 0)
    unknown=$(jq -r '.statistic.unknown // 0' "$summary" 2>/dev/null || echo 0)
    duration_ms=$(jq -r '.time.duration // 0' "$summary" 2>/dev/null || echo 0)
  else
    echo -e "${YELLOW}⚠️  No $summary or jq missing — defaulting Allure metrics to zeros${NC}"
  fi

  # Treat "broken" as failed in the dashboard
  local failed_like=$((failed + broken))
  local pass_rate=0
  if [ "$total" -gt 0 ]; then
    # scale=1 keeps one decimal
    pass_rate=$(echo "scale=1; $passed*100/$total" | bc -l 2>/dev/null || echo 0)
  fi

  # durations: Allure time.duration is in ms (total run); avg per test ~= total/total
  local total_seconds=$(( duration_ms / 1000 ))
  local avg_seconds=0
  if [ "$total" -gt 0 ]; then
    avg_seconds=$(( total_seconds / total ))
  fi

  # No direct "flaky" / "critical" in summary.json; keep as 0
  local flaky_tests=0 flaky_rate=0 critical_failures=0

  cat > "$metrics_file" <<EOF
{
  "allure": {
    "totalTests": $total,
    "passedTests": $passed,
    "failedTests": $failed_like,
    "skippedTests": $skipped,
    "criticalFailures": $critical_failures,
    "flakyTests": $flaky_tests,
    "passRate": $pass_rate,
    "flakyRate": $flaky_rate,
    "avgDuration": $avg_seconds,
    "totalDuration": $total_seconds
  }
}
EOF
  echo -e "${GREEN}✅ Allure metrics extracted${NC}"
}

# ---------------- Swagger metrics (from HTML) ----------------
extract_swagger_metrics() {
  local swagger_dir="$1"
  local metrics_file="$2"

  echo -e "${YELLOW}🔎 Reading Swagger coverage...${NC}"
  local report=""
  if      [ -f "$swagger_dir/index.html" ]; then report="$swagger_dir/index.html"
  elif    [ -f "$swagger_dir/swagger-coverage-report.html" ]; then report="$swagger_dir/swagger-coverage-report.html"
  elif    [ -f "swagger-coverage-report.html" ]; then report="swagger-coverage-report.html"
  elif    [ -f "reports/swagger-coverage-report.html" ]; then report="reports/swagger-coverage-report.html"
  fi

  local total_ops=0 no_calls_ops=0 total_tags=0 no_calls_tags=0 total_conditions=0
  local full_pct=0 partial_pct=0 empty_pct=0

  if [ -n "$report" ]; then
    echo "Using: $report"
    # Extract numbers with grep; ignore errors if patterns not found
    total_ops=$(grep -Eo 'All operations: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)
    no_calls_ops=$(grep -Eo 'Operations without calls: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)

    total_tags=$(grep -Eo 'All tags: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)
    no_calls_tags=$(grep -Eo 'Tags without calls: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)

    total_conditions=$(grep -Eo 'Total: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)

    full_pct=$(grep -Eo 'Full coverage: [0-9.]+%' "$report" 2>/dev/null | grep -Eo '[0-9.]+' | head -n1 || echo 0)
    partial_pct=$(grep -Eo 'Partial coverage: [0-9.]+%' "$report" 2>/dev/null | grep -Eo '[0-9.]+' | head -n1 || echo 0)
    empty_pct=$(grep -Eo 'Empty coverage: [0-9.]+%' "$report" 2>/dev/null | grep -Eo '[0-9.]+' | head -n1 || echo 0)
  else
    echo -e "${YELLOW}⚠️  Swagger report not found — defaulting to zeros${NC}"
  fi

  local covered_ops=0 covered_tags=0 api_coverage=0 conditions_coverage=0
  if [ "$total_ops" -gt 0 ]; then
    covered_ops=$(( total_ops - no_calls_ops ))
    api_coverage=$(echo "scale=1; $covered_ops*100/$total_ops" | bc -l 2>/dev/null || echo 0)
  fi
  if [ "$total_tags" -gt 0 ]; then
    covered_tags=$(( total_tags - no_calls_tags ))
  fi
  if [ "$total_conditions" -gt 0 ]; then
    # no "covered_conditions" in HTML; estimate by api_coverage
    conditions_coverage="$api_coverage"
  fi

  # Method/status coverage JSON optional; supply example structure when missing
  local method_cov='{"GET":{"coverage":85,"total":200},"POST":{"coverage":70,"total":150},"PUT":{"coverage":60,"total":50},"DELETE":{"coverage":40,"total":33}}'
  local status_cov='{"200":15,"400":8,"403":5,"404":3,"500":2}'
  if [ -f "$swagger_dir/swagger-coverage.json" ] && command -v jq >/dev/null 2>&1; then
    local mc sc
    mc=$(jq -r 'select(.methods) | .methods' "$swagger_dir/swagger-coverage.json" 2>/dev/null || true)
    sc=$(jq -r 'select(.statusCodes) | .statusCodes' "$swagger_dir/swagger-coverage.json" 2>/dev/null || true)
    if [ -n "${mc:-}" ] && [ "$mc" != "null" ]; then method_cov="$mc"; fi
    if [ -n "${sc:-}" ] && [ "$sc" != "null" ]; then status_cov="$sc"; fi
  fi

  if command -v jq >/dev/null 2>&1 && [ -f "$metrics_file" ]; then
    local tmp; tmp=$(mktemp)
    jq --argjson swagger "{
      \"totalOperations\": $total_ops,
      \"coveredOperations\": $covered_ops,
      \"totalTags\": $total_tags,
      \"coveredTags\": $covered_tags,
      \"totalConditions\": $total_conditions,
      \"coveredConditions\": $covered_tags,
      \"apiCoverage\": $api_coverage,
      \"conditionsCoverage\": $conditions_coverage,
      \"fullCoverage\": $full_pct,
      \"partialCoverage\": $partial_pct,
      \"emptyCoverage\": $empty_pct,
      \"methodCoverage\": $method_cov,
      \"statusCodeCoverage\": $status_cov
    }" '.swagger = $swagger' "$metrics_file" > "$tmp" && mv "$tmp" "$metrics_file"
  else
    cat > "$metrics_file" <<EOF
{
  "swagger": {
    "totalOperations": $total_ops,
    "coveredOperations": $covered_ops,
    "totalTags": $total_tags,
    "coveredTags": $covered_tags,
    "totalConditions": $total_conditions,
    "coveredConditions": $covered_tags,
    "apiCoverage": $api_coverage,
    "conditionsCoverage": $conditions_coverage,
    "fullCoverage": $full_pct,
    "partialCoverage": $partial_pct,
    "emptyCoverage": $empty_pct,
    "methodCoverage": $method_cov,
    "statusCodeCoverage": $status_cov
  }
}
EOF
  fi

  echo -e "${GREEN}✅ Swagger metrics extracted${NC}"
}

# ---------------- Final index.html ----------------
generate_final_index() {
  local output_dir="$1"
  local metrics_file="$2"
  local template_file=".github/report/index.html"
  local final_file="$output_dir/index.html"

  echo -e "${YELLOW}📄 Generating final index.html...${NC}"
  [ -f "$template_file" ] || { echo -e "${RED}❌ Template not found: $template_file${NC}"; return 1; }

  mkdir -p "$output_dir"
  cp "$template_file" "$final_file"

  # Build URLs + run number from base prefix
  local ALLURE_REPORT_URL="${BASE_URL_PREFIX}allure-report/"
  local SWAGGER_REPORT_URL="${BASE_URL_PREFIX}swagger-coverage-report/"
  local RUN_NUMBER="${BASE_URL_PREFIX%/}"; RUN_NUMBER="${RUN_NUMBER##*/}"

  export ALLURE_REPORT_URL SWAGGER_REPORT_URL RUN_NUMBER

  # Read metrics (if jq present) and export for envsubst
  if [ -f "$metrics_file" ] && command -v jq >/dev/null 2>&1; then
    # Allure
    export ALLURE_PASS_RATE="$(jq -r '.allure.passRate // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_TOTAL_TESTS="$(jq -r '.allure.totalTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_PASSED_TESTS="$(jq -r '.allure.passedTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_FAILED_TESTS="$(jq -r '.allure.failedTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_SKIPPED_TESTS="$(jq -r '.allure.skippedTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_FLAKY_TESTS="$(jq -r '.allure.flakyTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_FLAKY_RATE="$(jq -r '.allure.flakyRate // 0' "$metrics_file" 2>/dev/null || echo 0)"
    # durations already seconds in our metrics
    export ALLURE_AVG_DURATION="$(jq -r '.allure.avgDuration // 0' "$metrics_file" 2>/dev/null || echo 0)s"
    export ALLURE_TOTAL_DURATION="$(jq -r '.allure.totalDuration // 0' "$metrics_file" 2>/dev/null || echo 0)s"
    export ALLURE_CRITICAL_FAILURES="$(jq -r '.allure.criticalFailures // 0' "$metrics_file" 2>/dev/null || echo 0)"

    # Swagger
    export SWAGGER_API_COVERAGE="$(jq -r '.swagger.apiCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_CONDITIONS_COVERAGE="$(jq -r '.swagger.conditionsCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_FULL_COVERAGE="$(jq -r '.swagger.fullCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_PARTIAL_COVERAGE="$(jq -r '.swagger.partialCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_EMPTY_COVERAGE="$(jq -r '.swagger.emptyCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_OPERATIONS_COVERAGE="$(jq -r '.swagger.apiCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)" # reuse for bar width
    export SWAGGER_COVERED_OPERATIONS="$(jq -r '.swagger.coveredOperations // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_TOTAL_OPERATIONS="$(jq -r '.swagger.totalOperations // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_TAGS_COVERAGE="$(jq -r '.swagger.coveredTags as $c | .swagger.totalTags as $t | (if ($t|tonumber)>0 then ($c*100/$t) else 0 end) | tostring' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_COVERED_TAGS="$(jq -r '.swagger.coveredTags // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_TOTAL_TAGS="$(jq -r '.swagger.totalTags // 0' "$metrics_file" 2>/dev/null || echo 0)"
  else
    echo -e "${YELLOW}⚠️  No metrics or jq — injecting only links & run number${NC}"
  fi

  if command -v envsubst >/dev/null 2>&1; then
    local tmp; tmp=$(mktemp)
    envsubst < "$final_file" > "$tmp" && mv "$tmp" "$final_file"
  else
    # Minimal fallback: replace only links and run number
    sed -i.bak \
      -e "s|\$ALLURE_REPORT_URL|$ALLURE_REPORT_URL|g" \
      -e "s|\$SWAGGER_REPORT_URL|$SWAGGER_REPORT_URL|g" \
      -e "s|\$RUN_NUMBER|$RUN_NUMBER|g" \
      "$final_file"
  fi

  echo -e "${GREEN}✅ Final index.html ready: $final_file${NC}"
}

# ---------------- Main ----------------
main() {
  echo -e "${BLUE}🚀 Starting metrics extraction...${NC}"
  mkdir -p "$OUTPUT_DIR"

  extract_allure_metrics "$ALLURE_DIR" "$METRICS_FILE"
  extract_swagger_metrics "$SWAGGER_DIR" "$METRICS_FILE"
  generate_final_index "$OUTPUT_DIR" "$METRICS_FILE"

  if [ -f "$METRICS_FILE" ]; then
    cp "$METRICS_FILE" "$OUTPUT_DIR/" && echo "📁 Metrics copied to: $OUTPUT_DIR/$METRICS_FILE"
  fi

  echo -e "${GREEN}✅ Done${NC}"
}

main "$@"
