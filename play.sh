#!/bin/bash
docker build -f Dockerfile.build -t accepter-build-play . && docker run -ti -v `pwd`:/mnt -p 9000:9000 accepter-build-play $*
