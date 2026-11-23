const express = require('express');

const app = express();
app.use(express.json({ limit: '100kb' }));

app.get('/api/health', (req, res) => {
  res.json({ success: true, message: 'OpticPro API berjalan', data: { status: 'ok' } });
});

module.exports = app;