echo "Creating topic commands"
docker exec -i broker \
kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic BankAccount-commands \
  --create \
  --config "cleanup.policy=compact" \
  --config "retention.ms=-1"
#  --config "cleanup.policy=delete" \
#  --config "retention.ms=160000"

echo "Creating topic commands"
docker exec -i broker \
kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic BankAccount-commands-zippedWithIndex \
  --create \
  --config "cleanup.policy=compact" \
  --config "retention.ms=-1"

#  --config "cleanup.policy=delete" \
#  --config "retention.ms=60000"


echo "Creating topic events"
docker exec -i broker \
kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic BankAccount-events \
  --create \
  --config "cleanup.policy=compact" \
  --config "retention.ms=-1"

echo "Creating topic snapshot"
docker exec -i broker \
kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic BankAccount-snapshots \
  --create \
  --config "cleanup.policy=compact" \
  --config "retention.ms=-1"