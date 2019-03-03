# Basic docker container with minimal layer
FROM alpine
LABEL maintainer="jihoonkim1004@gmail.com"
RUN apk add --update nodejs nodejs-npm
COPY ./src/main/jsapp
WORKDIR /src/main/jsapp
RUN npm install
RUN npm run dist
EXPOSE 8080
ENTRYPOINT ["npm", "run", "serve:dist"]
