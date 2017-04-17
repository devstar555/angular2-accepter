#!/bin/bash
cd ng-reason && docker build -t accepter-build-angular . && docker run -ti -v `pwd`:/mnt -p 4200:4200 accepter-build-angular $*
