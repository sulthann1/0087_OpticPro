CREATE TABLE IF NOT EXISTS users (
  id            SERIAL PRIMARY KEY,
  nama_lengkap  VARCHAR(100) NOT NULL,
  email         VARCHAR(150) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  no_telepon    VARCHAR(20)  NOT NULL,
  role          VARCHAR(10)  NOT NULL CHECK (role IN ('ADMIN','CUSTOMER')),
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS users_email_uq ON users (lower(email));

CREATE TABLE IF NOT EXISTS refresh_token (
  id         SERIAL PRIMARY KEY,
  user_id    INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash CHAR(64) NOT NULL UNIQUE,
  expires_at TIMESTAMPTZ NOT NULL,
  revoked_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS katalog_frame (
  id            SERIAL PRIMARY KEY,
  merk          VARCHAR(50)  NOT NULL,
  nama_model    VARCHAR(100) NOT NULL,
  bahan         VARCHAR(15)  NOT NULL CHECK (bahan IN ('Plastik','Metal','Titanium')),
  jenis_bingkai VARCHAR(15)  NOT NULL CHECK (jenis_bingkai IN ('Full Rim','Half Rim','Rimless')),
  warna         VARCHAR(40)  NOT NULL,
  harga         INTEGER NOT NULL CHECK (harga > 0),
  stok          INTEGER NOT NULL DEFAULT 0 CHECK (stok >= 0),
  gambar_url    TEXT,
  deskripsi     VARCHAR(500),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS resep_lensa (
  id                SERIAL PRIMARY KEY,
  user_id           INTEGER NOT NULL REFERENCES users(id),
  diperiksa_oleh    INTEGER REFERENCES users(id),
  tanggal_periksa   DATE NOT NULL,
  od_sph NUMERIC(4,2) NOT NULL, od_cyl NUMERIC(4,2) NOT NULL, od_axis SMALLINT CHECK (od_axis BETWEEN 0 AND 180),
  os_sph NUMERIC(4,2) NOT NULL, os_cyl NUMERIC(4,2) NOT NULL, os_axis SMALLINT CHECK (os_axis BETWEEN 0 AND 180),
  add_value         NUMERIC(3,2) CHECK (add_value > 0 AND add_value <= 4),
  pd                SMALLINT NOT NULL CHECK (pd BETWEEN 40 AND 80),
  catatan           VARCHAR(500),
  status_verifikasi VARCHAR(20) NOT NULL DEFAULT 'Belum Terverifikasi'
                    CHECK (status_verifikasi IN ('Belum Terverifikasi','Terverifikasi')),
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS pesanan_kacamata (
  id                SERIAL PRIMARY KEY,
  kode_pesanan      VARCHAR(20) NOT NULL UNIQUE,
  user_id           INTEGER NOT NULL REFERENCES users(id),
  resep_id          INTEGER NOT NULL REFERENCES resep_lensa(id),
  frame_id          INTEGER NOT NULL REFERENCES katalog_frame(id),
  jenis_lensa       VARCHAR(15) NOT NULL CHECK (jenis_lensa IN ('Single Vision','Bifocal','Progresif')),
  coating           VARCHAR(15) NOT NULL CHECK (coating IN ('Tanpa','Anti-Radiasi','Photochromic')),
  harga_frame       INTEGER NOT NULL,
  harga_lensa       INTEGER NOT NULL,
  total_harga       INTEGER NOT NULL,
  status_pengerjaan VARCHAR(20) NOT NULL DEFAULT 'Menunggu Konfirmasi'
                    CHECK (status_pengerjaan IN ('Menunggu Konfirmasi','Diproses','Siap Diambil','Selesai','Dibatalkan')),
  status_pembayaran VARCHAR(15) NOT NULL DEFAULT 'Belum Bayar' CHECK (status_pembayaran IN ('Belum Bayar','DP','Lunas')),
  catatan           VARCHAR(300),
  tanggal_selesai   TIMESTAMPTZ,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS jadwal_kunjungan (
  id            SERIAL PRIMARY KEY,
  user_id       INTEGER NOT NULL REFERENCES users(id),
  tanggal       DATE NOT NULL,
  jam           CHAR(5) NOT NULL,
  keluhan       VARCHAR(300),
  status        VARCHAR(15) NOT NULL DEFAULT 'Menunggu' CHECK (status IN ('Menunggu','Dikonfirmasi','Selesai','Dibatalkan')),
  catatan_admin VARCHAR(300),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS jadwal_slot_aktif_uq ON jadwal_kunjungan (tanggal, jam) WHERE status IN ('Menunggu','Dikonfirmasi');

CREATE TABLE IF NOT EXISTS klaim_garansi (
  id            SERIAL PRIMARY KEY,
  user_id       INTEGER NOT NULL REFERENCES users(id),
  pesanan_id    INTEGER NOT NULL REFERENCES pesanan_kacamata(id),
  jenis_klaim   VARCHAR(20) NOT NULL CHECK (jenis_klaim IN ('Kerusakan Frame','Lensa Tergores','Servis')),
  deskripsi     VARCHAR(500) NOT NULL,
  tanggal_klaim DATE NOT NULL DEFAULT CURRENT_DATE,
  status        VARCHAR(15) NOT NULL DEFAULT 'Diajukan' CHECK (status IN ('Diajukan','Disetujui','Ditolak','Selesai')),
  catatan_admin VARCHAR(300),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);