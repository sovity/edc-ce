# Stage 1: Compile and Build angular codebase
FROM node:lts as build

WORKDIR /app
COPY ./ /app/
RUN npm install
RUN npm run ng build --prod --base-href=/ui/ --deploy-url=/ui/script/

# Stage 2: Serve app with nginx
FROM nginx:alpine
COPY --from=build /app/deployment/nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/edc-demo-client /usr/share/nginx/html/ui
COPY --from=build /app/src/assets /usr/share/nginx/html/ui/assets
EXPOSE 80

HEALTHCHECK --interval=2s --timeout=5s --retries=10 \
  CMD curl -f http://localhost/ || exit 1
