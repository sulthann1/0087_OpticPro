# OpticPro

Aplikasi manajemen rekam ukuran resep lensa dan pesanan kacamata custom untuk toko optik lokal.
Project Akhir Pengembangan Aplikasi Mobile 2026 (Universitas Muhammadiyah Yogyakarta).

- **Penulis:** Sulthan Awaliya Firmansyah (NIM 20240140087)
- **Android:** Kotlin, Jetpack Compose, MVVM, Navigation Compose, Hilt, Retrofit
- **Backend:** Node.js + Express, JWT (access + refresh token)
- **Database:** PostgreSQL (Supabase), tanpa Room
- **Dokumen:** [SRS](docs/SRS_OpticPro_0087.docx)

## Struktur

```
backend/   REST API (Express) + skema dan seed database
android/   Aplikasi Android (Jetpack Compose)
docs/      SRS
postman/   Koleksi Postman
```

## Akun demo (setelah `npm run db:seed`)

| Peran | Email | Password |
| --- | --- | --- |
| ADMIN | admin@opticpro.test | Admin123 |
| CUSTOMER | budi@opticpro.test | Customer123 |
| CUSTOMER | siti@opticpro.test | Customer123 |

## Menjalankan backend

```bash
cd backend
cp .env.example .env        # isi DATABASE_URL (Supabase) dan rahasia JWT
npm install
npm run db:init             # membuat tabel
npm run db:seed             # akun demo
npm start                   # http://localhost:3000/api
npm run test:smoke          # uji alur autentikasi (server harus berjalan)
```

## Menjalankan aplikasi Android

Lihat [android/SETUP.md](android/SETUP.md).

## Progres mingguan

Status: ⬜ belum, 🟨 proses, ✅ selesai. Diperbarui tiap akhir minggu.

| Minggu | Target | Status | Catatan / bukti |
| --- | --- | --- | --- |
| 1 (22-28 Nov) | Repo, skema database, backend auth, login/register Android, token terenkripsi, navigasi per peran | ✅ | |
| 2 (29 Nov-5 Des) | Katalog Frame (CRUDS + upload foto), Resep Lensa (CRUDS) | ⬜ | |
| 3 (6-12 Des) | Pesanan (stok, alur status, pembayaran) | ⬜ | |
| 4 (13-19 Des) | PDF Kartu Resep dan Nota, File Tersimpan, Jadwal Kunjungan | ⬜ | |
| 5 (20-26 Des) | Klaim Garansi, Dashboard, uji alur lengkap | ⬜ | |
| 6 (27 Des-2 Jan) | Perapian, tes, README, APK, latihan presentasi | ⬜ | Batas pengumpulan 3 Januari 2027 |

## Status fitur (sesuai SRS)

| Fitur | Kode | Status |
| --- | --- | --- |
| Autentikasi dan sesi (JWT) | AUTH | 🟨 backend selesai, Android menunggu uji |
| Otorisasi berbasis peran | AUTHZ | 🟨 middleware backend selesai, navigasi per peran di Android menunggu uji |
| Katalog Frame | FRM | ⬜ |
| Resep Lensa | RSP | ⬜ |
| Pesanan Kacamata | PSN | ⬜ |
| Jadwal Kunjungan | JDW | ⬜ |
| Klaim Garansi | KLM | ⬜ |
| File lokal (PDF) | FIL | ⬜ |
| Dashboard Admin | DSH | ⬜ |
