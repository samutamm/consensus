#!/usr/bin/env bash

 xterm -e java -jar ~/Documents/INSA/MID/TP/TP4/consensus/build/libs/consensus.jar 8080 8081 8082 &
 xterm -e java -jar ~/Documents/INSA/MID/TP/TP4/consensus/build/libs/consensus.jar 8081 8080 8082 &
 xterm -e java -jar ~/Documents/INSA/MID/TP/TP4/consensus/build/libs/consensus.jar 8082 8080 8081