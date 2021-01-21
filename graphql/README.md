# graphql

## View service

This project is a `graphql` `view` service which is responsible to fetch
price fluctuations in the coinbase product subscription.
These price fluctuations are contained in `messages` table in
***RehinkDb*** and contain various of fields per record.

### Types of queries

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
fetches all messages, only listing the fields supplied in the block. 

`POST /` along with body:
```graphql
{
  messages(mostRecent: 5) {
    price
    time
    productId
  }
}
```
fetches a collection of ***most recent*** messages (given the number after `mostRecent`),
listing only fields supplied in the block.

`POST /` along with body:
```graphql
{
  subscriptions
}
```
fetches the ***coinbase*** product ids we are subsribed to.

## To Run Locally

Requires ***RethinkDb*** in order to run locally.
