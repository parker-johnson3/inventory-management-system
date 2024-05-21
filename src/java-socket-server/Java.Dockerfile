# Start from gradle image with Java 17
FROM gradle:7.2.0-jdk17

# Define communication ports as environment variables to avoid hardcoding them
# in applications
ENV WEBSERVER_HOST=web_server
ENV PROXYSERVER_HOST=proxy_server
ENV SQL_SERVER_HOST=sql_server
ENV WEBSERVER_PORT=5000
ENV PROXYSERVER_PORT=8000
ENV SQL_SERVER_PORT=3306

# These don't really do much, but allow readers to know these ports will be
# exposed
EXPOSE 8000
EXPOSE 3306

# The primary directory will be the /src in all apps
RUN mkdir src
WORKDIR /src

# Preserve any files generated within the container
RUN mkdir -p artifacts
VOLUME /src/artifacts
ENV ARTIFACTS_ROOT=/src/artifacts

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew jar
RUN mv /src/build/libs/ProxyServer.jar /src/ProxyServer.jar

CMD ["java", "-jar", "ProxyServer.jar", "-w", "5", "-l", "5", "-t", "1000", "-s", "1024", "-p", "8000"]
