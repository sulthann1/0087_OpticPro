require('dotenv').config();
const app = require('./app');

const port = Number(process.env.PORT) || 3000;
app.listen(port, '0.0.0.0', () => console.log(`OpticPro API berjalan di http://localhost:${port}/api`));