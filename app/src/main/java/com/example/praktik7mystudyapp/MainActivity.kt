package com.example.praktik7mystudyapp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager

    private lateinit var tvNamaApp: TextView
    private lateinit var tvSalam: TextView
    private lateinit var tvSubJudul: TextView
    private lateinit var tvMateriIsi: TextView
    private lateinit var tvJadwalIsi: TextView
    private lateinit var tvTugasIsi: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnPengaturan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settings = SettingsManager(this)

        tvNamaApp     = findViewById(R.id.tvNamaApp)
        tvSalam       = findViewById(R.id.tvSalam)
        tvSubJudul    = findViewById(R.id.tvSubJudul)
        tvMateriIsi   = findViewById(R.id.tvMateriIsi)
        tvJadwalIsi   = findViewById(R.id.tvJadwalIsi)
        tvTugasIsi    = findViewById(R.id.tvTugasIsi)
        tvRingkasan   = findViewById(R.id.tvRingkasan)
        btnPengaturan = findViewById(R.id.btnPengaturan)

        btnPengaturan.setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }
    }

    /** Dijalankan setiap halaman tampil, termasuk setelah kembali dari Pengaturan. */
    override fun onResume() {
        super.onResume()
        terapkanPengaturanTampilan()
    }

    private fun terapkanPengaturanTampilan() {
        // 1) ukuran teks
        val ukuran = settings.ukuranTeksSp()
        listOf(tvSubJudul, tvMateriIsi, tvJadwalIsi, tvTugasIsi).forEach {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, ukuran)
        }

        // 2) warna aksen
        val aksen = settings.warnaAksen()
        tvNamaApp.setTextColor(aksen)
        tvSalam.setTextColor(aksen)
        btnPengaturan.backgroundTintList = ColorStateList.valueOf(aksen)

        // 3) salam nama pengguna
        val nama = settings.ambilNama()
        tvSalam.visibility = if (nama.isEmpty()) View.GONE else View.VISIBLE
        tvSalam.text = getString(R.string.salam_format, nama)

        // 4) ringkasan pengaturan aktif
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)
    }
}