package com.jfkj.bililitebyxiayu.page.player.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.metrics.Event
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.FragmentVideoPlayerBinding
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.VideoResult
import com.jfkj.bililitebyxiayu.func.slip_down
import com.jfkj.bililitebyxiayu.func.slip_left_show
import com.jfkj.bililitebyxiayu.func.slip_right_close
import com.jfkj.bililitebyxiayu.func.slip_up
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class PlayerFragment(val bvid: String, val cid: String, val title: String, val preview: View) :
    Fragment() {
    lateinit var binding: FragmentVideoPlayerBinding
    lateinit var player: ExoPlayer

    //进度条单独Handel
    lateinit var ProgressHandler: Handler
    private var Time = 0//播放器播放剩余时间

    //状态判断
    private var player_status = true
    private var bottonbar_status = true

    private var isFullScreen = false
    private var isPad = false

    var Video_Width = 0
    var Video_Height = 0

//    constructor() : super()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_video_player, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        val data = mapOf(
            "bvid" to bvid,
            "cid" to cid,
            "platform" to "html5",
            "high_quality" to "1",
            "qn" to "80"
        )
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get("https://api.bilibili.com/x/player/wbi/playurl", data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: VideoResult = json.decodeFromString(it)
            requireActivity().runOnUiThread {
                binding.title.text = title
                player = ExoPlayer.Builder(context)
//            .setMediaSourceFactory(MediaSourceFactory.UNSUPPORTED)
                    .build()
                binding.player.player = player
                player.addMediaItem(MediaItem.fromUri(Map.data.durl[0].url))
                val lenth = Map.data.durl[0].length
                Time = (lenth / 1000).toInt()
                //准备播放
                player.prepare()
                //准备完成就开始播放
                player.playWhenReady = true
                player.addListener(
                    PlayerListener(
                        binding,
                        Video_Width,
                        Video_Height,
                        lenth,
                        player,
                        preview
                    )
                )
                binding.seekbar.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        // 进度改变时调用
                        // progress 是当前进度值
                        // fromUser 表示进度是否是用户拖动导致的
                        if (fromUser) player.seekTo(lenth / 100 * progress)

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // 开始拖动时调用
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // 停止拖动时调用
                    }
                })
            }

        })

        // 设置事件监听器
        var startX = 0f
        var startY = 0f
        var LongClick = false
        var Move = false
        val handler = Handler(Looper.getMainLooper())
        binding.player.setOnTouchListener { v, event ->
            val endX = event.x
            val endY = event.y
            val deltaX = endX - startX
            val deltaY = endY - startY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    handler.postDelayed({
                        // 判断是否触发了长按事件
                        requireActivity().runOnUiThread {
                            binding.seekto.visibility = ViewGroup.VISIBLE
                            binding.seektotext.text = "3倍速播放中"
                            val playbackParameters = PlaybackParameters(3f)
                            // 设置播放参数
                            player.playbackParameters = playbackParameters
                            LongClick = true
                        }
                    }, 500) // 设置长按判断的时间阈值为 500ms
                }

                MotionEvent.ACTION_MOVE -> {
                    if (Math.abs(deltaX) > 20 || Math.abs(deltaY) > 20) {
                        Move = true
                    }
                }

                MotionEvent.ACTION_UP -> {
                    // 手指抬起，根据需要执行其他操作
                    handler.removeCallbacksAndMessages(null)
                    if (LongClick) {
                        binding.seekto.visibility = ViewGroup.GONE
                        val playbackParameters = PlaybackParameters(1f)
                        // 设置播放参数
                        player.playbackParameters = playbackParameters
                    } else if (Move) {
                        val currentPosition = binding.player.player?.currentPosition ?: 0
                        player.seekTo(currentPosition + (deltaX / requireContext().resources.displayMetrics.widthPixels * player.duration).toLong())
                    } else {
                        v.performClick()
                    }
                    LongClick = false
                    Move = false
                }
            }
            true // 返回 true 表示已处理触摸事件
        }
