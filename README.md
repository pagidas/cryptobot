# Cryptobot <!-- omit in toc -->

## Contents <!-- omit in toc -->

- [Languages:](#languages)
- [Main technologies used:](#main-technologies-used)
- [Overview:](#overview)
- [Running:](#running)

## Languages:
- [Kotlin](https://kotlinlang.org/)
- [Python](https://www.python.org/)

## Main technologies used:
- [Http4k](https://www.http4k.org/) - a collection of lightweight libraries written natively in kotlin to handle http.
- [Micronaut](https://micronaut.io/) - a JVM-based framework.
- [RethinkDB](https://rethinkdb.com/) - RethinkDB pushes JSON to your apps in realtime.
- [Requests](https://requests.readthedocs.io/en/master/) - a simple HTTP library for Python.

## Overview:
This project, still at its early development process, sends a request to an adapter service
which talks to coinbase open api websocket, retreiving and storing realtime messages regarding
value that a crypto currency has over to a real currency. The target is to analyze those values
and play safe bets little by little.

## Running:

To get you up-and-running and test how the project behaves at the moment, you need the following dependencies:
 - **Docker**
 ```shell
 For mac os users: https://hub.docker.com/editions/community/docker-ce-desktop-mac/
 You can use brew, otherwise
 
 For linux users of various of distros: https://docs.docker.com/engine/install/#server
 ```
 
 - **docker-compose**
 ```shell
 For mac os users:
 either use brew --> brew install docker-compose
 or pip --> pip install docker-compose
 
 For linux users:
 # check the current release and update accordingly
 https://github.com/docker/compose/releases
 
 # get package
 sudo curl -L https://github.com/docker/compose/releases/download/<version-of-docker-compose>/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
 
 # set permissions
 sudo chmod +x /usr/local/bin/docker-compose
 
 # verify version
 docker-compose --version
 ```
 
 Once you get `docker` and `docker-compose` (and having `make` and `git` too) simply clone the project
 ```shell
 git clone https://github.com/pagidas/cryptobot.git
 ```
 and at the root of the project for the first time run
 ```shell
 docker-compose up -d --build
 ```

 in subsequent runs you can also use
 ```shell
 docker-compose up -d
 ```

 there is also a command to force-build the images from scratch
 ```shell
 docker-compose build
 ```
 
 connect to `localhost:8080` and it will prompt you to a rethinkdb gui where you can
 watch the messages stored when retreived from coinbase open api websocket.


A CHANGE!!!