const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const pool = require('../db/pool');
const env = require('../config/env');

const sha256 = (s) => crypto.createHash('sha256').update(s).digest('hex');

const signAccess = (user) =>
  jwt.sign({ sub: user.id, role: user.role }, env.accessSecret, { expiresIn: env.accessTtl });

// Access token (singkat) + refresh token (panjang, hash-nya disimpan di database)
async function issueTokens(user) {
  const accessToken = signAccess(user);
  const refreshToken = jwt.sign({ sub: user.id, jti: crypto.randomUUID() }, env.refreshSecret, {
    expiresIn: `${env.refreshTtlDays}d`,
  });
  const expires = new Date(Date.now() + env.refreshTtlDays * 86400 * 1000);
  await pool.query(
    'INSERT INTO refresh_token (user_id, token_hash, expires_at) VALUES ($1,$2,$3)',
    [user.id, sha256(refreshToken), expires]
  );
  return { accessToken, refreshToken };
}

// Mencabut token; mengembalikan baris bila token valid dan belum dicabut
async function revoke(token) {
  const r = await pool.query(
    `UPDATE refresh_token SET revoked_at = now()
     WHERE token_hash = $1 AND revoked_at IS NULL AND expires_at > now() RETURNING user_id`,
    [sha256(token)]
  );
  return r.rows[0];
}

module.exports = { issueTokens, revoke };