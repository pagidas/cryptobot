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

if [[ -d "../$folder" ]];
then
  echo "$folder module is present"
  echo "$2" | docker login ghcr.io --username pagidas --password-stdin

  if [ "$folder" == "coinbase-integration" ]; then
      echo "Building $folder docker images..."

      echo "Building coinbase-subscriber..."
      cd ../coinbase-integration/subscriber
      docker build . --tag $BASE_CONTAINER_REPO/coinbase-subscriber:latest
      echo "Pushing coinbase-subscriber docker image to ghcr.io..."
      docker push $BASE_CONTAINER_REPO/coinbase-subscriber:latest
      cd "$BASE_DIR"

      # This is when the adapter is implemented within the multiproject.
#      echo "Building coinbase-adapter..."
#      cd ../coinbase-integration/adapter
#      docker build . --tag $BASE_CONTAINER_REPO/coinbase-adapter:latest
#      echo "Pushing coinbase-adapter docker image to ghcr.io..."
#      docker push $BASE_CONTAINER_REPO/coinbase-adapter:latest
#      cd "$BASE_DIR"
  exit 0
  fi

  echo "Building $folder docker image..."
  cd ../"$folder"
  docker build . --tag $BASE_CONTAINER_REPO/"$folder":latest
  echo "Pushing $folder docker image to ghcr.io..."
  docker push $BASE_CONTAINER_REPO/"$folder":latest
  cd "$BASE_DIR"
fi

