package com.example.vocabulary.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.vocabulary.R
import com.example.vocabulary.auth.signup.SignUpActivity
import com.example.vocabulary.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    var RC_SIGN_IN = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSign.setOnClickListener {
            val signinintent = mGoogleSignInClient.getSignInIntent()
            startActivityForResult(signinintent, RC_SIGN_IN)
        }

        password.editText?.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE)
                log_in()
            return@setOnEditorActionListener false
        }

        login.setOnClickListener {
            log_in()
        }

        newUser.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        email.editText?.doAfterTextChanged { email.error = null }
        password.editText?.doAfterTextChanged { password.error = null }

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Toast.makeText(applicationContext, user!!.displayName, Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(
                        TAG,
                        "signInWithCredential:failure",
                        it.exception
                    )
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Authentication Failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun log_in() {
        try {
            val inputManager =
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)!!
            inputManager.hideSoftInputFromWindow(
                Objects.requireNonNull(currentFocus)?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (ignored: Exception) {
        }
        login.isEnabled = false
        newUser.isEnabled = false
        MainProgressBar.visibility = View.VISIBLE

        val Stremail = email.editText?.text.toString()
        val Strpass = password.editText?.text.toString()

        if (Stremail.isEmpty()) {
            email.error = "Field cannot be empty"
            login.isEnabled = true
            newUser.isEnabled = true
            return
        }
        if (Strpass.isEmpty()) {
            password.error = "Field cannot be empty"
            login.isEnabled = true
            newUser.isEnabled = true
            return
        }

        firebaseAuth.signInWithEmailAndPassword(Stremail, Strpass).addOnCompleteListener {
            if (!it.isSuccessful) {
                firebaseAuth.fetchSignInMethodsForEmail(Stremail).addOnCompleteListener { task ->
                    val b: Boolean = task.result?.signInMethods?.isNotEmpty()!!

                    if (b) {
                        Toast.makeText(
                            applicationContext,
                            "Incorrect Password!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "User not Registered!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    login.isEnabled = true
                    newUser.isEnabled = true
                    MainProgressBar.visibility = View.GONE
                }
            } else {
                if (firebaseAuth.currentUser?.isEmailVerified!!) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Email Not Verified! Please verify before continuing",
                        Toast.LENGTH_SHORT
                    ).show()
                    login.isEnabled = true
                    newUser.isEnabled = true
                    MainProgressBar.visibility = View.GONE
                }
            }
        }

    }

    fun forgotPassword (view: View) {
        val Stremail = email.editText?.text.toString()
        if (Stremail.isEmpty()) {
            email.error = "Enter a valid Email id"
            return
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(Stremail).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Password Reset Email sent. Check Inbox",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Email not registered!", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account =
                    task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null && firebaseUser.isEmailVerified) {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }
    }

}