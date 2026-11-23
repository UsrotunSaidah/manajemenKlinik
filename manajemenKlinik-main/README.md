# Manajemen Klinik

Aplikasi desktop Java untuk mengelola pasien, antrean, rekam medis, resep, dan pembayaran.

## Menjalankan aplikasi

Pastikan JDK tersedia di `PATH`, lalu jalankan dari folder proyek:

```powershell
New-Item -ItemType Directory -Force bin | Out-Null
javac -encoding UTF-8 -sourcepath src -d bin src\com\klinik\main\MainApp.java
java -cp bin com.klinik.main.MainApp
```

Data aplikasi disimpan secara lokal di folder `data`. File data dan hasil kompilasi
tidak disertakan dalam Git; gunakan data contoh yang sudah dianonimkan untuk berbagi proyek.