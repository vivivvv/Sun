package com.app.mybase.views.video

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.R
import com.app.mybase.adapters.PlaybackIconsAdapter
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityVideoBinding
import com.app.mybase.helper.AppConstants.POSITION
import com.app.mybase.helper.AppConstants.VIDEO_LIST
import com.app.mybase.helper.Utils.parcelableArrayList
import com.app.mybase.model.IconModel
import com.app.mybase.model.Video
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import dagger.android.AndroidInjection
import javax.inject.Inject

class VideoActivity : BaseActivity(), View.OnClickListener {

    val TAG = this::class.java.name
    lateinit var binding: ActivityVideoBinding
    lateinit var viewmodel: VideoViewModel
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    lateinit var controlsLayout: ConstraintLayout
    lateinit var playbackIconsRV: RecyclerView
    lateinit var playbackIconsAdapter: PlaybackIconsAdapter
    lateinit var lockBtn: ImageView
    lateinit var unLockBtn: ImageView
    lateinit var backBtn: ImageView
    lateinit var videoTitle: TextView
    var iconModelList = ArrayList<IconModel>()
    var expand = false
    lateinit var nightMode: View
    var dark = false
    var mute = false
    var pictureInPicture: PictureInPictureParams.Builder? = null
    var isCrossChecked = false

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        viewmodel = ViewModelProvider(this, factory)[VideoViewModel::class.java]
        binding.videoViewModel = viewmodel
        binding.lifecycleOwner = this@VideoActivity
        // Hiding status bar
        hideStatusBarUI()
        // Initialize binding variables
        initializeBinding()
        // Initialize Player
        setupPlayer()
        // Update video data from HomePage
        initialVideoData()

    }

    private fun initializeBinding() {
        playerView = binding.exoplayerView
        controlsLayout = findViewById(R.id.controls_layout)
        lockBtn = findViewById(R.id.lock)
        lockBtn.setOnClickListener(this)
        unLockBtn = findViewById(R.id.unlock)
        unLockBtn.setOnClickListener(this)
        backBtn = findViewById(R.id.video_back)
        backBtn.setOnClickListener(this)
        nightMode = findViewById(R.id.night_mode)
        videoTitle = findViewById(R.id.video_title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPicture = PictureInPictureParams.Builder()
        }
        initializeDataToRV()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeDataToRV() {
        iconModelList.add(IconModel(R.drawable.ic_right, getString(R.string.more)))

        playbackIconsRV = findViewById(R.id.recyclerview_icon)
        playbackIconsRV.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        playbackIconsAdapter = PlaybackIconsAdapter(iconModelList, this@VideoActivity)
        playbackIconsRV.adapter = playbackIconsAdapter
        playbackIconsAdapter.notifyDataSetChanged()
        playbackIconsAdapter.setOnItemClickListener(object :
            PlaybackIconsAdapter.OnItemClickListener {
            @SuppressLint("Range")
            override fun onItemClick(position: Int) {
                if (position == 0) {
                    if (expand) {
                        iconModelList.clear()
                        iconModelList.add(IconModel(R.drawable.ic_right, getString(R.string.more)))
                        playbackIconsAdapter.notifyDataSetChanged()
                        expand = false
                    } else {
                        if (iconModelList.size == 1) {
                            iconModelList.add(
                                IconModel(
                                    R.drawable.ic_pip_mode,
                                    getString(R.string.popup)
                                )
                            )
                            iconModelList.add(
                                IconModel(
                                    R.drawable.ic_fit_screen,
                                    getString(R.string.scaling)
                                )
                            )
                            iconModelList.add(
                                IconModel(
                                    R.drawable.ic_night_mode,
                                    getString(R.string.night)
                                )
                            )
                            iconModelList.add(
                                IconModel(
                                    R.drawable.ic_volume_off,
                                    getString(R.string.mute)
                                )
                            )
                            iconModelList.add(
                                IconModel(
                                    R.drawable.ic_rotate,
                                    getString(R.string.rotate)
                                )
                            )
                        }
                        iconModelList[position] =
                            IconModel(R.drawable.ic_left, getString(R.string.less))
                        playbackIconsAdapter.notifyDataSetChanged()
                        expand = true
                    }
                }
                if (position == 1) {
                    // popup
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val aspectRatio = Rational(16, 9)
                        pictureInPicture!!.setAspectRatio(aspectRatio)
                        enterPictureInPictureMode(pictureInPicture!!.build())
                    }
                }
                if (position == 2) {
                    when (playerView.resizeMode) {
                        AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            player.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
                            iconModelList[position] =
                                IconModel(R.drawable.ic_zoom_out, getString(R.string.zoom))
                            playbackIconsAdapter.notifyDataSetChanged()

                        }
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            player.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
                            iconModelList[position] =
                                IconModel(R.drawable.ic_fit_screen, getString(R.string.fit))
                            playbackIconsAdapter.notifyDataSetChanged()
                        }
                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            player.videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
                            iconModelList[position] =
                                IconModel(R.drawable.ic_full_screen, getString(R.string.full))
                            playbackIconsAdapter.notifyDataSetChanged()
                        }
                        else -> {}
                    }
                }
                if (position == 3) {
                    // night mode
                    if (dark) {
                        nightMode.visibility = View.GONE
                        iconModelList[position] =
                            IconModel(R.drawable.ic_night_mode, getString(R.string.night))
                        playbackIconsAdapter.notifyDataSetChanged()
                        dark = false
                    } else {
                        nightMode.visibility = View.VISIBLE
                        iconModelList.set(
                            position,
                            IconModel(R.drawable.ic_night_mode, getString(R.string.day))
                        )
                        playbackIconsAdapter.notifyDataSetChanged()
                        dark = true
                    }
                }
                if (position == 4) {
                    //mute
                    if (mute) {
                        player.volume = 60f
                        iconModelList[position] =
                            IconModel(R.drawable.ic_volume_off, getString(R.string.mute))
                        playbackIconsAdapter.notifyDataSetChanged()
                        mute = false
                    } else {
                        player.volume = 0f
                        iconModelList[position] =
                            IconModel(R.drawable.ic_volume, getString(R.string.unMute))
                        playbackIconsAdapter.notifyDataSetChanged()
                        mute = true
                    }
                }
                if (position == 5) {
                    // rotate
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        playbackIconsAdapter.notifyDataSetChanged()
                    } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        playbackIconsAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
    }

    private fun initialVideoData() {
        viewmodel.position = intent?.getIntExtra(POSITION, 0) ?: 0
        val list = intent?.parcelableArrayList<Video>(VIDEO_LIST)
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
        player.seekTo(viewmodel.position, 0)
        player.playWhenReady = true
        // Update video title
        videoTitle.text = viewmodel.videoDataList[viewmodel.position].title
        videoTitle.isSelected = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isCrossChecked = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            playerView.hideController()
        } else {
            playerView.showController()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPause() {
        super.onPause()
        if (isInPictureInPictureMode) {
            player.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (isCrossChecked) {
            player.release()
            finishAndRemoveTask()
        } else {
            // Pause the player when activity no longer visible to the user
            player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Player no longer used
        player.release()
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.lock -> {
                lockBtn.visibility = View.INVISIBLE
                unLockBtn.visibility = View.VISIBLE
                controlsLayout.visibility = View.VISIBLE
            }
            R.id.unlock -> {
                lockBtn.visibility = View.VISIBLE
                unLockBtn.visibility = View.INVISIBLE
                controlsLayout.visibility = View.INVISIBLE
            }
            R.id.video_back -> {
                finish()
            }
            else -> {}
        }
    }

}