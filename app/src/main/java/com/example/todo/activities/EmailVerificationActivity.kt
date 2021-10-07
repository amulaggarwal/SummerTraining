package com.example.todo.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.todo.R


class EmailVerificationActivity : AppCompatActivity() {
    lateinit var otpeditbox1: EditText
    lateinit var otpeditbox2: EditText
    lateinit var otpeditbox3: EditText
    lateinit var otpeditbox4: EditText
    lateinit var btn_verify: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_email_verification)
        otpeditbox1 = findViewById(R.id.otp_edit_box1)
        otpeditbox2 = findViewById(R.id.otp_edit_box2)
        otpeditbox3 = findViewById(R.id.otp_edit_box3)
        otpeditbox4 = findViewById(R.id.otp_edit_box4)
        btn_verify=findViewById(R.id.btn_verify)
        btn_verify.setOnClickListener {
            startActivity(Intent(this, VerifyCompleteActivity::class.java))
            finish()
        }
        otpOnFocusChangeListener()
        otpOnTextChangeListener()




    }

    private fun otpOnTextChangeListener() {
        otpeditbox1.addTextChangedListener(GenericTextWatcher(otpeditbox1, otpeditbox2))
        otpeditbox2.addTextChangedListener(GenericTextWatcher(otpeditbox2, otpeditbox3))
        otpeditbox3.addTextChangedListener(GenericTextWatcher(otpeditbox3, otpeditbox4))
        otpeditbox4.addTextChangedListener(GenericTextWatcher(otpeditbox4, null))


        otpeditbox1.setOnKeyListener(GenericKeyEvent(otpeditbox1, null))
        otpeditbox2.setOnKeyListener(GenericKeyEvent(otpeditbox2, otpeditbox1))
        otpeditbox3.setOnKeyListener(GenericKeyEvent(otpeditbox3, otpeditbox2))
        otpeditbox4.setOnKeyListener(GenericKeyEvent(otpeditbox4, otpeditbox3))
    }

    private fun otpOnFocusChangeListener() {
        otpeditbox1.setOnFocusChangeListener { v, hasFocus ->
            when(hasFocus){
                true->otpeditbox1.setBackgroundResource(R.drawable.shadow_withstroke)
                else->otpeditbox1.setBackgroundResource(R.drawable.shadow)
            }
        }
        otpeditbox2.setOnFocusChangeListener { v, hasFocus ->
            when(hasFocus){
                true->otpeditbox2.setBackgroundResource(R.drawable.shadow_withstroke)
                else->otpeditbox2.setBackgroundResource(R.drawable.shadow)
            }
        }
        otpeditbox3.setOnFocusChangeListener { v, hasFocus ->
            when(hasFocus){
                true->otpeditbox3.setBackgroundResource(R.drawable.shadow_withstroke)
                else->otpeditbox3.setBackgroundResource(R.drawable.shadow)
            }
        }
        otpeditbox4.setOnFocusChangeListener { v, hasFocus ->
            when(hasFocus){
                true->otpeditbox4.setBackgroundResource(R.drawable.shadow_withstroke)
                else->otpeditbox4.setBackgroundResource(R.drawable.shadow)
            }
        }
    }


    class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.otp_edit_box1 && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }


    }



    class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) : TextWatcher {
        fun View.hideKeyboard() {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.otp_edit_box1 ->{
                    if (text.length == 1){
                        nextView!!.requestFocus()
                    }
                }
                R.id.otp_edit_box2 -> if (text.length == 1){
                    nextView!!.requestFocus()
                }
                R.id.otp_edit_box3 -> if (text.length == 1){
                    nextView!!.requestFocus()
                }
                R.id.otp_edit_box4 -> {
                    if (text.length == 1){
                        currentView!!.clearFocus()
                        currentView!!.hideKeyboard()

                    }

                }
            }
        }

        override fun beforeTextChanged(
                arg0: CharSequence,
                arg1: Int,
                arg2: Int,
                arg3: Int
        ) { // TODO Auto-generated method stub
        }

        override fun onTextChanged(
                arg0: CharSequence,
                arg1: Int,
                arg2: Int,
                arg3: Int
        ) { // TODO Auto-generated method stub
        }


    }






}

