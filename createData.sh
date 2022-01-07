
printf '{"id": "A"},{ "owner": {"name": "miguel"}, "type":"AddOwner"}
        {"id": "A"},{ "amount": 1, "type":"MakeDeposit"}
        {"id": "B"},{ "owner": {"name": "anastasia"}, "type":"AddOwner"}
        {"id": "B"},{ "amount": 1, "type":"MakeDeposit"}
        {"id": "A"},{ "amount": 1, "type":"MakeDeposit"}
        {"id": "A"},{ "amount": 1, "type":"MakeDeposit"}
        {"id": "A"},{ "amount": 1, "type":"MakeDeposit"}
        {"id": "B"},{ "amount": 1, "type":"MakeDeposit"}
        {"id": "B"},{ "amount": 1, "type":"MakeDeposit"}
        {"id": "A"},{ "amount": 1, "type":"MakeDeposit"}' |  docker exec -i broker  \
      kafka-console-producer \
      --topic BankAccount-commands \
      --broker-list localhost:9092 \
      --property parse.key=true \
      --property key.separator=,
