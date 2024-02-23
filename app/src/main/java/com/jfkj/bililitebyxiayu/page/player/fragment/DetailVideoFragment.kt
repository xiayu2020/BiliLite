package com.jfkj.bililitebyxiayu.page.player.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jfkj.bililitebyxiayu.R
import com.jfkj.bililitebyxiayu.databinding.FragmentVideoDetailBinding
import com.jfkj.bililitebyxiayu.databinding.PopVideoThrowcoinBinding
import com.jfkj.bililitebyxiayu.func.Https
import com.jfkj.bililitebyxiayu.func.VideoDetailResult
import com.jfkj.bililitebyxiayu.func.VideoIsCoin
import com.jfkj.bililitebyxiayu.func.VideoIsGood
import com.jfkj.bililitebyxiayu.func.VideoIsStar
import com.jfkj.bililitebyxiayu.func.VideoRelatedResult
import com.jfkj.bililitebyxiayu.func.slip_down
import com.jfkj.bililitebyxiayu.func.user_agent
import com.jfkj.bililitebyxiayu.page.player.VideoActivity
import com.jfkj.bililitebyxiayu.page.player.adpater.UserVideoAdpater.RelatedVideoAdpater
import com.jfkj.bililitebyxiayu.page.user.UserActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DetailVideoFragment() : Fragment() {
    lateinit var Map: VideoDetailResult
    lateinit var binding: FragmentVideoDetailBinding
    private var VideoGooded = false
    private var VideoCoined = false
    private var VideoStared = false

    lateinit var aid: String
    lateinit var bvid: String

    constructor(aid: String, bvid: String) : this() {
        this.aid = aid
        this.bvid = bvid

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_video_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //加载视频详情
        DetailVideo()
        //加载推荐视频
        RelatedVideo()
        //加载是否点赞等
        isStarVideo()
        isCoinVideo()
        isLikeVideo()
        //
        binding.user.setOnClickListener {
            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                binding.usercard,
                "user_card_transition",
            )
            val intent = Intent(requireContext(), UserActivity::class.java)
            intent.putExtra("mid", Map.data.owner.mid.toString())
            startActivity(intent, options.toBundle())
        }
        binding.like.setOnClickListener {
            LikeVideo()
        }
        binding.coin.setOnClickListener {
            diaglog_coin()
        }
        binding.star.setOnClickListener {
            StarVideo()
        }
        binding.share.setOnClickListener {
            val url = Map.data.title + "/n" + "https://www.bilibili.com/video/" // 要分享的链接
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, url)

            startActivity(Intent.createChooser(shareIntent, "分享链接"))
        }
    }

    private fun DetailVideo() {
        val url = "https://api.bilibili.com/x/web-interface/view"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "aid" to aid,
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            Map = json.decodeFromString(it)
            requireActivity().runOnUiThread {
                binding.title.text = Map.data.title
                binding.detail.text = Map.data.tname
                binding.name.text = Map.data.owner.name
                binding.likenumber.text = Map.data.stat.like.toString()
                binding.coinnumber.text = Map.data.stat.coin.toString()
                binding.starnumber.text = Map.data.stat.favorite.toString()
                binding.sharenumber.text = Map.data.stat.share.toString()
                //up头像
                Glide.with(binding.root)
                    .load(Map.data.owner.face)
                    .transition(DrawableTransitionOptions.withCrossFade()) // 添加淡入淡出效果
                    .into(binding.face)
            }
        })
    }

    private fun RelatedVideo() {
        val url = "https://api.bilibili.com/x/web-interface/archive/related"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val data = mapOf(
            "aid" to aid,
        )
        val okhttp = Https()
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: VideoRelatedResult = json.decodeFromString(it)
            requireActivity().runOnUiThread {
                val adpater = RelatedVideoAdpater(Map.data, callback = { position, view ->
                    val options = ActivityOptionsCompat.makeCustomAnimation(requireContext(),R.animator.enter_animation ,R.animator.exit_animation)
                    val intent = Intent(requireContext(), VideoActivity::class.java)
                    intent.putExtra("aid", Map.data[position].aid.toString())
                    intent.putExtra("cid", Map.data[position].cid.toString())
                    intent.putExtra("bvid", Map.data[position].bvid)
                    intent.putExtra("title", Map.data[position].title)
                    intent.putExtra("pic", Map.data[position].pic)
                    startActivity(intent, options.toBundle())

                })
                binding.related.adapter = adpater
            }

        })
    }

    private fun isLikeVideo() {
        val url = "https://api.bilibili.com/x/web-interface/archive/has/like"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val data = mapOf(
            "aid" to aid,
        )
        val okhttp = Https()
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: VideoIsGood = json.decodeFromString(it)
            requireActivity().runOnUiThread {
                if (Map.data == 1) {
                    binding.likeimage.setImageResource(R.drawable.good)
                    VideoGooded = true
                }
            }
        })

    }

    private fun isCoinVideo() {
        val url = "https://api.bilibili.com/x/web-interface/archive/coins"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val data = mapOf(
            "aid" to aid,
        )
        val okhttp = Https()
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: VideoIsCoin = json.decodeFromString(it)
            if (Map.data.multiply > 0) binding.coinimage.setImageResource(R.drawable.coin); VideoCoined =
            true
        })

    }

    private fun isStarVideo() {
        val url = "https://api.bilibili.com/x/v2/fav/video/favoured"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val data = mapOf(
            "aid" to aid,
        )
        val okhttp = Https()
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        okhttp.init(cookie_str)
        okhttp.get(url, data, headers, callback = {
            val json = Json { ignoreUnknownKeys = true }
            val Map: VideoIsStar = json.decodeFromString(it)
            if (Map.data.favoured) binding.starimage.setImageResource(R.drawable.star); VideoStared =
            true
        })
    }

    private fun diaglog_coin() {
        val MorePop = PopupWindow(context)
        val binding: PopVideoThrowcoinBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.pop_video_throwcoin,
                null,
                false
            )
        MorePop.contentView = binding.root
        MorePop.setWidth(-1)
        MorePop.setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
        val backgroundDrawable = ColorDrawable(Color.parseColor("#000000"))
        backgroundDrawable.alpha = 0 // 设置透明度，范围从0到255
        MorePop.setBackgroundDrawable(backgroundDrawable)
        MorePop.showAtLocation(
            binding.root,
            Gravity.BOTTOM,
            0,
            0
        )
