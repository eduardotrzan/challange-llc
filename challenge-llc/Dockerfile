FROM openjdk:15-jdk-alpine

RUN apk add --no-cache bash

COPY challenge-llc-server/target/server.jar /opt/llc/server/server.jar
COPY start.sh /opt/llc/server/start.sh

RUN chmod -R o+x /opt/llc/server/ && chmod 777 /opt/llc/server/

ENV JAVA_OPTS=-javaagent:/opt/llc/server/server.jar

WORKDIR /opt/llc/server/

EXPOSE 8484

CMD /bin/bash /opt/llc/server/start.sh ${springProfile}
