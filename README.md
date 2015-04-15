Productization Verification Tool(PVT)
===========

PVT handles package level verification for all JBoss MW products.

Prerequisite
=====
Maven 3.x
JDK 1.7+
Tomcat7+ or JBoss EAP 6.4+ or WildFly 8+

Build
=====
> mvn clean install

  Then you will get the `pvt.war` in `target/` directory

  This `pvt.war` can be used to deploy to JBoss Application Server.

To build for Tomcat:

> mvn -Ptomcat clean install

  Then you will get the `pvt.war` in `target/` directory

  This `pvt.war` can be used to deploy to Tomcat 7+.

Notes on swagger configuration
=====
There is a property named: `pvt.ip.address` with default value: `localhost:8080` defined in `pom.xml`.

This is used for Swagger UI to access the REST endpoints, to change it at `build` time, run(for example):

> mvn -Dpvt.ip.address=192.168.1.102:8080 clean install

Then you can access the Swagger UI using address: http://192.168.1.102:8080/pvt/rest/api-docs

Deploy
=====
After deploy to Tomcat or JBoss.

It can be accessed at address: http://localhost:8080/pvt/

And the REST API docs at: http://localhost:8080/pvt/rest/api-docs

