#!/bin/bash

echo "Stopping all services..."
docker-compose down
echo "Done! You might need to cleanup intermediate images built.
                                      They will be under '<none>' in: $ docker images"