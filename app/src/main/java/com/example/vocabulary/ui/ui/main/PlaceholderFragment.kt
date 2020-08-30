package com.example.vocabulary.ui.ui.main

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import com.example.vocabulary.R
import com.example.vocabulary.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_placeholder.*

class PlaceholderFragment(private val position: Int) : Fragment() {

    val sounds =
        arrayListOf(R.raw.ambiguous, R.raw.literally, R.raw.building, R.raw.colonel, R.raw.abscond, R.raw.cognizant, R.raw.despot, R.raw.emend, R.raw.fatuous,R.raw.hapless)
    lateinit var player: MediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_placeholder, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (MainActivity.checklist.contains(MainActivity.list[position])){
            constraint.setBackgroundColor(getColor(resources, R.color.cardselectbg, null))
            cardView.outlineAmbientShadowColor =
                getColor(resources, R.color.cardshadowselect, null)
            cardView.outlineSpotShadowColor =
                getColor(resources, R.color.cardshadowselect, null)
            cardView.strokeColor = getColor(resources,R.color.lightyellow,null)
            review.setTextColor(Color.parseColor("#B78A00"))
            review.text = "Click to Remove from Review"
        }

        cardView.setOnClickListener {
            if (MainActivity.checklist.contains(MainActivity.list[position])) {
                constraint.setBackgroundColor(getColor(resources, R.color.cardbg, null))
                review.setTextColor(
                    getColor(
                        resources,
                        R.color.design_default_color_primary_variant,
                        null
                    )
                )
                cardView.outlineAmbientShadowColor = getColor(resources, R.color.darkpurple, null)
                cardView.outlineSpotShadowColor = getColor(resources, R.color.darkpurple, null)
                cardView.strokeColor = getColor(resources,R.color.purple_200,null)
                review.text = "Click to Mark for Review"
                MainActivity.checklist.remove(MainActivity.list[position])
            } else {
                constraint.setBackgroundColor(getColor(resources, R.color.cardselectbg, null))
                cardView.outlineAmbientShadowColor =
                    getColor(resources, R.color.cardshadowselect, null)
                cardView.outlineSpotShadowColor =
                    getColor(resources, R.color.cardshadowselect, null)
                cardView.strokeColor = getColor(resources,R.color.lightyellow,null)
                review.setTextColor(Color.parseColor("#B78A00"))
                review.text = "Click to Remove from Review"
                MainActivity.checklist.add(MainActivity.list[position])
            }
        }

        if (position == MainActivity.list.size - 1)
            right.visibility = View.VISIBLE

        right.setOnClickListener {
            if (MainActivity.checklist.size == 0) {
                val alertDialog = context?.let { it1 ->
                    MaterialAlertDialogBuilder(it1)
                        .setMessage("Words Learned:${MainActivity.list.size - MainActivity.checklist.size}\nAll Words Learned! Congratulations")
                        .setPositiveButton("Exit") { dialog, _ ->
                            dialog.dismiss()
                            MainActivity.act.finish()
                        }
                }?.create()
                alertDialog?.show()
            } else {
                val alertDialog = context?.let { it1 ->
                    MaterialAlertDialogBuilder(it1)
                        .setMessage("Words Learned:${MainActivity.list.size - MainActivity.checklist.size} , Saved for review :${MainActivity.checklist.size}")
                        .setPositiveButton("Review") { dialog, _ ->
                            dialog.dismiss()
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }.setNegativeButton("Exit") { dialog, _ ->
                            dialog.dismiss()
                            MainActivity.act.finish()
                        }
                }?.create()

                alertDialog?.show()
            }
        }

        word.text = MainActivity.list[position].word
        pronounce.text = MainActivity.list[position].pronounce
        meaning.text = MainActivity.list[position].meaning
        textView3.text = MainActivity.list[position].type
        pronounce.setOnClickListener {
            if (position >= sounds.size)
                Toast.makeText(context, "Pronunciations will be added soon", Toast.LENGTH_SHORT)
                    .show()
            else {
                player = MediaPlayer.create(context, sounds[position])
                player.start()
            }
        }
    }
}