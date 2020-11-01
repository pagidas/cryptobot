#!/bin/bash
# This script is only used by github actions to build the gradle projects

BASE_DIR=$(pwd)

# arg$1: the folder where the repo exists
build_if_exists() {
  local folder=$1

  if [[ -d "../$folder" ]]; then
    echo "$folder gradle module is present"

    if [ "$folder" == 'http4k-starter' ]; then
      # First build and publish to local maven http4k-starter
      cd ../http4k-starter
      ./gradlew clean build publishToMavenLocal
      cd "$BASE_DIR"
      return 1
    fi

    cd ../"$folder"
    echo "Building $folder..."
    # we only care if they compile, thus skip building the fatjar
    ./gradlew clean build -x shadowJar -x jar
    cd "$BASE_DIR"
  fi
}


## main script
build_if_exists "http4k-starter"
build_if_exists "coinbase-integration"
build_if_exists "graphql"
