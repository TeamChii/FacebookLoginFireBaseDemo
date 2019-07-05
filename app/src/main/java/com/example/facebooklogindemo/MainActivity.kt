package com.example.facebooklogindemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import java.util.*
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.provider.SyncStateContract.Helpers.update
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.os.Debug
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var provider : List<AuthUI.IdpConfig>
    val MY_REQUEST_CODE: Int = 7117

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        provider =arrayListOf(

            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
       showSigInOptions()


        buttonSignout.setOnClickListener {
            AuthUI.getInstance().signOut(this@MainActivity)
                .addOnCompleteListener {
                    buttonSignout.isEnabled = false
                    showSigInOptions()
                }
                .addOnFailureListener{
                        e -> Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "" + user!!.email, Toast.LENGTH_SHORT).show()
                buttonSignout.isEnabled = true

            }

            else {
                Toast.makeText(this,""+response!!.error!!.message,Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun showSigInOptions(){


        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(provider)
            .setTheme(R.style.MyTheme)
            .build(),MY_REQUEST_CODE
        )

    }

    private fun haskey(){
        try {
            val info = packageManager.getPackageInfo(
                "com.example.facebooklogindemo",
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }

    }
}
