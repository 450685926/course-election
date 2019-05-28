FROM 100.125.17.64:20202/audit_organization/centos7-base:1.0.1
wget https://github.com/sgerrand/alpine-pkg-java-openjfx/releases/download/8.151.12-r0/java-openjfx-8.151.12-r0.apk 
apk --no-cache add --allow-untrusted ./java-openjfx-8.151.12-r0.apk

WORKDIR /home/apps/
ADD target/course-election-0.0.1-SNAPSHOT.jar .
ADD start.sh .
# ADD client.truststore.jks .
# ADD dms_kafka_client_jaas.conf .

ENTRYPOINT ["sh", "/home/apps/start.sh"]