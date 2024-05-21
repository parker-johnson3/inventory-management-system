# Start from the stable Node 20.11.1 LTS version
FROM node:20.11.1

# Define communication ports as environment variables to avoid hardcoding them
# in applications
ENV WEBSERVER_HOST=web_server
ENV WEBSERVER_PORT=5000

# The primary directory will be the /src in all apps
RUN mkdir src
WORKDIR /src

# Install packages required by the react app
COPY package-lock.json .
COPY package.json .
RUN npm install

# Preserve any files generated within the container
RUN mkdir -p artifacts
VOLUME /src/artifacts
ENV ARTIFACTS_ROOT=/src/artifacts

COPY . .

# These don't really do much, but allow readers to know these ports will be
# exposed
EXPOSE 5000

# Uncomment these when you want to build the project
RUN npm run build

CMD ["npm", "start"]
# CMD ["sleep", "infinity"]
