#!/bin/bash
# This script is only used by github actions to build the gradle fatjars

BASE_DIR=$(pwd)

# arg$1: the folder where the repo exists
build_if_exists() {
  local folder=$1

  if [[ -d "../$folder" ]]; then
    echo "$folder gradle module is present"
    cd ../"$folder"
    echo "Building $folder..."
    ./gradlew build shadowJar
    cd "$BASE_DIR"
  fi
}


## main script
build_if_exists "coinbase-adapter"
build_if_exists "coinbase-subscriber"
build_if_exists "graphql"
