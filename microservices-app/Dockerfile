# Stage 1 — Build Angular app
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --prefer-offline --legacy-peer-deps
COPY . .
ENV NODE_OPTIONS=--max-old-space-size=4096
ENV NODE_TLS_REJECT_UNAUTHORIZED=0
RUN npm run build -- --configuration production


# Stage 2 — Serve with nginx
FROM nginx:alpine
COPY --from=builder /app/dist/microservices-app/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
