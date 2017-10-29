FROM jboss/wildfly
EXPOSE 8080

COPY target/alpha-api.war /opt/jboss/wildfly/standalone/deployments/alpha-api.war
