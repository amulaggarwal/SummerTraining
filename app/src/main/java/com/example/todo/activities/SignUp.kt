package com.example.todo.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.example.todo.R
import com.example.todo.firebase.firestoreClass
import com.example.todo.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.properties.Delegates

class SignUp : AppCompatActivity() {
    private var googleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN=123
    lateinit var btn_googlesignup: ImageButton
    private var currentApiVersion by Delegates.notNull<Int>()
    private lateinit var auth: FirebaseAuth
    lateinit var et_firstname: EditText
    lateinit var et_lastname: EditText
    lateinit var et_emailSignUp: EditText
    lateinit var et_passwordSignup: EditText
    lateinit var btn_signUp: Button
    lateinit var ll_signin: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        currentApiVersion = Build.VERSION.SDK_INT
        btn_googlesignup=findViewById(R.id.btn_googlesignup)
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
        et_firstname=findViewById(R.id.et_firstname)
        et_lastname=findViewById(R.id.et_lastname)
        et_emailSignUp=findViewById(R.id.et_emailsignup)
        et_passwordSignup=findViewById(R.id.et_passwordsignup)
        btn_signUp=findViewById(R.id.btn_signUp)
        ll_signin=findViewById(R.id.ll_signin)
        auth= FirebaseAuth.getInstance()
        createRequest()
        btn_googlesignup.setOnClickListener {
            signIn()
        }
        btn_signUp.setOnClickListener {
            registerUser()
        }
        ll_signin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
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
                Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
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
                    val user1=User(user.uid,user.displayName,user.email)
                    firestoreClass().registerUser(this,user1)
                    val intent= Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this,"Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser!=null){
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun registerUser(){
        val firstName=et_firstname.text.toString()
        val lastName=et_lastname.text.toString()
        val email=et_emailSignUp.text.toString()
        val password=et_passwordSignup.text.toString()

        if(validateForm(firstName,lastName, email, password)){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registered_email = firebaseUser.email!!
                        val name= "$firstName $lastName"

                        val user=User(firebaseUser.uid,name,registered_email)
                        firestoreClass().registerUser(this,user)


                    } else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    private fun validateForm(firstName: String,lastName: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(firstName) -> {
                Toast.makeText(this,"Enter your first name",Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(lastName) -> {
                Toast.makeText(this,"Enter your last name",Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this,"Enter your email",Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this,"Enter your password",Toast.LENGTH_SHORT).show()
                false
            }
            password.length<8->{
                Toast.makeText(this,"Password must alteast be of 8 digits",Toast.LENGTH_SHORT).show()
                false
            }

            else->true
        }
    }
    fun userRegisteredSuccess(){
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

}