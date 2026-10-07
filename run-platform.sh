#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
rm -rf snake-platform/my-snake/*
cp src/* snake-platform/my-snake
./snake-platform/start.sh