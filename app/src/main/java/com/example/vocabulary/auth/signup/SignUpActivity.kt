package com.example.vocabulary.auth.signup

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.vocabulary.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        register.setOnClickListener {
            val inputManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (currentFocus != null) inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
            registerUser()
        }
        textname.editText?.doAfterTextChanged { textname.error = null }
        textemail.editText?.doAfterTextChanged { textemail.error = null }
        textpass.editText?.doAfterTextChanged { textpass.error = null }
    }

    private fun registerUser() {
        val Strpassword: String = textpass.editText?.text.toString()
        val Strname: String = textname.editText?.text.toString()
        val Stremail: String = textemail.editText?.text.toString()

        if (Strname.isEmpty()) {
            textname.error = "Field cannot be empty"
            return
        }
        if (Strpassword.isEmpty()) {
            textpass.error = "Field cannot be empty"
            return
        }
        if (Stremail.isEmpty()) {
            textemail.error = "Field cannot be empty"
            return
        }

        mAuth = FirebaseAuth.getInstance()
        if (isEmailValid(Stremail)) {
            mAuth.fetchSignInMethodsForEmail(Stremail).addOnCompleteListener {
                val b: Boolean = it.result?.signInMethods?.isNotEmpty()!!
                if (b) {
                    Toast.makeText(
                        applicationContext,
                        "Email already Exist! or Verify your email if already registered!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mAuth.createUserWithEmailAndPassword(Stremail, Strpassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                    Toast.makeText(
                                        this,
                                        "Registered! Email Verification sent",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else
                                Toast.makeText(
                                    this, task.exception!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            finish()
                        }
                }
            }
        } else
            Toast.makeText(applicationContext, "Enter a Valid Email Id", Toast.LENGTH_LONG)
                .show()
    }


    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern =
            Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}