package com.example.vocabulary.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.vocabulary.R
import com.example.vocabulary.Word
import com.example.vocabulary.auth.login.LoginActivity
import com.example.vocabulary.ui.ui.main.SectionsPagerAdapter
import com.example.vocabulary.utils.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    var counter = 0
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvtitle.text = "Vocabulary"
        setSupportActionBar(toolbarcom)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//        toast(checklist.size.toString())
        act = this
        list.clear()
        if (checklist.isNullOrEmpty()) {
            list.add(
                Word(
                    "Ambiguous",
                    "/amˈbɪɡjʊəs/",
                    "adjective",
                    "• open to more than one interpretation; not having one obvious meaning.\nExample: \"ambiguous phrases\"\n\n• not clear or decided.\nExample: \"the election result was ambiguous\""
                )
            )
            list.add(
                Word(
                    "Literally",
                    "/ˈlɪt(ə)rəli/",
                    "adverb",
                    "• in a literal manner or sense; exactly.\nExample: \"the driver took it literally when asked to go straight over the roundabout\"\n\n• used for emphasis while not being literally true.\n\nExample:\"I was literally blown away by the response I got\""
                )
            )
            list.add(
                Word(
                    "Building",
                    "/ˈbɪldɪŋ/",
                    "noun",
                    "• a structure with a roof and walls, such as a house or factory..\n\n• the action or trade of constructing something.\nExample: \"the building of motorways\""
                )
            )
            list.add(
                Word(
                    "Colonel",
                    "/ˈkəːn(ə)l/",
                    "noun",
                    "• a rank of officer in the army and in the US air force, above a lieutenant colonel and below a brigadier or brigadier general.\n\n• short for lieutenant colonel."
                )
            )
            list.add(
                Word(
                    "Abscond",
                    "/əbˈskɒnd,abˈskɒnd/",
                    "verb",
                    "• leave hurriedly and secretly, typically to escape from custody or avoid arrest.\nExample:\"the barman absconded with a week's takings\""
                )
            )
            list.add(
                Word(
                    "Cognizant",
                    "/ˈkɒ(ɡ)nɪz(ə)nt/",
                    "adjective",
                    "• having knowledge or awareness.\nExample: \"statesmen must be cognizant of the political boundaries within which they work\""
                )
            )
            list.add(
                Word(
                    "Despot",
                    "/ˈdɛspɒt/",
                    "adjective",
                    "• a ruler or other person who holds absolute power, typically one who exercises it in a cruel or oppressive way."
                )
            )
            list.add(
                Word(
                    "Emend",
                    "/ɪˈmɛnd/",
                    "verb",
                    "• make corrections and revisions to (a text).\nExample: \"these studies show him collating manuscripts and emending texts\"\n\n• alter (something that is incorrect).\nExample:\"the year of his death might need to be emended to 652\""
                )
            )
            list.add(
                Word(
                    "Fatuous",
                    "/ˈfatjʊəs/",
                    "adjective",
                    "• silly and pointless.\nExample: \"a fatuous comment\""
                )
            )
            list.add(
                Word(
                    "Hapless",
                    "/ˈhapləs/",
                    "adjective",
                    "• (especially of a person) unfortunate.\nExample: \"the hapless victims of the disaster\""
                )
            )
        } else
            list.addAll(checklist)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        if (viewPager.currentItem == list.size - 1)
            right.visibility = View.GONE

        right.setOnClickListener {
            if (viewPager.currentItem < 10)
                viewPager.currentItem = viewPager.currentItem + 1
        }
        left.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem = viewPager.currentItem - 1
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    left.visibility = View.GONE
                    right.visibility = View.VISIBLE
                } else if (position == list.size - 1) {
                    left.visibility = View.VISIBLE
                    right.visibility = View.GONE
                } else {
                    left.visibility = View.VISIBLE
                    right.visibility = View.VISIBLE
                }
            }

        })
        checklist.clear()
    }

    companion object {
        val list = arrayListOf<Word>()
        val checklist = arrayListOf<Word>()
        lateinit var act: Activity
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        val build = MaterialAlertDialogBuilder(this)
            .setTitle("Logout?")
            .setPositiveButton("YES") { dialog, _ ->
                FirebaseAuth.getInstance().signOut()
                mGoogleSignInClient.signOut()
                dialog.dismiss()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }.create()

        build.show()
    }
}