# Maven dockerfiles available from https://github.com/carlossg/docker-maven
FROM maven:3.8.6-openjdk-11

# set work directory
WORKDIR /facogen

# install pom packages
COPY src/etc /facogen/src/etc
COPY pom.xml /facogen
RUN mvn package

# freq changing files are added below
COPY . /facogen

# use this to run the api server
CMD ["mvn", "quarkus:dev"]
