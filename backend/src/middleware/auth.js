const jwt = require('jsonwebtoken');
const env = require('../config/env');
const HttpError = require('../utils/httpError');

// Wajib header Authorization: Bearer <accessToken>
function authenticate(req, _res, next) {
  const [scheme, token] = (req.headers.authorization || '').split(' ');
  if (scheme !== 'Bearer' || !token) return next(new HttpError(401, 'Token tidak ditemukan'));
  try {
    const p = jwt.verify(token, env.accessSecret);
    req.user = { id: Number(p.sub), role: p.role };
    next();
  } catch {
    next(new HttpError(401, 'Token tidak valid atau kedaluwarsa'));
  }
}

// Pembatasan berdasarkan peran
const requireRole = (...roles) => (req, _res, next) =>
  roles.includes(req.user.role) ? next() : next(new HttpError(403, 'Anda tidak memiliki akses'));

module.exports = { authenticate, requireRole };