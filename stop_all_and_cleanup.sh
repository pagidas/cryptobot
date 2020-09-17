#!/bin/bash

echo "Stopping all services..."
docker-compose down
echo "Done! You might need to cleanup intermediate images built.
                                      They will be under '<none>' in: $ docker images"
echo "Cleaning..."
docker rmi $(docker images | grep "^<none>" | awk "{print $3}")
echo "Done!"