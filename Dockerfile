FROM 100.125.17.64:20202/audit_organization/centos7-base:1.0.1


WORKDIR /home/apps/
ADD target/course-election-0.0.1-SNAPSHOT.jar .
ADD start.sh .
# ADD client.truststore.jks .
# ADD dms_kafka_client_jaas.conf .

ENTRYPOINT ["sh", "/home/apps/start.sh"]