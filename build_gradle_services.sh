#!/bin/bash
# This script is only used by github actions to build the gradle projects

BASE_DIR=$(pwd)


# arg$1: the folder where the repo exists
# arg$2: the docker compose stack we will use to spin services in this repo
build_if_exists() {
  local folder=$1
  local docker_compose_file=$2

  if [[ -d $folder ]]; then
    echo "$folder gradle module is present"
    cd $folder
    echo "Building $folder..."
    ./gradlew build
    cd "$BASE_DIR"
  fi
}


## main script
build_if_exists "coinbase-adapter"
build_if_exists "coinbase-subscriber"
build_if_exists "graphql"
