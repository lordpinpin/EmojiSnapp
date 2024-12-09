package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityWaitBinding


class WaitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWaitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rotatingImage = binding.waitTx

        // Create an ObjectAnimator to rotate the view
        val rotateAnimation = ObjectAnimator.ofFloat(rotatingImage, "rotation", 0f, 360f)
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 7000 // 2 seconds for a full rotation
        rotateAnimation.repeatCount = ObjectAnimator.INFINITE // Keep rotating
        rotateAnimation.repeatMode = ObjectAnimator.RESTART // Restart after each cycle
        rotateAnimation.start()

        tryFind()

        binding.botBtn.setOnClickListener {
            Matchmaker.removeMatchmakingEntry()
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("IS_BOT_GAME", true)
            startActivity(intent)
            finish()
        }

        binding.returnBtn.setOnClickListener {
            Matchmaker.removeMatchmakingEntry()
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Matchmaker.removeMatchmakingEntry()
                finish()
            }
        })

    }

    var isActive = true;
    override fun onStop() {
        super.onStop()
        isActive = false

    }


    override fun onDestroy() {
        super.onDestroy()
    }

    fun tryFind() {
        Matchmaker.getMatch({ uid ->
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("IS_BOT_GAME", false)
            startActivity(intent)
        }, /* error */ {
            val handler = Handler()
            Log.e("WaitActivity", "Retrying after 5000ms")
            handler.postDelayed({
                if(isActive) {
                    tryFind()
                }
            }, 5000)
        })
    }
}