package com.example.todo.firebase

import android.app.Activity
import android.util.Log
import com.example.todo.activities.LoginActivity
import com.example.todo.activities.MainActivity
import com.example.todo.activities.SignUp
import com.example.todo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class firestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: Activity,userInfo: User){
        mFirestore.collection("Users")
                .document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                    when (activity){
                        is SignUp->{
                            activity.userRegisteredSuccess()
                        }
                        is LoginActivity->{
                            activity.userLoginSuccessfull()
                        }
                    }
                }.addOnFailureListener {
                Log.e(activity.javaClass.simpleName,"error writing document")
                }

    }

    fun signInUser(activity: Activity){
        mFirestore.collection("Users")
                .document(getCurrentUserId())
                .get()
                .addOnSuccessListener {
                   document->
                    val loggedInUser=document.toObject(User::class.java)
                    when(activity){
                        is LoginActivity->{
                            if (loggedInUser != null) {
                                activity.userLoginSuccessfull()
                            }
                        }
                        is MainActivity->{
                            if (loggedInUser != null) {
                                activity.updateUserDetails(loggedInUser)
                            }
                        }
                    }
                }.addOnFailureListener {
                    Log.e(activity.javaClass.simpleName,"error writing document")
                }
    }

    fun getCurrentUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}