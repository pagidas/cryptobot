#!/bin/bash
# This script is only used by github actions
# to build the docker images and publish them
# in GitHub container registry.

BASE_DIR=$(pwd)
BASE_CONTAINER_REPO=ghcr.io/pagidas

# arg$1: the folder where the repo exists
# arg$2: the github container registry personal access token
folder=$1
pat=$2

if [[ -d "../$folder" ]]; then
  echo "$folder module is present"
  echo "$2" | docker login ghcr.io --username pagidas --password-stdin
  echo "Building $folder docker image..."
  docker build ../"$folder" --tag "$folder":latest --file "$BASE_DIR"/dockerfiles/"$folder".Dockerfile
  docker tag "$folder":latest $BASE_CONTAINER_REPO/"$folder":latest
  echo "Pushing $folder docker image to ghcr.io..."
  docker push $BASE_CONTAINER_REPO/"$folder":latest
  cd "$BASE_DIR"
fi

