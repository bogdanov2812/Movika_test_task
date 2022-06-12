package com.bogdanov.movika

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.bogdanov.movika.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var progressJob: Job? = null
    private var movementJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: MainViewModel by viewModels()

        binding.button.setOnClickListener {
            binding.video.stopPlayback()
            viewModel.clickedButton()
        }

        binding.tryAgain.setOnClickListener {
            startVideo(viewModel.currentVideo.value?: R.raw.first_video)
        }

        binding.video.setOnCompletionListener {
            binding.tryAgain.visibility = View.VISIBLE
            binding.button.visibility = View.INVISIBLE
            setProgressBar(0)
        }


        binding.video.setOnPreparedListener {
            it.start()
            progressJob?.cancel()
            progressJob=lifecycleScope.launch(Dispatchers.Default) {
                try{
                    while (it.currentPosition < it.duration){
                        delay(15)
                        val progress = 100 - it.currentPosition / durationToProgress(it.duration)
                        runOnUiThread {
                            setProgressBar(progress)
                        }
                    }
                }catch (e:Exception){
                    Log.d("progressJob","exception = $e")
                }
            }

            movementJob?.cancel()
            movementJob = lifecycleScope.launch(Dispatchers.Default) {
                try {
                    while (it.currentPosition < it.duration){
                        delay(1500)
                        val startX = -binding.field.width/2 + binding.button.width
                        val startY = -binding.field.height/2 + binding.button.height
                        runOnUiThread{
                            ViewCompat.animate(binding.button)
                                .translationX(Random.nextInt(startX..-startX).toFloat())
                                .translationY(Random.nextInt(startY..-startY).toFloat())
                                .duration = 300
                        }

                    }
                }catch (e: Exception){
                    Log.d("movementJob","exception = $e")
                }

            }


        }

        viewModel.currentVideo.observe(this){
            startVideo(it)
        }

    }
    private fun durationToProgress(duration: Int) = duration / 100


    private fun startVideo(video: Int){
        binding.button.x = (binding.field.width/2 - binding.button.width).toFloat()
        binding.button.y = (binding.field.height/2 - binding.button.height).toFloat()
        binding.video.setVideoURI(Uri.parse("android.resource://$packageName/$video"))
        binding.video.start()
        binding.tryAgain.visibility = View.INVISIBLE
        binding.button.visibility = View.VISIBLE
    }

    private fun setProgressBar(progress: Int){
        binding.mainLeftPb.progress = progress
        binding.mainRightPb.progress = progress
        binding.secondaryLeftPb.progress = progress
        binding.secondaryRightPb.progress = progress
        binding.rightPercent.text = (100-progress).toString()
        binding.leftPercent.text = (100-progress).toString()
    }

}