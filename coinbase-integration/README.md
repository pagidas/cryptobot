# coinbase-integration

## Services integrating with coinbase.

This project contains two services with their own responsibilities.

## To Run Locally

Both of those services require ***RethinkDb*** in order to run locally.

## Contents
1. Subscriber
2. Adapter

### Subscriber
Responsible for subscribing to ***Coinbase*** cryptocurrency market
and fetch product fluctuations in a stream manner through
a `websocket` connection. Stores these fluctuations in ***RethinkDb***
in `messages`

### Adapter
Responsible for forwarding requests to ***Coinbase*** with correct
signing. Currently, it only supports fetching information about
our profile subscribed to them.

endpoint: `/profiles`