// src/routes/index.js
const router = require('express').Router();
const { ok } = require('../utils/response');

router.get('/health', (_req, res) => ok(res, { status: 'ok' }, 'OpticPro API berjalan'));

module.exports = router;