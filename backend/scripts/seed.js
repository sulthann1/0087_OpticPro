require('../src/config/env');
const bcrypt = require('bcryptjs');
const pool = require('../src/db/pool');

const adminPw = process.env.SEED_ADMIN_PASSWORD || 'Admin123';
const custPw = process.env.SEED_CUSTOMER_PASSWORD || 'Customer123';

const users = [
  ['Admin OpticPro', 'admin@opticpro.test', adminPw, '081200000001', 'ADMIN'],
  ['Budi Santoso', 'budi@opticpro.test', custPw, '081200000002', 'CUSTOMER'],
  ['Siti Rahma', 'siti@opticpro.test', custPw, '081200000003', 'CUSTOMER'],
];

(async () => {
  try {
    for (const [nama, email, pw, telp, role] of users) {
      const hash = await bcrypt.hash(pw, 10);
      await pool.query(
        `INSERT INTO users (nama_lengkap, email, password_hash, no_telepon, role)
         VALUES ($1,$2,$3,$4,$5) ON CONFLICT (lower(email)) DO NOTHING`,
        [nama, email, hash, telp, role]
      );
    }
    console.log('Seed akun demo selesai.');
  } catch (e) {
    console.error('Gagal seed:', e.message);
    process.exitCode = 1;
  } finally {
    await pool.end();
  }
})();