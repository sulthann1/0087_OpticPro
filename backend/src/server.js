// src/server.js
const env = require('./config/env');
const app = require('./app');

app.listen(env.port, '0.0.0.0', () => console.log(`OpticPro API berjalan di http://localhost:${env.port}/api`));