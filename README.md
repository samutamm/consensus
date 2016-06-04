
# Consensus

Simple web application that stores String key/value pairs and can be distributed to multiple servers.

Uses [Two-phase commit](https://en.wikipedia.org/wiki/Two-phase_commit_protocol) protocol for replicating data.

## Test cases

### Aborting cluster

Run
```
./scripts/aborting_cluster.sh
```
that starts two instances that are configured to have third neighbor.
Run
```
curl -XPUT -H "Content-Type: application/json" -d '{"value":"value"}' localhost:8080/key
```

Transaction will get aborted and no changes made.