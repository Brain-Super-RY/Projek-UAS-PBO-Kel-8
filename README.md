# 📸 Rancang Bangun Aplikasi Manajemen Jasa dan Penyewaan Studio Fotografi "Studio Kita" Berbasis Pemrograman Berorientasi Objek

Dokumentasi ini disusun untuk memenuhi tugas projek akhir mata kuliah **Pemrograman Berorientasi Objek (PBO)** Semester Genap. Aplikasi ini dikembangkan menggunakan bahasa pemrograman Java dengan menerapkan arsitektur *Model-View-Controller (MVC)* dan *Data Access Object (DAO)* untuk mengelola operasional jasa fotografi dan sewa alat pada **Studio Kita**.

---

## 👥 Anggota Kelompok 8
*   **Amelia Marliana** (NPM: 2510631170025)
*   **An'amatus Syafira Aulia Azahra** (NPM: 2510631170013)
*   **Raffa Hafizh Hauzaan** (NPM: 2510631170004)
*   **Rizky Yoga Salasa** (NPM: 2510631170049)

---

**Dosen Pengampu:** Purwantoro, S.Kom., M.Kom.  
**Program Studi:** Informatika  
**Universitas:** Universitas Singaperbangsa Karawang (UNSIKA)

---

## 🚀 Fitur Utama Aplikasi
Aplikasi **Studio Kita** dirancang dengan antarmuka berbasis desktop (Java Swing) yang mencakup fitur-fitur operasional utama berikut:
1.  **Sistem Autentikasi Keamanan:** Manajemen login multi-user yang membedakan hak akses tingkat *Super Admin*, *Kasir / Frontdesk*, dan *Customer*.
2.  **Panel Staf Admin:** Fitur *CRUD* terpusat untuk mendaftarkan akun staf baru, memantau daftar pengguna aktif, serta mencabut hak akses admin secara langsung.
3.  **Panel Rekap Transaksi:** Pencatatan otomatis nota pemesanan jasa foto dan penyewaan kamera, lengkap dengan fitur penyaringan (*filtering*) data dinamis berdasarkan status transaksi (`PENDING`, `APPROVED`, `DECLINED`, `SELESAI`).
4.  **Panel Finansial & Penghasilan:** Dashboard ringkasan finansial yang mengalkulasi total omzet pendapatan studio, jumlah transaksi sewa, serta akumulasi transaksi jasa foto secara *real-time*.

---

## 🏗️ Struktur Arsitektur Package (MVC + DAO)
Proyek ini menerapkan pemisahan tanggung jawab kode yang ketat (*Clean Architecture*) ke dalam beberapa struktur package berikut:

---

```text
studiokita/
├── database/     # Mengatur siklus koneksi ke server MySQL (Singleton Pattern)
├── model/        # Blueprint objek data/entitas (User, Transaksi, SewaAlat, JasaFoto)
├── dao/          # Penanganan khusus operasi query database SQL (Data Access Object)
├── controller/   # Jembatan logika bisnis yang menghubungkan aksi komponen View ke DAO
├── util/         # Helper utility (SessionManager, CurrencyFormatter Rupiah, DateFormatter)
└── view/         # Komponen-komponen antarmuka GUI Java Swing (Pure Coding)

```

---

## 🛠️ Teknologi & Library yang Digunakan

* **Bahasa Pemrograman:** Java (JDK 11 atau versi di atasnya)
* **Database Engine:** MySQL Server
* **GUI Framework:** Java Swing Pustaka Standar
* **Database Connector:** `mysql-connector-java` (JDBC Driver)

---

## 💻 Panduan Instalasi dan Pengaturan

### 1. Kloning Repositori

Lakukan kloning proyek ini ke direktori lokal komputer Anda terlebih dahulu:

```bash
git clone [https://github.com/Brain-Super-RY/Projek-UAS-PBO-Kel-8.git](https://github.com/Brain-Super-RY/Projek-UAS-PBO-Kel-8.git)
cd Projek-UAS-PBO-Kel-8

```

### 2. Konfigurasi Database (Pilih Salah Satu)

* **Opsi A: Menggunakan Database Online (Bawaan)**
Aplikasi ini sudah dikonfigurasi secara bawaan untuk terhubung langsung ke server database cloud secara online via internet. Anda bisa langsung menjalankan aplikasi tanpa perlu mengaktifkan database lokal.
* **Opsi B: Menggunakan Database Lokal (Offline Backup)**
Jika Anda ingin menjalankan aplikasi secara offline melalui `localhost` (XAMPP):
1. Aktifkan modul **Apache** dan **MySQL** pada XAMPP Control Panel Anda.
2. Buka browser dan akses `http://localhost/phpmyadmin/`.
3. Buat database baru dengan nama `studiokita`.
4. Pilih menu **Import**, lalu pilih file skema SQL proyek yang terletak di: `railway.sql`.
5. Sesuaikan kredensial host, username, dan password database Anda pada file kelas `KoneksiDB.java` di package `studiokita.database`.



### 3. Menjalankan Aplikasi

1. Buka proyek ini menggunakan IDE pilihan Anda (NetBeans / IntelliJ IDEA / Eclipse).
2. Lakukan proses **Clean and Build** pada IDE untuk memastikan library driver JDBC terpasang dengan benar.
3. Jalankan file utama aplikasi dengan klik **Run** setelah selesai **Clean and Build**

---

## 📄 Lisensi

Proyek ini dikembangkan murni untuk memenuhi tugas akademik UAS mata kuliah Pemrograman Berorientasi Objek Informatika UNSIKA. Hak cipta penuh dimiliki oleh pengembang Kelompok 8.
