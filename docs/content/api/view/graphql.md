+++
title = "graphql"
description = "api documentation"
weight = 1
+++

## Contents

- [Queries](#queries)
    - [Coinbase tick messages](#coinbase-tick-messages)
    - [Product subscriptions](#product-subscriptions)
    
---

## Queries
- `messages` query.
- `subscriptions` query.

### Coinbase tick messages
*Aka* product fluctuations that are stored in `RethinkDb`

#### request
`POST /` along with body:
```graphql
{ 
    messages { 
        price 
        time 
        productId 
        bestBid 
    }
}
```
fetches all messages, only listing the fields supplied in the query block.

`POST /` along with body:
```graphql
{
    messages(mostRecent: 3) {
        price
        time
        productId
    }
}
```
fetches a collection of ***most recent*** messages (given the number after `mostRecent`),
listing only fields supplied in the query block.

#### response
An example response is displayed below.

The response is similar to those requests with and without the
parameter `mostRecent`. 
```graphql
{
    "data": {
        "messages": [
            {
                "price": "31772.15",
                "time": "2021-02-07T18:45:52.316192Z",
                "productId": "BTC-EUR"
            },
            {
                "price": "31760.65",
                "time": "2021-02-07T18:45:51.830952Z",
                "productId": "BTC-EUR"
            },
            {
                "price": "31755.84",
                "time": "2021-02-07T18:45:50.261338Z",
                "productId": "BTC-EUR"
            }
        ]
    }
}
```


### Product subscriptions
The `productIds` we are currently subscribed in `coinbase`, and thus
receiving the tick messages about fluctuations.

#### request
`POST /` along with body:
```graphql
{
    subscriptions
}
```

#### response
```graphql
{
    "data": {
        "subscriptions": [
            "BTC-EUR"
        ]
    }
}
```
