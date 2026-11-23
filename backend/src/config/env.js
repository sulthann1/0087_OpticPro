require('dotenv').config();

const required = ['DATABASE_URL', 'JWT_ACCESS_SECRET', 'JWT_REFRESH_SECRET'];
const missing = required.filter((k) => !process.env[k]);
if (missing.length) {
  console.error(`Environment variable belum diisi: ${missing.join(', ')} (lihat .env.example)`);
  process.exit(1);
}

module.exports = {
  port: Number(process.env.PORT) || 3000,
  databaseUrl: process.env.DATABASE_URL,
  dbSsl: process.env.DB_SSL !== 'false',
  accessSecret: process.env.JWT_ACCESS_SECRET,
  refreshSecret: process.env.JWT_REFRESH_SECRET,
  accessTtl: process.env.ACCESS_TOKEN_TTL || '15m',
  refreshTtlDays: Number(process.env.REFRESH_TOKEN_TTL_DAYS) || 7,
};