// Amplop respons seragam: {success, message, data}
const ok = (res, data = null, message = 'Berhasil', status = 200) =>
  res.status(status).json({ success: true, message, data });

module.exports = { ok };