#!/usr/bin/env bash

#path to current dir
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#build project
cd ${DIR}/..
./gradlew clean build uberjar

#path to jar
DIR=${DIR}/../build/libs/consensus.jar

# Starts two instances. Both are configured to have 2 neighbors but in reality, there is only one.
# When the client tries to put new entry, it will be aborted becouse all nodes do not agree with
# the commit.

xterm -e java -jar ${DIR} 8080 8081 8082 &
xterm -e java -jar ${DIR} 8081 8080 8082