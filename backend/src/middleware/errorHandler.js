const HttpError = require('../utils/httpError');

const notFound = (req, _res, next) =>
  next(new HttpError(404, `Endpoint tidak ditemukan: ${req.method} ${req.originalUrl}`));

// eslint-disable-next-line no-unused-vars
function errorHandler(err, _req, res, _next) {
  if (err instanceof HttpError) {
    return res.status(err.status).json({
      success: false,
      message: err.message,
      data: err.errors ? { errors: err.errors } : null,
    });
  }
  if (err.type === 'entity.parse.failed') {
    return res.status(400).json({ success: false, message: 'Format JSON tidak valid', data: null });
  }
  console.error(err); // detail hanya di log server
  res.status(500).json({ success: false, message: 'Terjadi kesalahan pada server', data: null });
}

module.exports = { notFound, errorHandler };