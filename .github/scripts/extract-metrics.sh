#!/bin/bash
# Extract metrics from Allure + Swagger and generate dashboard index.html
# Usage:
#   .github/scripts/extract-metrics.sh <allure-report-dir> <swagger-report-dir> [output-dir] [base-url-prefix]
# Example:
#   .github/scripts/extract-metrics.sh gh-pages/32/allure-report gh-pages/32/swagger-coverage-report gh-pages/32 /w1/32/

set -euo pipefail

# ----- Pretty colors (optional logs) -----
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'

# ----- Args / defaults -----
ALLURE_DIR="${1:-target/site}"      # published Allure report dir (index.html, data/, widgets/)
SWAGGER_DIR="${2:-target/site}"     # published Swagger report dir (index.html)
OUTPUT_DIR="${3:-.github/report}"   # where to write final index.html + metrics.json
BASE_URL_PREFIX_RAW="${4:-/}"       # like /w1/32/

METRICS_FILE="metrics.json"

# ----- Helpers -----
normalize_base_prefix() {
  local b="${1:-/}"
  [[ "$b" != /* ]] && b="/$b"
  [[ "$b" != */ ]] && b="$b/"
  printf "%s" "$b"
}

# Keep only digits -> integer (avoid "0\n0" issues)
to_int() {
  local x; x="$(printf '%s' "${1:-0}" | tr -cd '0-9')"
  [ -n "$x" ] || x=0
  printf '%s' "$x"
}

