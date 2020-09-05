#!/bin/bash

if [ "$1" == 'build' ]; then
  echo "Building images and running all containers..."
  docker-compose up -d --build
else
  echo "Running all containers from previously built images..."
  docker-compose up -d
fi
echo "Done! All services running in the background
                          to see the containers run: $ docker ps -a"