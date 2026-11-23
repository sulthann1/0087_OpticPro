const router = require('express').Router();
const ctl = require('../controllers/authController');
const { authenticate, requireRole } = require('../middleware/auth');
const { ok } = require('../utils/response');

router.get('/health', (_req, res) => ok(res, { status: 'ok' }, 'OpticPro API berjalan'));

router.post('/auth/register', ctl.register);
router.post('/auth/login', ctl.login);
router.post('/auth/refresh', ctl.refresh);
router.post('/auth/logout', authenticate, ctl.logout);
router.get('/auth/me', authenticate, ctl.me);

// Rute khusus ADMIN untuk menguji 403 (fitur admin lain menyusul)
router.get('/admin/ping', authenticate, requireRole('ADMIN'), (_req, res) => ok(res, { pong: true }));

module.exports = router;