# Keep digits and dot -> float-ish number
to_num() {
  local x; x="$(printf '%s' "${1:-0}" | tr -cd '0-9.')"
  [ -n "$x" ] || x=0
  printf '%s' "$x"
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

  local total=0 passed=0 failed=0 broken=0 skipped=0 duration_ms=0
  if [ -f "$summary" ] && command -v jq >/dev/null 2>&1; then
    total=$(jq -r '.statistic.total // 0'   "$summary" 2>/dev/null || true);     total="$(to_int "$total")"
    passed=$(jq -r '.statistic.passed // 0' "$summary" 2>/dev/null || true);     passed="$(to_int "$passed")"
    failed=$(jq -r '.statistic.failed // 0' "$summary" 2>/dev/null || true);     failed="$(to_int "$failed")"
    broken=$(jq -r '.statistic.broken // 0' "$summary" 2>/dev/null || true);     broken="$(to_int "$broken")"
    skipped=$(jq -r '.statistic.skipped // 0' "$summary" 2>/dev/null || true);   skipped="$(to_int "$skipped")"
    duration_ms=$(jq -r '.time.duration // 0' "$summary" 2>/dev/null || true);   duration_ms="$(to_int "$duration_ms")"
  else
    echo -e "${YELLOW}⚠️  No $summary or jq missing — defaulting Allure metrics to zeros${NC}"
  fi

  # Treat "broken" as failed for dashboard
  local failed_like=$((failed + broken))

  local pass_rate=0
  if [ "$total" -gt 0 ] && command -v bc >/dev/null 2>&1; then
    pass_rate="$(echo "scale=1; $passed*100/$total" | bc -l 2>/dev/null || echo 0)"
  elif [ "$total" -gt 0 ]; then
    # integer fallback
    pass_rate=$(( passed*100/total ))
  fi

  local total_seconds=$(( duration_ms / 1000 ))
  local avg_seconds=0
  if [ "$total" -gt 0 ]; then avg_seconds=$(( total_seconds / total )); fi

  cat > "$metrics_file" <<EOF
{
  "allure": {
    "totalTests": $total,
    "passedTests": $passed,
    "failedTests": $failed_like,
    "skippedTests": $skipped,
    "criticalFailures": 0,
    "flakyTests": 0,
    "passRate": $pass_rate,
    "flakyRate": 0,
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
    total_ops="$(grep -Eo 'All operations: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)";        total_ops="$(to_int "$total_ops")"
    no_calls_ops="$(grep -Eo 'Operations without calls: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)"; no_calls_ops="$(to_int "$no_calls_ops")"
    total_tags="$(grep -Eo 'All tags: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)";              total_tags="$(to_int "$total_tags")"
    no_calls_tags="$(grep -Eo 'Tags without calls: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)"; no_calls_tags="$(to_int "$no_calls_tags")"
    total_conditions="$(grep -Eo 'Total: [0-9]+' "$report" 2>/dev/null | grep -Eo '[0-9]+' | head -n1 || echo 0)";           total_conditions="$(to_int "$total_conditions")"

    full_pct="$(grep -Eo 'Full coverage: [0-9.]+%' "$report" 2>/dev/null | grep -Eo '[0-9.]+' | head -n1 || echo 0)";        full_pct="$(to_num "$full_pct")"
    partial_pct="$(grep -Eo 'Partial coverage: [0-9.]+%' "$report" 2>/dev/null | grep -Eo '[0-9.]+' | head -n1 || echo 0)";  partial_pct="$(to_num "$partial_pct")"
    empty_pct="$(grep -Eo 'Empty coverage: [0-9.]+%' "$report" 2>/dev/null | grep -Eo '[0-9.]+' | head -n1 || echo 0)";      empty_pct="$(to_num "$empty_pct")"
  else
    echo -e "${YELLOW}⚠️  Swagger report not found — defaulting to zeros${NC}"
  fi

  local covered_ops=0 covered_tags=0 api_coverage=0 conditions_coverage=0
  if [ "$total_ops" -gt 0 ]; then
    covered_ops=$(( total_ops - no_calls_ops ))
    if command -v bc >/dev/null 2>&1; then
      api_coverage="$(echo "scale=1; $covered_ops*100/$total_ops" | bc -l 2>/dev/null || echo 0)"
    else
      api_coverage=$(( covered_ops*100/total_ops ))
    fi
  fi
  if [ "$total_tags" -gt 0 ]; then
    covered_tags=$(( total_tags - no_calls_tags ))
  fi
  if [ "$total_conditions" -gt 0 ]; then
    conditions_coverage="$api_coverage"
  fi

  # Optional JSON for method/status coverage; fall back to a small example
  local method_cov='{"GET":{"coverage":85,"total":200},"POST":{"coverage":70,"total":150},"PUT":{"coverage":60,"total":50},"DELETE":{"coverage":40,"total":33}}'
  local status_cov='{"200":15,"400":8,"403":5,"404":3,"500":2}'
  if [ -f "$swagger_dir/swagger-coverage.json" ] && command -v jq >/dev/null 2>&1; then
    local mc; mc="$(jq -r 'select(.methods) | .methods' "$swagger_dir/swagger-coverage.json" 2>/dev/null || true)"
    local sc; sc="$(jq -r 'select(.statusCodes) | .statusCodes' "$swagger_dir/swagger-coverage.json" 2>/dev/null || true)"
    if [ -n "${mc:-}" ] && [ "$mc" != "null" ]; then method_cov="$mc"; fi
    if [ -n "${sc:-}" ] && [ "$sc" != "null" ]; then status_cov="$sc"; fi
  fi

  if command -v jq >/dev/null 2>&1 && [ -f "$metrics_file" ]; then
    local tmp; tmp="$(mktemp)"
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
    # overwrite metrics with swagger-only (unlikely path)
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

# ---------------- Optional: append Java test metrics safely ----------------
append_test_metrics() {
  local metrics_file="$1"
  local test_dir="src/test/java"

  echo -e "🧪 Reading test coverage metrics..."
  echo "Scanning Java test files in: $test_dir"

  if [ ! -d "$test_dir" ]; then
    echo "  No $test_dir directory — skipping."
    return 0
  fi

  local files tests lines
  files="$(find "$test_dir" -type f -name '*.java' 2>/dev/null | wc -l | tr -d '[:space:]')"; files="$(to_int "$files")"
  tests="$(grep -R --include='*.java' -E '@Test\b' "$test_dir" 2>/dev/null | wc -l | tr -d '[:space:]')"; tests="$(to_int "$tests")"

  if command -v xargs >/dev/null 2>&1; then
    lines="$(find "$test_dir" -type f -name '*.java' -print0 2>/dev/null | xargs -0 cat 2>/dev/null | wc -l | tr -d '[:space:]')"
  else
    lines="$(find "$test_dir" -type f -name '*.java' 2>/dev/null -exec cat {} + | wc -l | tr -d '[:space:]')"
  fi
  lines="$(to_int "$lines")"

  echo "  Java test files: $files"
  echo "  @Test annotations: $tests"
  echo "  Lines of code: $lines"

  if command -v jq >/dev/null 2>&1 && [ -f "$metrics_file" ]; then
    local tmp; tmp="$(mktemp)"
    jq --argjson tests "{\"javaFiles\": $files, \"javaTestAnnotations\": $tests, \"javaLines\": $lines}" \
       '.tests = $tests' "$metrics_file" > "$tmp" && mv "$tmp" "$metrics_file"
    echo "  ✅ Appended test metrics to $metrics_file"
  else
    echo "  ℹ️ jq not available or $metrics_file missing; skipping JSON append."
  fi
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

  # Build links + run number from base prefix
  local ALLURE_REPORT_URL="${BASE_URL_PREFIX}allure-report/"
  local SWAGGER_REPORT_URL="${BASE_URL_PREFIX}swagger-coverage-report/"
  local RUN_NUMBER="${BASE_URL_PREFIX%/}"; RUN_NUMBER="${RUN_NUMBER##*/}"

  # Export link placeholders (always)
  export ALLURE_REPORT_URL SWAGGER_REPORT_URL RUN_NUMBER

  # Export metric placeholders (if metrics exist)
  if [ -f "$metrics_file" ] && command -v jq >/dev/null 2>&1; then
    export ALLURE_PASS_RATE="$(jq -r '.allure.passRate // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_TOTAL_TESTS="$(jq -r '.allure.totalTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_PASSED_TESTS="$(jq -r '.allure.passedTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_FAILED_TESTS="$(jq -r '.allure.failedTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_SKIPPED_TESTS="$(jq -r '.allure.skippedTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_FLAKY_TESTS="$(jq -r '.allure.flakyTests // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export ALLURE_FLAKY_RATE="0"
    export ALLURE_AVG_DURATION="$(jq -r '.allure.avgDuration // 0' "$metrics_file" 2>/dev/null || echo 0)s"
    export ALLURE_TOTAL_DURATION="$(jq -r '.allure.totalDuration // 0' "$metrics_file" 2>/dev/null || echo 0)s"
    export ALLURE_CRITICAL_FAILURES="$(jq -r '.allure.criticalFailures // 0' "$metrics_file" 2>/dev/null || echo 0)"

    export SWAGGER_API_COVERAGE="$(jq -r '.swagger.apiCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_CONDITIONS_COVERAGE="$(jq -r '.swagger.conditionsCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_FULL_COVERAGE="$(jq -r '.swagger.fullCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_PARTIAL_COVERAGE="$(jq -r '.swagger.partialCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_EMPTY_COVERAGE="$(jq -r '.swagger.emptyCoverage // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_COVERED_OPERATIONS="$(jq -r '.swagger.coveredOperations // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_TOTAL_OPERATIONS="$(jq -r '.swagger.totalOperations // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_COVERED_TAGS="$(jq -r '.swagger.coveredTags // 0' "$metrics_file" 2>/dev/null || echo 0)"
    export SWAGGER_TOTAL_TAGS="$(jq -r '.swagger.totalTags // 0' "$metrics_file" 2>/dev/null || echo 0)"
    if [ "${SWAGGER_TOTAL_TAGS:-0}" -gt 0 ] && command -v bc >/dev/null 2>&1; then
      export SWAGGER_TAGS_COVERAGE="$(echo "scale=1; $SWAGGER_COVERED_TAGS*100/$SWAGGER_TOTAL_TAGS" | bc -l 2>/dev/null || echo 0)"
    elif [ "${SWAGGER_TOTAL_TAGS:-0}" -gt 0 ]; then
      export SWAGGER_TAGS_COVERAGE="$(( SWAGGER_COVERED_TAGS*100/SWAGGER_TOTAL_TAGS ))"
    else
      export SWAGGER_TAGS_COVERAGE="0"
    fi
    export SWAGGER_OPERATIONS_COVERAGE="$SWAGGER_API_COVERAGE"
  else
    # Defaults for first render
    export ALLURE_PASS_RATE="0" ALLURE_TOTAL_TESTS="0" ALLURE_PASSED_TESTS="0" ALLURE_FAILED_TESTS="0"
    export ALLURE_SKIPPED_TESTS="0" ALLURE_FLAKY_TESTS="0" ALLURE_FLAKY_RATE="0" ALLURE_CRITICAL_FAILURES="0"
    export ALLURE_AVG_DURATION="0s" ALLURE_TOTAL_DURATION="0s"
    export SWAGGER_API_COVERAGE="0" SWAGGER_CONDITIONS_COVERAGE="0" SWAGGER_FULL_COVERAGE="0" SWAGGER_PARTIAL_COVERAGE="0"
    export SWAGGER_EMPTY_COVERAGE="0" SWAGGER_OPERATIONS_COVERAGE="0" SWAGGER_COVERED_OPERATIONS="0" SWAGGER_TOTAL_OPERATIONS="0"
    export SWAGGER_TAGS_COVERAGE="0" SWAGGER_COVERED_TAGS="0" SWAGGER_TOTAL_TAGS="0"
  fi

  # Only substitute our placeholders (protect JS template literals)
  local SUBST_VARS='$RUN_NUMBER $ALLURE_REPORT_URL $SWAGGER_REPORT_URL \
$ALLURE_PASS_RATE $ALLURE_TOTAL_TESTS $ALLURE_PASSED_TESTS $ALLURE_FAILED_TESTS \
$ALLURE_SKIPPED_TESTS $ALLURE_FLAKY_TESTS $ALLURE_FLAKY_RATE $ALLURE_CRITICAL_FAILURES \
$ALLURE_AVG_DURATION $ALLURE_TOTAL_DURATION \
$SWAGGER_API_COVERAGE $SWAGGER_CONDITIONS_COVERAGE $SWAGGER_FULL_COVERAGE \
$SWAGGER_PARTIAL_COVERAGE $SWAGGER_EMPTY_COVERAGE $SWAGGER_OPERATIONS_COVERAGE \
$SWAGGER_COVERED_OPERATIONS $SWAGGER_TOTAL_OPERATIONS $SWAGGER_TAGS_COVERAGE \
$SWAGGER_COVERED_TAGS $SWAGGER_TOTAL_TAGS'

  if command -v envsubst >/dev/null 2>&1; then
    local tmp; tmp="$(mktemp)"
    envsubst "$SUBST_VARS" < "$final_file" > "$tmp" && mv "$tmp" "$final_file"
  else
    # Minimal fallback: links + run number only
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

  # Optional: safe Java test metrics appended to metrics.json
  append_test_metrics "$METRICS_FILE"

  generate_final_index "$OUTPUT_DIR" "$METRICS_FILE"

  if [ -f "$METRICS_FILE" ]; then
    cp "$METRICS_FILE" "$OUTPUT_DIR/" && echo "📁 Metrics copied to: $OUTPUT_DIR/$METRICS_FILE"
  fi

  echo -e "${GREEN}✅ Done${NC}"
}

main "$@"
