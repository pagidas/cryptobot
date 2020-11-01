#!/bin/bash

if [ "$1" == 'build' ]
then
  echo "Building images and running all containers..."
  docker-compose up -d --build
elif [ "$1" == 'fetch-new' ]
then
  echo "Removing old containers..."
  docker rmi $(docker images | grep ghcr.io/pagidas)
  echo "Downloading new containers..."
  docker-compose up -d --build
else
  echo "Spinning up containers..."
  docker-compose up -d
fi
echo "Done! All services running in the background
                          to see the containers run: $ docker ps -a"