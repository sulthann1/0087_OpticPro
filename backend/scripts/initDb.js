const fs = require('fs');
const path = require('path');
const pool = require('../db/pool');

(async () => {
  try {
    if (process.argv.includes('--reset')) {
      await pool.query(`DROP TABLE IF EXISTS klaim_garansi, jadwal_kunjungan, pesanan_kacamata, resep_lensa,
        katalog_frame, refresh_token, users CASCADE`);
      console.log('Semua tabel dihapus.');
    }
    await pool.query(fs.readFileSync(path.join(__dirname, '../db/schema.sql'), 'utf8'));
    console.log('Skema database siap.');
  } catch (e) {
    console.error('Gagal inisialisasi database:', e.message);
    process.exitCode = 1;
  } finally {
    await pool.end();
  }
})();