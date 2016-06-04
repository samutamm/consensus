
# Consensus

Simple web application that stores String key/value pairs and can be distributed to multiple servers.

Uses [Two-phase commit](https://en.wikipedia.org/wiki/Two-phase_commit_protocol) protocol for replicating data.

## Examples

Run
```
./gradlew clean build buildjar
```
to build project.

Start instance ``` java -jar pathToProject/build/libs/consensus.jar 7000 7001 7002 ```
where 7000 is port for this instance and ports 7001 7002 of other instances. The number of instances is not limited.

GET ``` curl localhost:7000/example ``` where example is key of key/pair entry.

PUT ``` curl -XPUT -H "Content-Type: application/json" -d '{"value":"newvalue"}' localhost:8080/bolobolo ```
where bolobolo is the key and newvalue the value of new entry.

## Todo
Delete functionality.

## Test cases

### Commit

Run
```
./scripts/start_cluster.sh
```
that starts three instances that are configured to have each others as neighbors.
Run
```
curl -XPUT -H "Content-Type: application/json" -d '{"value":"value"}' localhost:8080/key
```
and new entry will be replicated to other nodes. Check with curl or go to http://localhost:8082/key.

### Abort

Run
```
./scripts/aborting_cluster.sh
```
that starts two instances that are configured to have third, non existant neighbor.
Run
```
curl -XPUT -H "Content-Type: application/json" -d '{"value":"value"}' localhost:8080/key
```

Transaction will get aborted and no changes made.