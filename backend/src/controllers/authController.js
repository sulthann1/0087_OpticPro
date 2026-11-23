const auth = require('../services/authService');
const { ok } = require('../utils/response');

// Meneruskan error async ke errorHandler
const wrap = (fn) => (req, res, next) => fn(req, res).catch(next);

exports.register = wrap(async (req, res) =>
  ok(res, await auth.register(req.body || {}), 'Registrasi berhasil', 201));