const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function validateRegister(b) {
  const e = {};
  const nama = String(b.nama_lengkap || '').trim();
  if (nama.length < 3 || nama.length > 100) e.nama_lengkap = 'Nama harus 3 sampai 100 karakter';
  const email = String(b.email || '').trim();
  if (!EMAIL_RE.test(email) || email.length > 150) e.email = 'Format email tidak valid';
  const telp = String(b.no_telepon || '').trim();
  if (!/^\d{10,15}$/.test(telp)) e.no_telepon = 'No telepon harus 10 sampai 15 digit';
  const pw = typeof b.password === 'string' ? b.password : '';
  if (pw.length < 8 || !/[A-Za-z]/.test(pw) || !/\d/.test(pw)) {
    e.password = 'Password minimal 8 karakter dan berisi huruf serta angka';
  }
  if (b.konfirmasi_password !== pw) e.konfirmasi_password = 'Konfirmasi password tidak sama';
  return {
    errors: e,
    value: { nama_lengkap: nama, email: email.toLowerCase(), no_telepon: telp, password: pw },
  };
}

module.exports = { validateRegister };