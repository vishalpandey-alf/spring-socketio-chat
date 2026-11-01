#!/bin/bash
export SOCKET_HOST=0.0.0.0
export SOCKET_ALLOWED_ORIGIN='*'
# After the app starts, make both ports public (needs the gh CLI; ignore errors)
( sleep 30; gh codespace ports visibility 8080:public 9092:public -c "$CODESPACE_NAME" ) &
mvn spring-boot:run
