# WebServer

##### Note
All snippets in this document assume you're at the base directory of the project

## Requirements
The WebServer uses the `flask` library to set up a http server. All requirements are listed in the
[requirements.txt](./requirements.txt). Install the requirements using

```bash
cd src/python_web_api
pip install -r requirements.txt  # use pip3 if pip doesn't work
```

## Running the server
To start a development server locally, use
```bash
cd src/python_web_api
flask run
```

To start a development server in a docker container, use
```bash
cd src/python_web_api
docker build . -f Python.Dockerfile -t webserver:latest
docker run -d webserver:latest
```

If you'd like to expose the webserver running in a container on a local port, use
```bash
cd src/python_web_api
docker build . -f Python.Dockerfile -t webserver:latest

# Replace XXXX with the local port you'd like to use
docker run webserver:latest -d -p XXXX:5000
```
