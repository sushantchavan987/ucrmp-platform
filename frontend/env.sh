#!/bin/sh

# Recreate config file
rm -rf /usr/share/nginx/html/env-config.js
touch /usr/share/nginx/html/env-config.js

# Add assignment
echo "window._env_ = {" >> /usr/share/nginx/html/env-config.js

# Read specific environment variable and write to file
# We use a default if the var is missing
echo "  VITE_API_BASE_URL: \"${VITE_API_BASE_URL:-http://localhost:8090/api/v1}\"," >> /usr/share/nginx/html/env-config.js

echo "}" >> /usr/share/nginx/html/env-config.js

# Start Nginx
nginx -g "daemon off;"