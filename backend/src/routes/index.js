// src/routes/index.js
const router = require('express').Router();
const { ok } = require('../utils/response');
const ctl = require('../controllers/authController');

router.get('/health', (_req, res) => ok(res, { status: 'ok' }, 'OpticPro API berjalan'));
router.post('/auth/register', ctl.register);

module.exports = router;