//        ProgressHandler = Handler(Looper.getMainLooper())
//        while (Time--==0) {
//            ProgressHandler.postDelayed({
//                val currentPosition = binding.player.player?.currentPosition ?: 0
//                binding.progress.progress = (currentPosition * 100 / lenth).toInt()
//                binding.seekbar.progress = (currentPosition * 100 / lenth).toInt()
//                val CurrentTime=(currentPosition/1000).toInt()
//                val Lenth=(lenth/1000).toInt()
//                binding.progresstext.text="${CurrentTime/60}:${CurrentTime%60}/${Lenth/60}:${Lenth%60}"
//            }, 1000)
//        }

        //判断是否为平板模式
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density.toInt()
        if (screenWidthDp > 500) isPad = true

        //事件设置
        var lastClickTime = 0L
        binding.player.setOnClickListener {
            // 获取点击位置信息
//            val x = it.x
//            val y = it.y
            // 执行双击操作
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < 300) {
                // 触发双击事件
                check_player_status(!player_status)
            } else {
                // 触发单击事件
                check_bottonbar_status(!bottonbar_status)
            }
            lastClickTime = clickTime
        }
        binding.close.setOnClickListener {
            activity?.finish()
        }
        binding.pause.setOnClickListener {
            check_player_status(!player_status)
        }
        binding.fullscreen.setOnClickListener {
            if (isFullScreen) {
                FullScreen(false)
            } else {
                FullScreen(true)
            }

        }
        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (isFullScreen) {
                    FullScreen(false)
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
//        requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
//            // 在这里处理返回键点击事件
//
//            return@addCallback true
//        }

//        view.setOnKeyListener { _, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
//
//                true // 返回true表示事件被处理
//            } else false // 返回false表示事件未被处理，将会传递给其他可接受该事件的视图
//
//        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 在这里处理屏幕旋转后的逻辑
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isFullScreen = false
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFullScreen = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
        //实现完整共享元素动画
        preview.visibility = ViewGroup.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        check_player_status(false)
    }

    //其他事件处理
    private fun check_player_status(check: Boolean) {
        if (check) {
            player.play()
            binding.pause.setImageResource(R.drawable.pause)
        } else {
            player.pause()
            binding.pause.setImageResource(R.drawable.play)
        }
        player_status = check
    }

    private fun check_bottonbar_status(check: Boolean) {
        if (check) {
            slip_up(binding.bottombar, -40f, requireActivity())
            slip_down(binding.topbar, -40f, requireActivity())
        } else {
            slip_down(binding.bottombar, 0f, requireActivity())
            slip_up(binding.topbar, 0f, requireActivity())
        }
        bottonbar_status = check
    }

    private fun StatusBar(check: Boolean) {
        if (check) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {                    // 在 Android 11 以下版本，使用旧的方法隐藏状态栏
                val controller = requireActivity().window.insetsController
                // 隐藏状态栏
                controller?.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                requireActivity().window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = requireActivity().window.insetsController
                // 显示状态栏
                controller?.show(WindowInsetsCompat.Type.systemBars())
            } else {
                // 在 Android 11 以下版本，使用旧的方法隐藏状态栏
                requireActivity().window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }
    private fun FullScreen(check: Boolean) {
        // 获取父 Activity 对象
        val parentActivity = requireActivity()
        if (check) {
            if (isPad) {
                if (parentActivity is VideoActivity) {
                    val activityBinding =  parentActivity.binding
                    slip_right_close(activityBinding.ViewPager,requireContext())
                }
            } else {
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

            }
            isFullScreen = true
            StatusBar(true)
        } else {
            if (isPad) {
                if (parentActivity is VideoActivity) {
                    val activityBinding =  parentActivity.binding
                    slip_left_show(activityBinding.ViewPager,requireContext())
                }
            } else {
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)

            }
            isFullScreen = false
            StatusBar(false)
        }
    }


}

class PlayerListener(
    val binding: FragmentVideoPlayerBinding,
    var Video_Width: Int,
    var Video_Hegiht: Int,
    val lenth: Long,
    val player: Player,
    val preview: View,
) : Player.Listener {
    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        val currentPosition = binding.player.player?.currentPosition ?: 0
        Log.d("ExoBaseUserActivity onPlayWhenReadyChanged", currentPosition.toString())

    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        Video_Width = videoSize.width
        Video_Hegiht = videoSize.height
    }

    override fun onPlayerError(error: PlaybackException) {

    }

    @SuppressLint("ResourceType")
    override fun onPlaybackStateChanged(playbackState: Int) {
        // 播放进度发生改变时调用
        // 你可以在这里获取当前播放的位置等信息
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                // 处理缓冲开始事件
                binding.load.visibility = ViewGroup.VISIBLE
                Glide.with(binding.root)
                    .load(R.drawable.loading)
                    .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
                    .into(binding.load)
            }

            Player.STATE_READY -> {
                // 处理缓冲完成事件
                binding.load.visibility = ViewGroup.GONE
            }
        }
        //隐藏预览图
        if (player.isPlaying && preview.isVisible) preview.visibility = ViewGroup.GONE
    }

    override fun onEvents(player: Player, events: Player.Events) {
        val currentPosition = binding.player.player?.currentPosition ?: 0
        binding.progress.progress = (currentPosition * 100 / lenth).toInt()
        binding.seekbar.progress = (currentPosition * 100 / lenth).toInt()
        val CurrentTime = (currentPosition / 1000).toInt()
        val Lenth = (lenth / 1000).toInt()
        binding.progresstext.text =
            "${CurrentTime / 60}:${CurrentTime % 60}/${Lenth / 60}:${Lenth % 60}"
    }

    override fun onPlayWhenReadyChanged(
        playWhenReady: Boolean, reason: @Player.PlayWhenReadyChangeReason Int
    ) {
        //播放暂停会回调该方法，播放时playWhenReady为true
        Log.d("ExoBaseUserActivity onPlayWhenReadyChanged", "onVolumeChanged--volume=" + reason)
    }
}