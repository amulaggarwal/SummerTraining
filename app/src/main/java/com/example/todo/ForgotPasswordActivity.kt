package com.example.todo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.todo.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var btn_sendResetLink: Button
    lateinit var ll_forgotPass:LinearLayout
    lateinit var ll_emailSent:LinearLayout
    lateinit var et_forgotPass:EditText
    lateinit var ll_rememberPass: LinearLayout
    private lateinit var auth: FirebaseAuth
    lateinit var btn_backtoLogin: Button
    lateinit var ll_resendLink:LinearLayout
    private var currentApiVersion by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupActionBar()
        btn_sendResetLink=findViewById(R.id.btn_sendResetLink)
        ll_forgotPass=findViewById(R.id.ll_forgotPass)
        ll_emailSent=findViewById(R.id.ll_emailSent)
        auth= FirebaseAuth.getInstance()
        et_forgotPass=findViewById(R.id.et_forgotpass)
        ll_rememberPass=findViewById(R.id.ll_remeberpass)
        btn_backtoLogin=findViewById(R.id.btn_backtoLogin)
        ll_resendLink=findViewById(R.id.ll_resendLink)
        ll_rememberPass.setOnClickListener {
            onBackPressed()
            finish()
        }
        btn_backtoLogin.setOnClickListener {
            onBackPressed()
            finish()
        }
        ll_resendLink.setOnClickListener {
            resetPassword()
            Toast.makeText(this,"Password reset link has been sent again.",Toast.LENGTH_SHORT).show()
        }
        btn_sendResetLink.setOnClickListener {
            resetPassword()

        }
        currentApiVersion = Build.VERSION.SDK_INT;
        val flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = flags
            val decorView = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    decorView.systemUiVisibility = flags
                }
            }
        }
    }

    private fun setupActionBar() {
        val toolbar_card_details_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_forgotPassword)
        setSupportActionBar(toolbar_card_details_activity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)

        }
        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }

    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }
    fun resetPassword(){
        val email=et_forgotPass.text.toString().trim()
        if(email.isEmpty()){
            Toast.makeText(this,"Enter your email to reset password",Toast.LENGTH_SHORT).show()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Enter a valid email",Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                ll_emailSent.visibility=View.VISIBLE
                ll_forgotPass.visibility=View.GONE
            }else{
                Toast.makeText(this,"Something went wrong please try again!",Toast.LENGTH_SHORT).show()
            }
        }


    }
}