//        slip_up_show(binding.PopIn, 0f, requireContext())
        binding.close.setOnClickListener {
            slip_down(binding.PopIn, 0f, requireContext())
            MorePop.dismiss()
        }
        binding.onecoin.setOnClickListener {
            CoinVideo(1)
            slip_down(binding.PopIn, 0f, requireContext())
            MorePop.dismiss()
        }
        binding.doublecoin.setOnClickListener {
            CoinVideo(2)
            slip_down(binding.PopIn, 0f, requireContext())
            MorePop.dismiss()
        }
    }

    private fun LikeVideo() {
        val url = "https://api.bilibili.com/x/web-interface/archive/like"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val check = if (VideoGooded) "2" else "1"
        val okhttp = Https()
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val cookie_map = okhttp.init(cookie_str)
        val csrf = cookie_map["bili_jct"] ?: ""
        val data = mapOf(
            "aid" to aid,
            "like" to check,
            "csrf" to csrf,
        )
        okhttp.post(url, data, headers, callback = {
            VideoGooded = !VideoGooded
        })
        if (VideoGooded) {
            binding.likeimage.setImageResource(R.drawable.good_nocheck)
            binding.likenumber.text = (binding.likenumber.text.toString().toInt() - 1).toString()
        } else {
            binding.likeimage.setImageResource(R.drawable.good)
            binding.likenumber.text = (binding.likenumber.text.toString().toInt() + 1).toString()
        }
    }

    private fun CoinVideo(num: Int) {
        val url = "https://api.bilibili.com/x/web-interface/coin/add"
        val headers = mapOf(
            "User-Agent" to user_agent
        )
        val okhttp = Https()
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val cookie_map = okhttp.init(cookie_str)
        val csrf = cookie_map["bili_jct"] ?: ""
        val data = mapOf(
            "aid" to aid,
            "multiply" to num.toString(),
            "csrf" to csrf,
        )
        okhttp.post(url, data, headers, callback = {
            VideoCoined = !VideoCoined
        })
        binding.coinimage.setImageResource(R.drawable.coin)
        binding.coinnumber.text = (binding.coinnumber.text.toString().toInt() + num).toString()

    }

    private fun StarVideo() {
        val url = "https://api.bilibili.com/medialist/gateway/coll/resource/deal"
        val headers = mapOf(
            "User-Agent" to user_agent,
            "Referer" to "https://www.bilibili.com/"
        )
        val okhttp = Https()
        val cookie_str = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("cookie", "null") ?: "null"
        val cookie_map = okhttp.init(cookie_str)
        val csrf = cookie_map["bili_jct"] ?: ""
        val data = mapOf(
            "rid" to aid,
            "type" to "2",
            "csrf" to csrf,
            "add_media_ids" to "49166435",
            "del_media_ids" to "",
        )
        println(aid)
        okhttp.post(url, data, headers, callback = {
            VideoCoined = !VideoCoined
            println(it)
        })
        if (VideoCoined) {
            binding.starimage.setImageResource(R.drawable.star_nocheck)
            binding.starnumber.text = (binding.starnumber.text.toString().toInt() - 1).toString()
        } else {
            binding.starimage.setImageResource(R.drawable.star)
            binding.starnumber.text = (binding.starnumber.text.toString().toInt() + 1).toString()
        }

    }
}