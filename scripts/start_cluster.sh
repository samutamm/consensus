#!/usr/bin/env bash

#path to current dir
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#build project
cd ${DIR}/..
./gradlew clean build uberjar

#path to jar
DIR=${DIR}/../build/libs/consensus.jar

#start three instances in new terminals
xterm -e java -jar ${DIR} 8080 8081 8082 &
xterm -e java -jar ${DIR} 8081 8080 8082 &
xterm -e java -jar ${DIR} 8082 8080 8081