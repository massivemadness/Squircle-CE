#!/bin/bash

git stash -q --keep-index

echo "Running git pre-commit hook"

./gradlew ktlint

RESULT=$?

git stash pop -q

# return 1 exit code if running checks fails
[ $RESULT -ne 0 ] && exit 1
exit 0