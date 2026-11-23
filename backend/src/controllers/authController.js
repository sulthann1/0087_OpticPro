const auth = require('../services/authService');
const { ok } = require('../utils/response');

// Meneruskan error async ke errorHandler
const wrap = (fn) => (req, res, next) => fn(req, res).catch(next);

exports.register = wrap(async (req, res) =>
  ok(res, await auth.register(req.body || {}), 'Registrasi berhasil', 201));

exports.login = wrap(async (req, res) => ok(res, await auth.login(req.body || {}), 'Login berhasil'));

exports.refresh = wrap(async (req, res) =>
  ok(res, await auth.refresh((req.body || {}).refreshToken), 'Token diperbarui'));
exports.logout = wrap(async (req, res) => {
  await auth.logout((req.body || {}).refreshToken);
  ok(res, null, 'Logout berhasil');
});
exports.me = wrap(async (req, res) => ok(res, await auth.me(req.user.id)));