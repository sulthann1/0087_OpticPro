const pool = require('../src/db/pool');

pool.query('select now() as waktu')
  .then((r) => console.log('Terhubung:', r.rows[0]))
  .catch((e) => console.error('Gagal:', e.message))
  .finally(() => pool.end());