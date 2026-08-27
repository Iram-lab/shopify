#!/bin/bash
# ─── Semantic Version Generator ──────────────────────────────────────
# Generates version from latest git tag + commit count.
# Format: {major}.{minor}.{patch}-{commits}.{short-sha}
# Output: VERSION, IMAGE_TAG, SHORT_SHA, BRANCH_TAG
# ─────────────────────────────────────────────────────────────────────

set -euo pipefail

# Get latest semver tag
LATEST_TAG=$(git tag -l "v*" --sort=-v:refname | head -1 2>/dev/null || echo "v0.0.0")
VERSION="${LATEST_TAG#v}"

# Count commits since last tag
COMMITS_SINCE=$(git rev-list "${LATEST_TAG}..HEAD" --count 2>/dev/null || echo "0")
SHORT_SHA=$(git rev-parse --short=7 HEAD)
BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Build full version string
if [ "$COMMITS_SINCE" -eq 0 ]; then
    FULL_VERSION="$VERSION"
else
    FULL_VERSION="${VERSION}-${COMMITS_SINCE}.${SHORT_SHA}"
fi

# Sanitize branch name for Docker tag (no slashes or special chars)
BRANCH_TAG=$(echo "$BRANCH" | sed 's/[^a-zA-Z0-9._-]/-/g' | cut -c1-128)

# Output — Jenkins reads these as KEY=VALUE
cat <<EOF
VERSION=${FULL_VERSION}
SEMVER=${VERSION}
SHORT_SHA=${SHORT_SHA}
BRANCH_TAG=${BRANCH_TAG}
COMMITS_SINCE=${COMMITS_SINCE}
IMAGE_TAG=${FULL_VERSION}
EOF
