# Marlow
In this repository I will explore Kafka Streams

The project provides the following topology:
![](https://user-images.githubusercontent.com/9152392/148232597-a5db9890-3f19-4a13-b51a-db8ebe20b3c2.png)

I will proceed to explain the idea in writing as well as providing live comentary over video.

So the idea is simple, we want event sourcing to keep the state of bank accounts that are going to be accessed via ATMs.

## The Domain
A bank account contains money which the owner can withdraw, and anybody can deposit to.
A bank account can have many owners.

## The Implementation: Event Sourcing using Kafka
The core idea was to use the key-value store from Kafka, RocksDB, to be able to recover the state of any entity during stateful processing.
