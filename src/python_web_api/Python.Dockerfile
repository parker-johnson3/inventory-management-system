# Start from the stable Python 3.11 version
FROM python:3.11

# Update software
RUN apt-get update && apt-get install --no-install-recommends -y \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# Define reused vars
ARG SERVICE_PORT=5000

# Define communication ports as environment variables to avoid hardcoding them
# in applications
ENV WEBSERVER_HOST=web_server
ENV PROXYSERVER_HOST=proxy_server
ENV FRONTEND_HOST=frontend
ENV WEBSERVER_PORT=${SERVICE_PORT}
ENV PROXYSERVER_PORT=8000
ENV FRONTEND_PORT=3000

# Environment variables required by Flask
ENV FLASK_APP=app.py

# Allow all hosts for now
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_RUN_PORT=${SERVICE_PORT}

# Change this to False for production
ENV FLASK_DEBUG=False

# These don't really do much, but allow readers to know these ports will be
# exposed
EXPOSE 5000
EXPOSE 8000

# The primary directory will be the /src in all apps
RUN mkdir src
WORKDIR /src

# Install dependencies for the Web Server
COPY requirements.txt .
RUN pip install -r requirements.txt

# Preserve any files generated within the container
RUN mkdir -p artifacts
VOLUME /src/artifacts
ENV ARTIFACTS_ROOT=/src/artifacts

COPY . .

# Install webserver modules
RUN pip install src/

CMD ["python3", "-u", "app.py"]
