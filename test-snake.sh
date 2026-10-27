#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
rm -rf snake-platform/my-snake/*
cp src/* snake-platform/my-snake
pushd ./snake-platform
javac -d .build/my-snake my-snake/*.java
python -m snake.cli evaluate "cmd:java -cp .build/my-snake Snake" --seeds $1..$2
popd