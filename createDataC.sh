
printf '{"id": "C"},{ "owner": {"name": "miguel"}, "type":"AddOwner"}
        {"id": "C"},{ "amount": 1, "type":"MakeDeposit"}' |  docker exec -i broker  \
      kafka-console-producer \
      --topic BankAccount-commands \
      --broker-list localhost:9092 \
      --property parse.key=true \
      --property key.separator=,
