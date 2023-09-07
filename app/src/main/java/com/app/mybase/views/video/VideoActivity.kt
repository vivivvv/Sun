package com.app.mybase.views.video

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.mybase.R
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityVideoBinding
import com.app.mybase.helper.AppConstants
import com.app.mybase.helper.AppConstants.MEDIA_ITEM
import com.app.mybase.helper.AppConstants.POSITION
import com.app.mybase.helper.AppConstants.SEEK_TIME
import com.app.mybase.helper.Utils.parcelableArrayList
import com.app.mybase.model.Video
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import dagger.android.AndroidInjection
import javax.inject.Inject

class VideoActivity : BaseActivity(), Player.Listener {

    val TAG = this::class.java.name
    lateinit var binding: ActivityVideoBinding
    lateinit var viewmodel: VideoViewModel
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        viewmodel = ViewModelProvider(this, factory)[VideoViewModel::class.java]
        binding.videoViewModel = viewmodel
        binding.lifecycleOwner = this@VideoActivity

        // Initialize Player
        setupPlayer()
        // Update video data from HomePage
        initialVideoData()
        // Play selected video
        updateData(savedInstanceState)

    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView = binding.videoView
        progressBar = binding.progressBar
        playerView.player = player
        player.addListener(this)
    }

    // handle loading
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {
            }
            Player.STATE_IDLE -> {
            }
        }
    }

    // save details if Activity is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // current play position
        outState.putLong(SEEK_TIME, player.currentPosition)
        // current mediaItem
        outState.putInt(MEDIA_ITEM, player.currentMediaItemIndex)
    }

    private fun updateData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val seekTime = savedInstanceState.getLong(SEEK_TIME)
            val restoredMediaItem = savedInstanceState.getInt(MEDIA_ITEM)
            player.seekTo(restoredMediaItem, seekTime)
        } else {
            player.seekTo(viewmodel.position, 0)
        }
        player.playWhenReady = true
    }

    private fun initialVideoData() {
        viewmodel.position = intent?.getIntExtra(POSITION, 0) ?: 0
        val list = intent?.parcelableArrayList<Video>(AppConstants.VIDEO_LIST)
        viewmodel.videoDataList.clear()
        viewmodel.videoDataList.addAll(list ?: ArrayList())
        addVideoFiles()
    }

    private fun addVideoFiles() {
        val newItems = ArrayList<MediaItem>()
        viewmodel.videoDataList.forEach {
            newItems.add(MediaItem.fromUri(it.sources[0]))
        }
        player.addMediaItems(newItems)
        player.prepare()
    }

    override fun onStop() {
        super.onStop()
        // Pause the player when activity no longer visible to the user
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Player no longer used
        player.release()
    }

}