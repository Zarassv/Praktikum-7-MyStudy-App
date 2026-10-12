package com.example.praktik7mystudyapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged

class PengaturanActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager
    private lateinit var tvRingkasan: TextView
    private lateinit var tvStatusNotifikasi: TextView

    // supaya pengembalian pilihan bahasa tidak memicu dialog lagi
    private var sedangMemulihkan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan)

        settings = SettingsManager(this)

        tvRingkasan = findViewById(R.id.tvRingkasan)
        tvStatusNotifikasi = findViewById(R.id.tvStatusNotifikasi)

        val etNama = findViewById<EditText>(R.id.etNama)
        val rgBahasa = findViewById<RadioGroup>(R.id.rgBahasa)
        val rgTema = findViewById<RadioGroup>(R.id.rgTema)
        val rgUkuran = findViewById<RadioGroup>(R.id.rgUkuran)
        val rgAksen = findViewById<RadioGroup>(R.id.rgAksen)
        val swNotifikasi = findViewById<SwitchCompat>(R.id.swNotifikasi)

        // 1) TAMPILKAN KEADAAN AWAL dulu
        isiKeadaanAwal(etNama, rgBahasa, rgTema, rgUkuran, rgAksen, swNotifikasi)

        // 2) BARU pasang listener
        etNama.doAfterTextChanged { settings.simpanNama(it.toString().trim()) }

        rgBahasa.setOnCheckedChangeListener { _, id ->
            if (sedangMemulihkan) return@setOnCheckedChangeListener

            val baru = if (id == R.id.rbEnglish) SettingsManager.BAHASA_EN
            else SettingsManager.BAHASA_ID
            if (baru == settings.ambilBahasa()) return@setOnCheckedChangeListener

            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_bahasa_judul)
                .setMessage(R.string.dialog_bahasa_pesan)
                .setCancelable(false)
                .setNegativeButton(R.string.dialog_batal) { _, _ ->
                    // kembalikan pilihan radio tanpa memicu listener lagi
                    sedangMemulihkan = true
                    rgBahasa.check(
                        if (settings.ambilBahasa() == SettingsManager.BAHASA_EN) R.id.rbEnglish
                        else R.id.rbIndonesia
                    )
                    sedangMemulihkan = false
                }
                .setPositiveButton(R.string.dialog_ya_ganti) { _, _ ->
                    settings.simpanBahasa(baru)
                    SettingsManager.terapkanKeAplikasi(this)   // teks langsung berganti
                }
                .show()
        }

        rgTema.setOnCheckedChangeListener { _, id ->
            settings.simpanTema(
                when (id) {
                    R.id.rbGelap  -> SettingsManager.TEMA_GELAP
                    R.id.rbSistem -> SettingsManager.TEMA_SISTEM
                    else          -> SettingsManager.TEMA_TERANG
                }
            )
            SettingsManager.terapkanKeAplikasi(this)           // tampilan langsung berganti
        }

        rgUkuran.setOnCheckedChangeListener { _, id ->
            val nilai = when (id) {
                R.id.rbKecil -> SettingsManager.UKURAN_KECIL
                R.id.rbBesar -> SettingsManager.UKURAN_BESAR
                else         -> SettingsManager.UKURAN_SEDANG
            }
            settings.simpanUkuran(nilai)
            perbaruiRingkasan()
        }

        rgAksen.setOnCheckedChangeListener { _, id ->
            settings.simpanAksen(
                when (id) {
                    R.id.rbBiru   -> SettingsManager.AKSEN_BIRU
                    R.id.rbOranye -> SettingsManager.AKSEN_ORANYE
                    else          -> SettingsManager.AKSEN_HIJAU
                }
            )
        }

        swNotifikasi.setOnCheckedChangeListener { _, aktif ->
            settings.simpanNotifikasi(aktif)
            perbaruiRingkasan()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener { konfirmasiReset() }
    }

    override fun onResume() {
        super.onResume()
        perbaruiRingkasan()
    }

    // ================= keadaan awal =================
    private fun isiKeadaanAwal(
        etNama: EditText,
        rgBahasa: RadioGroup,
        rgTema: RadioGroup,
        rgUkuran: RadioGroup,
        rgAksen: RadioGroup,
        swNotifikasi: SwitchCompat
    ) {
        etNama.setText(settings.ambilNama())

        rgBahasa.check(
            if (settings.ambilBahasa() == SettingsManager.BAHASA_EN) R.id.rbEnglish
            else R.id.rbIndonesia
        )
        rgTema.check(
            when (settings.ambilTema()) {
                SettingsManager.TEMA_GELAP  -> R.id.rbGelap
                SettingsManager.TEMA_SISTEM -> R.id.rbSistem
                else                        -> R.id.rbTerang
            }
        )
        rgUkuran.check(
            when (settings.ambilUkuran()) {
                SettingsManager.UKURAN_KECIL -> R.id.rbKecil
                SettingsManager.UKURAN_BESAR -> R.id.rbBesar
                else -> R.id.rbSedang
            }
        )
        rgAksen.check(
            when (settings.ambilAksen()) {
                SettingsManager.AKSEN_BIRU   -> R.id.rbBiru
                SettingsManager.AKSEN_ORANYE -> R.id.rbOranye
                else                         -> R.id.rbHijau
            }
        )
        swNotifikasi.isChecked = settings.ambilNotifikasi()
        perbaruiRingkasan()
    }

    // ================= ringkasan pengaturan aktif =================
    private fun perbaruiRingkasan() {
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)
        tvStatusNotifikasi.text = RingkasanPengaturan.statusNotifikasi(this, settings)
    }

    // ================= reset dengan konfirmasi =================
    private fun konfirmasiReset() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_reset_judul)
            .setMessage(R.string.dialog_reset_pesan)
            .setNegativeButton(R.string.dialog_batal, null)
            .setPositiveButton(R.string.dialog_ya) { _, _ ->
                settings.reset()                                   // hapus semua pengaturan
                Toast.makeText(
                    this, getString(R.string.toast_reset), Toast.LENGTH_SHORT
                ).show()
                SettingsManager.terapkanKeAplikasi(this)           // kembali ke bahasa & tema awal
                recreate()                                         // segarkan halaman ini
            }
            .show()
    }
}