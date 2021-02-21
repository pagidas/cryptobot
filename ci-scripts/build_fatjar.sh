#!/bin/bash
# This script is only used by github actions to build the gradle fatjars

BASE_DIR=$(pwd)

# arg$1: the folder where the repo exists
folder=$1

if [[ -d "../$folder" ]]; then
  echo "$folder gradle module is present"

  if [ "$folder" == 'coinbase-integration' ]; then
    # First build and publish to local maven http4k-starter.
    echo "Building and publish to maven local http4k-starter..."
    cd ../http4k-starter
    ./gradlew clean build publishToMavenLocal
    cd "$BASE_DIR"

    # Build the target module dependent on http4k-starter.
    cd ../"$folder"
    echo "Building $folder..."
    ./gradlew clean build
    cd "$BASE_DIR"
    return 1
  fi

  cd ../"$folder"
  echo "Building $folder..."
  ./gradlew clean build
  cd "$BASE_DIR"
fi
