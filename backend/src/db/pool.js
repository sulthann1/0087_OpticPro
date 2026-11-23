const { Pool } = require('pg');
const env = require('../config/env');

const pool = new Pool({
  connectionString: env.databaseUrl,
  ssl: env.dbSsl ? { rejectUnauthorized: false } : false,
  max: 10,
});

pool.on('error', (err) => console.error('Kesalahan pool PostgreSQL:', err.message));

module.exports = pool;