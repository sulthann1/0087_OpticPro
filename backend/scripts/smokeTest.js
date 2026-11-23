const BASE = process.env.BASE_URL || 'http://localhost:3000/api';
let pass = 0;
let fail = 0;

async function call(method, path, body, token) {
  const res = await fetch(BASE + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });
  return { status: res.status, json: await res.json() };
}
function check(name, cond) {
  cond ? pass++ : fail++;
  console.log(`${cond ? 'LULUS' : 'GAGAL'}  ${name}`);
}

(async () => {
  const email = `uji${Date.now()}@opticpro.test`;
  const good = { nama_lengkap: 'Penguji Satu', email, no_telepon: '081234567890', password: 'Rahasia123', konfirmasi_password: 'Rahasia123' };
  const adminPw = process.env.SEED_ADMIN_PASSWORD || 'Admin123';

  let r = await call('GET', '/health');
  check('health 200', r.status === 200);
  r = await call('POST', '/auth/register', { ...good, password: 'pendek', konfirmasi_password: 'x' });
  check('register tidak valid -> 400 + pesan per field', r.status === 400 && r.json.data.errors.password && r.json.data.errors.konfirmasi_password);
  r = await call('POST', '/auth/register', { ...good, role: 'ADMIN' });
  check('register -> 201, peran CUSTOMER', r.status === 201 && r.json.data.role === 'CUSTOMER' && !('password_hash' in r.json.data));
  r = await call('POST', '/auth/register', good);
  check('email duplikat -> 409', r.status === 409);
  r = await call('POST', '/auth/login', { email, password: 'salah123' });
  check('password salah -> 401', r.status === 401 && r.json.message === 'Email atau password salah');
  r = await call('POST', '/auth/login', { email: 'tidakada@x.com', password: 'salah123' });
  check('email tak dikenal -> 401 (pesan sama)', r.status === 401 && r.json.message === 'Email atau password salah');
  r = await call('POST', '/auth/login', { email, password: 'Rahasia123' });
  check('login -> 200 + token', r.status === 200 && r.json.data.accessToken && r.json.data.refreshToken);
  const { accessToken, refreshToken } = r.json.data;
  r = await call('GET', '/auth/me');
  check('tanpa token -> 401', r.status === 401);
  r = await call('GET', '/auth/me', null, 'token.palsu.x');
  check('token palsu -> 401', r.status === 401);
  r = await call('GET', '/auth/me', null, accessToken);
  check('me dengan token -> 200', r.status === 200 && r.json.data.email === email);
  r = await call('GET', '/admin/ping', null, accessToken);
  check('CUSTOMER ke endpoint ADMIN -> 403', r.status === 403);
  r = await call('POST', '/auth/refresh', { refreshToken });
  check('refresh -> token baru (rotasi)', r.status === 200 && r.json.data.refreshToken !== refreshToken);
  const next = r.json.data;
  r = await call('POST', '/auth/refresh', { refreshToken });
  check('refresh token lama dipakai ulang -> 401', r.status === 401);
  r = await call('POST', '/auth/logout', { refreshToken: next.refreshToken }, next.accessToken);
  check('logout -> 200', r.status === 200);
  r = await call('POST', '/auth/refresh', { refreshToken: next.refreshToken });
  check('refresh setelah logout -> 401', r.status === 401);
  r = await call('POST', '/auth/login', { email: 'admin@opticpro.test', password: adminPw });
  check('login ADMIN seed -> 200', r.status === 200 && r.json.data.user.role === 'ADMIN');
  r = await call('GET', '/admin/ping', null, r.json.data.accessToken);
  check('ADMIN ke endpoint ADMIN -> 200', r.status === 200);
  r = await call('GET', '/tidak-ada');
  check('rute tak dikenal -> 404 JSON', r.status === 404 && r.json.success === false);

  console.log('');
  console.log(`${pass} lulus, ${fail} gagal`);
  process.exit(fail ? 1 : 0);
})().catch((e) => {
  console.error('Server tidak terjangkau?', e.message);
  process.exit(1);
});