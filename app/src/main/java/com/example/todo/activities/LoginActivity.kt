package com.example.todo.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.todo.ForgotPasswordActivity
import com.example.todo.R
import com.example.todo.firebase.firestoreClass
import com.example.todo.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {
    private var googleSignInClient: GoogleSignInClient? = null
    private var currentApiVersion by Delegates.notNull<Int>()
    lateinit var et_password: EditText
    lateinit var btn_login: Button
    lateinit var et_email: EditText
    private lateinit var auth: FirebaseAuth
    lateinit var ll_signup: LinearLayout
    private val RC_SIGN_IN=123
    lateinit var btn_googlesignin: ImageButton
    lateinit var tv_forgotPassword: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        currentApiVersion = Build.VERSION.SDK_INT;
        et_password=findViewById(R.id.et_password)
        btn_login=findViewById(R.id.btn_login)
        et_email=findViewById(R.id.et_email)
        ll_signup=findViewById(R.id.ll_signup)
        btn_googlesignin=findViewById(R.id.btn_googleSignIn)
        tv_forgotPassword=findViewById(R.id.tv_forgotPassword)
        auth= FirebaseAuth.getInstance()
        createRequest()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        findViewById<ImageView>(R.id.iv_login).setOnClickListener {
            startActivity(Intent(this,EmailVerificationActivity::class.java))

        }

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
        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password= et_password.text.toString()
            if(validateForm(email,password)){
                signInRegisteredUser(email,password)
            }

        }
        ll_signup.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }


        btn_googlesignin.setOnClickListener {
            signIn()
        }
        tv_forgotPassword.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }




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

    fun createRequest(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        val user1= User(user.uid,user.displayName,user.email)
                        firestoreClass().registerUser(this,user1)


                    } else {
                      Toast.makeText(this,"Auth Failed",Toast.LENGTH_SHORT).show()
                    }
                }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser!=null){
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun signInRegisteredUser(email: String,password:String){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {

            TextUtils.isEmpty(email) -> {
                Toast.makeText(this,"Enter your email",Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show()
                false
            }

            else->true
        }
    }
    fun userLoginSuccessfull() {
        Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show()
        val intent=Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}