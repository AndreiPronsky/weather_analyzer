FROM amazoncorretto:17-alpine3.18 as extractor

# set working directory
WORKDIR /project

# copy the packaged jar file into our docker image
COPY build/libs/app.jar /project

# extract jar file
RUN java -Djarmode=layertools -jar app.jar extract

FROM amazoncorretto:17-alpine3.18
WORKDIR /application

# every copy command is creating new layer in the image
COPY --from=extractor /project/dependencies/. /application/.
COPY --from=extractor /project/spring-boot-loader/. /application/.
COPY --from=extractor /project/snapshot-dependencies/. /application/.
COPY --from=extractor /project/application/. /application/.
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]