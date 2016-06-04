
# Consensus

Simple web application that stores String key/value pairs and can be distributed to multiple servers.

Uses [Two-phase commit](https://en.wikipedia.org/wiki/Two-phase_commit_protocol) protocol for replicating data.

## Test cases

Run
```
./gradlew clean build buildjar
```
to build project.

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