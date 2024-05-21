# Maven dockerfiles available from https://github.com/carlossg/docker-maven
FROM maven:3.8.6-openjdk-11

# set work directory
WORKDIR /ssd_gen

# install pom packages
COPY src/etc /ssd_gen/src/etc
COPY pom.xml /ssd_gen
RUN mvn package

# freq changing files are added below
COPY . /ssd_gen

# use this to run the api server
CMD ["mvn", "quarkus:dev"]
