package com.example.todo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.todo.R
import com.example.todo.firebase.firestoreClass
import com.example.todo.model.User
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var btn_log_out: Button
    lateinit var tv_name: TextView
    lateinit var tv_mail: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_mail=findViewById(R.id.tv_mail)
        tv_name=findViewById(R.id.tv_name)
        btn_log_out=findViewById(R.id.btn_logout)
        firestoreClass().signInUser(this)


        btn_log_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
    fun updateUserDetails(user: User){
        tv_name.text=user.name
        Log.e("Hello","$user.name")
        tv_mail.text=user.email
    }
}