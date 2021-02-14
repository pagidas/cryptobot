---
title: Architecture Designs
weight: 2
---

*Contains images/discussions/thoughts about architecture designs of cryptobot.*

## Contents
- [v1](#first-draft-of-cryptobot)
- [v2](#second-draft-of-cryptobot)

---

## First draft of cryptobot
![image](images/Architecture_diagram_v1.png)

## Second draft of cryptobot

We added a reverse proxy and load balancer, `nginx` to map
requests to different microservices.
![image](images/Architecture_diagram_v2.png)

