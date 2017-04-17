FROM kanduat/alpine3.4-build-node6
MAINTAINER Dominik Bieringer <d.bieringer@dodax.com>

RUN mkdir -p /node_modules /tmp/build
ADD package.json /tmp/build/
RUN apk add --update git python make g++ bash && npm install -g angular-cli && cd /tmp/build && npm install && cp -r /tmp/build/node_modules/* /node_modules && rm -rf /tmp/build && apk del git python make g++
RUN echo 'if [ "/node_modules" -nt "/mnt/node_modules" ]; then rm -rf /mnt/node_modules; fi; if [ ! -e "/mnt/node_modules" ]; then cp -r /node_modules /mnt; fi' > /usr/bin/hook_user

CMD ["ng","serve","--host 0.0.0.0","--proxy-config proxy.conf.json"]
