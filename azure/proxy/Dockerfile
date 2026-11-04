FROM nginx:stable-alpine
RUN apk add --no-cache ca-certificates
COPY default.conf /etc/nginx/conf.d/default.conf
EXPOSE 8080
