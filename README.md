## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

In order to run a mongoDB instance:
```shell script
docker run -ti --rm -p 27017:27017 mongo:4.0
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Build the image locally
./mvnw clean package -Dquarkus.container-image.build=true -Dquarkus.container-image.name=michelepapale1996/estate-notifier-bot -Dquarkus.container-image.group=""

to run the image:
docker run michelepapale1996/estate-notifier-bot:1.0.0-SNAPSHOT

then, to push:
docker push michelepapale1996/estate-notifier-bot:1.0.0