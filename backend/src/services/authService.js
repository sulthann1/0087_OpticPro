const bcrypt = require('bcryptjs');
const pool = require('../db/pool');
const HttpError = require('../utils/httpError');
const { validateRegister } = require('../validators/auth');
const tokens = require('./tokenService');
const jwt = require('jsonwebtoken');
const env = require('../config/env');


const publicUser = (u) => ({
  id: u.id,
  nama_lengkap: u.nama_lengkap,
  email: u.email,
  no_telepon: u.no_telepon,
  role: u.role,
});

// Register selalu membuat CUSTOMER (field role dari klien diabaikan)
async function register(body) {
  const { errors, value } = validateRegister(body);
  if (Object.keys(errors).length) throw new HttpError(400, 'Data tidak valid', errors);

  const dup = await pool.query('SELECT 1 FROM users WHERE lower(email) = $1', [value.email]);
  if (dup.rowCount) throw new HttpError(409, 'Email sudah terdaftar', { email: 'Email sudah terdaftar' });

  const hash = await bcrypt.hash(value.password, 10);
  const r = await pool.query(
    `INSERT INTO users (nama_lengkap, email, password_hash, no_telepon, role)
     VALUES ($1,$2,$3,$4,'CUSTOMER') RETURNING *`,
    [value.nama_lengkap, value.email, hash, value.no_telepon]
  );
  return publicUser(r.rows[0]);
}

async function login({ email, password }) {
  const fail = new HttpError(401, 'Email atau password salah'); // pesan sama untuk email/password salah
  if (typeof email !== 'string' || typeof password !== 'string') throw fail;

  const r = await pool.query('SELECT * FROM users WHERE lower(email) = $1', [email.trim().toLowerCase()]);
  const user = r.rows[0];
  const valid = user ? await bcrypt.compare(password, user.password_hash) : false;
  if (!valid) throw fail;

  return { ...(await tokens.issueTokens(user)), user: publicUser(user) };
}

// Rotasi: token lama dicabut, pasangan token baru diterbitkan
async function refresh(refreshToken) {
  const fail = new HttpError(401, 'Sesi berakhir, silakan login kembali');
  if (typeof refreshToken !== 'string') throw fail;
  let payload;
  try {
    payload = jwt.verify(refreshToken, env.refreshSecret);
  } catch {
    throw fail;
  }
  const revoked = await tokens.revoke(refreshToken);
  if (!revoked || revoked.user_id !== Number(payload.sub)) throw fail;
  const r = await pool.query('SELECT * FROM users WHERE id = $1', [payload.sub]);
  if (!r.rows[0]) throw fail;
  return { ...(await tokens.issueTokens(r.rows[0])), user: publicUser(r.rows[0]) };
}

async function logout(refreshToken) {
  if (typeof refreshToken === 'string') await tokens.revoke(refreshToken);
}

async function me(userId) {
  const r = await pool.query('SELECT * FROM users WHERE id = $1', [userId]);
  if (!r.rows[0]) throw new HttpError(401, 'Akun tidak ditemukan');
  return publicUser(r.rows[0]);
}

module.exports = { register,login, refresh, logout, me };