# YISAS GPRO INTEGRATION

## Introducción

Pending.

* Nombre del microservicio: yisas-gprointegration

## Desarrollo

### Prerequisitos

* Un IDE con soporte al proyecto Lombok (<https://projectlombok.org/>): Eclipse, IntelliJ, Visual Studio Code.
* Máquina versión Java instalada. Bien:
** JKD 21 de Oracle
* Maven: No necesario, está integrado en el proyecto mediante "maven wrapper" / mvnw (<https://github.com/takari/maven-wrapper>)
* Docker: para construir y ejecutar imágenes Docker -
** Windows / Mac: <https://www.docker.com/products/docker-desktop>
** WSL (Windows Subsystem Linux) + Docker Desktop: <https://nickjanetakis.com/blog/setting-up-docker-for-windows-and-wsl-to-work-flawlessly>
** WSL + Remote Docker Server: <https://dev.to/sebagomez/installing-the-docker-client-on-windows-subsystem-for-linux-ubuntu-3cgd>
** Ubuntu: <https://docs.docker.com/install/linux/docker-ce/ubuntu/> - Ubuntu

### Configuración

* Se configura el registro en un servidor Eureka en el fichero application.yaml

### Variables de entorno y valores por defecto

Se deben tener las siguientes variables de entorno para poder arrancar la aplicación

* EUREKA_URI -> <http://localhost:8761/eureka>

### Banner de arranque

* Banner generado con la fuente alligator2 (https://devops.datenkollektiv.de/banner.txt/index.html)


### Ejecución en desarrollo

Se deben tener las variables de entorno configuradas y ejecutar, dentro del proyecto, el siguiente comando:

```sh
$ cd gprointegration
$ EUREKA_URI=http://localhost:8761/eureka
$ ./mvnw spring-boot:run
```

## Docker

### Variables Docker

El dockerfile incluye las mismas variables de entorno para poder configurar el endpoint de Eureka

* EUREKA_URI -> <http://localhost:8761/eureka>
* CONFIG_SERVER -> <http://localhost:8888>

### Construcción de imagen Docker

El nombre de la imagen se genera a partir de dos propiedades definidas en el pom.xml

${docker.image.prefix}/${artifactId} -> yisasthemanuel/gprointegration

```sh
$ cd gprointegration
$ ./mvnw clean package
```

### Ejecución de la imagen

```shell
docker run -d -e EUREKA_URI=http://host.docker.internal:8761/eureka -e CONFIG_SERVER=http://host.docker.internal:8888 -e SPRING_PROFILES_ACTIVE=development -p 9085:8080 --name yisas-gprointegration yisasthemanuel/gprointegration:<version>
```

## Changelog [ES]


## TO DOs

* Tests en H2
* Incluir la gestión de equipos


## Referencias [EN]

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/htmlsingle/#production-ready)
* [Spring Boot Monitor metrics with Prometheus](https://www.callicoder.com/spring-boot-actuator-metrics-monitoring-dashboard-prometheus-grafana/)
* [Spring Boot Thin Launcher](https://github.com/spring-projects-experimental/spring-boot-thin-launcher)
* [Spring Boot Thin Launcher & Docker](https://dev.to/bufferings/spring-boot-thin-launcher-anddocker-2oa7)

## Guías [EN]

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
