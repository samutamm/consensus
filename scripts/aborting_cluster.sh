#!/usr/bin/env bash

# Starts two instances. Both are configured to have 2 neighbors but in reality, there is only one.
# When the client tries to put new entry, it will be aborted becouse all nodes do not agree with
# the commit.

xterm -e java -jar ~/Documents/INSA/MID/TP/TP4/consensus/build/libs/consensus.jar 8080 8081 8082 &
xterm -e java -jar ~/Documents/INSA/MID/TP/TP4/consensus/build/libs/consensus.jar 8081 8080 8082