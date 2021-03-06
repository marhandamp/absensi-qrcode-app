package com.app.absensi.ui.dosen

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.app.absensi.MainActivity
import com.app.absensi.R
import com.app.absensi.SessionManager
import com.app.absensi.databinding.ActivityDosenBinding
import com.app.absensi.data.response.LogoutDosenResponse
import com.app.absensi.network.RetrofitClient
import com.app.absensi.ui.dosen.presensi.MatakuliahActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDosenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAttendance.setOnClickListener {
            Intent(this@DosenActivity, MatakuliahActivity::class.java).also {
                it.putExtra("KODE_FITUR", 1)
                startActivity(it)
            }
        }

        binding.btnCourseList.setOnClickListener {
            Intent(this@DosenActivity, MatakuliahActivity::class.java).also {
                it.putExtra("KODE_FITUR", 2)
                startActivity(it)
            }
        }

//        val PREFS_NAME = "StatusMahasiswa"
//        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        sharedPref.edit().clear().commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.signout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_signout){
            postLogoutDosenApi()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun postLogoutDosenApi(){
        val progressDialog = ProgressDialog(this@DosenActivity)
        progressDialog.setMessage("Tunggu..")
        progressDialog.setCancelable(false)
        progressDialog.show()
        RetrofitClient.instance(this).postLogoutDosen().enqueue(object : Callback<LogoutDosenResponse> {
            override fun onResponse(
                call: Call<LogoutDosenResponse>,
                dosenResponse: Response<LogoutDosenResponse>
            ) {
                when(dosenResponse.code()){
                    200 -> {
                        progressDialog.dismiss()
                        val sessionManager = SessionManager(this@DosenActivity)
                        sessionManager.deleteAuthTokenIdUserAndStatusUser()
                        Intent(this@DosenActivity, MainActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(it)
                        }
                    }
                    else -> {
                        progressDialog.dismiss()
                        Toast.makeText(this@DosenActivity, "Ada yang tidak beres\n${dosenResponse.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LogoutDosenResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@DosenActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}