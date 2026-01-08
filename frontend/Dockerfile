# --- STAGE 1: BUILD ---
FROM node:18-alpine as build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

# --- STAGE 2: SERVE (Non-Root Nginx) ---
FROM nginxinc/nginx-unprivileged:alpine

# Switch to root strictly for setup
USER root
RUN apk add --no-cache bash

# 1. Clean default directory
RUN rm -rf /usr/share/nginx/html/*

# 2. Copy static files FROM build stage
COPY --from=build /app/dist /usr/share/nginx/html

# 3. Copy Nginx Config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 4. Copy the Injector Script
COPY env.sh /docker-entrypoint.d/env.sh
RUN chmod +x /docker-entrypoint.d/env.sh

# ✅ THE FIX: Grant ownership of the HTML folder to the Nginx User (ID 101)
# This allows env.sh to write 'env-config.js' at runtime.
RUN chown -R 101:101 /usr/share/nginx/html

# Switch back to unprivileged Nginx user
USER 101

# Expose Non-Privileged Port
EXPOSE 8080

# Run the script
CMD ["/bin/sh", "/docker-entrypoint.d/env.sh"]