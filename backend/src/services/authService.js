const bcrypt = require('bcryptjs');
const pool = require('../db/pool');
const HttpError = require('../utils/httpError');
const { validateRegister } = require('../validators/auth');

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

module.exports = { register };