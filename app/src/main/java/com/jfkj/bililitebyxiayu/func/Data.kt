package com.jfkj.bililitebyxiayu.func

import kotlinx.serialization.Serializable


//val cookie_str =
//    "buvid3=241E0CA7-9678-3101-19DF-C4F18E65CDC461870infoc; i-wanna-go-back=-1; _uuid=5E715DC3-C238-108A10-10BAA-41093F517682563035infoc; FEED_LIVE_VERSION=V8; DedeUserID=355559688; DedeUserID__ckMd5=8a15caaddcef1d4d; buvid_fp_plain=undefined; rpdid=|(k|YRk|luuJ0J'uY)))m~|kY; nostalgia_conf=-1; b_ut=5; header_theme_version=CLOSE; LIVE_BUVID=AUTO2016958081387233; enable_web_push=DISABLE; CURRENT_FNVAL=4048; hit-dyn-v2=1; CURRENT_QUALITY=80; b_nut=1706623669; PVID=1; fingerprint=6b3b6c2169d7e7f9d3bee1555b340e81; browser_resolution=1347-721; home_feed_column=4; bp_video_offset_355559688=893088876726321176; b_lsid=A25E446E_18D64748BE0; bili_ticket=eyJhbGciOiJIUzI1NiIsImtpZCI6InMwMyIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDcwNDc1MDYsImlhdCI6MTcwNjc4ODI0NiwicGx0IjotMX0.boZPnyESUEizYCjgpadEHQjyrOtHTkdOXpaJ_Mk3Jr4; bili_ticket_expires=1707047446; SESSDATA=8c300178,1722341518,556fb*22CjDpvRbAimJ4SOgJz99uZIEX8lPWqkQzbBkn5Ftq7OI2kubm9SZmcof8x4M1oCw5A54SVmxCT3JBbjFtN1FkWHc3VXFRUFQwR0dMeHppaldzbWxfczA2cktWZF9XWGdDYkY1NEIxUDhOY2h5cWNQQVQxcnNpV1ByNWRtZmgyTUVkTEw3b0I2dlZnIIEC; bili_jct=e1722c55ccd67d93d75cb2030526ce36; sid=6158tvor; buvid4=FD3743E8-A723-0F23-FE82-E3203EBE37E463052-023070917-7sNEkaqZhL5+YJspJoOh0g==; buvid_fp=241E0CA7-9678-3101-19DF-C4F18E65CDC461870infoc"
val user_agent =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36"

//主页推荐请求数据相关
@Serializable
data class Stat(
    val view: Int,
    val like: Int,
    val danmaku: Int
) {
    operator fun get(s: String): Int? {
        return when (s) {
            "view" -> view
            "like" -> like
            "danmaku" -> danmaku
            else -> null
        }
    }
}


@Serializable
data class Owner(
    val name: String,
    val face: String
) {
    operator fun get(s: String): String? {
        return when (s) {
            "name" -> name
            "face" -> face
            else -> null
        }
    }
}

@Serializable
data class HomeVideoData(
    val id: Long,
    val bvid: String,
    val cid: Long,
    val goto: String,
    val uri: String,
    val pic: String,
    val title: String,
    val duration: Long,
    val pubdate: Long,
    val owner: Owner,
    val stat: Stat
) {
    operator fun get(s: String): String? {
        return when (s) {
            "id" -> id.toString()
            "bvid" -> bvid
            "cid" -> cid.toString()
            "goto" -> goto
            "uri" -> uri
            "pic" -> pic
            "title" -> title
            "duration" -> duration.toString()
            "pubdate" -> pubdate.toString()
            // 添加其他属性的处理
            else -> null
        }
    }
}

@Serializable
data class HomeVideoItem(val item: MutableList<HomeVideoData>)

@Serializable
data class HomeResult(val data: HomeVideoItem)

//视频播放页相关
@Serializable
data class VideoResult(val data: VideoData)

@Serializable
data class VideoDurl(
    val order: Int,
    val length: Long,
    val size: Long,
    val url: String,
)

@Serializable
data class VideoData(
    val quality: Int,
    val format: String,
    val timelength: Int,
    val last_play_time: Int?,
    val durl: MutableList<VideoDurl>
)

//视频详细信息,包含分集和合集
@Serializable
data class VideoDetailResult(val data: VideoDetail)

@Serializable
data class VideoDetail(
    val tid: Int,
    val tname: String,
    val pic: String,
    val title: String,
    val duration: Long,
    val pubdate: Long,
    val desc: String,//视频详情描述
    val owner: VideoDetailOwner,
    val stat: VideoDetailStat,
    val pages: MutableList<VideoDetailPage>?,
)

@Serializable
data class VideoDetailOwner(
    val mid: Long,
    val name: String,
    val face: String,
)

@Serializable
data class VideoDetailStat(
    val view: Long,
    val danmaku: Long,
    val favorite: Long,//收藏人数
    val coin: Long,
    val share: Long,
    val like: Long,//点赞数
)

@Serializable
data class VideoDetailPage(
    val cid: Long,
    val part: String,
)

//是否点赞，投币,收藏,关注
//点赞
@Serializable
data class VideoIsGood(val data: Int)

//投币
@Serializable
data class VideoIsCoin(val data: VideoIsCoinNumber)

@Serializable
data class VideoIsCoinNumber(val multiply: Int)

//收藏
@Serializable
data class VideoIsStar(val data: VideoIsStarNumber)

@Serializable
data class VideoIsStarNumber(val favoured: Boolean)

//视频播放页推荐视频
@Serializable
data class VideoRelatedResult(val data: MutableList<VideoRelated>)

@Serializable
data class VideoRelated(
    val aid: Long,
    val bvid: String,
    val cid: Long,
    val tid: Int,
    val tname: String,
    val pic: String,
    val title: String,
    val duration: Long,
    val pubdate: Long,
    val desc: String,//视频详情描述
    val owner: VideoRelatedOwner,
    val stat: VideoRelatedStat,
)

@Serializable
data class VideoRelatedOwner(
    val name: String,
)

@Serializable
data class VideoRelatedStat(
    val view: Long,
    val danmaku: Long,
    val favorite: Long,//收藏人数
    val coin: Long,
    val share: Long,
    val like: Long,//点赞数
)

@Serializable
data class VideoDiversityResult(val data: MutableList<VideoDiversity>)

@Serializable
data class VideoDiversity(
    val cid: Long,
    val part: String,
    val duration: Long,

    )

//主页个人基本登录信息
@Serializable
data class MyResult(val data: MyData)

@Serializable
data class MyData(
    val isLogin: Boolean,
    val face: String,
    val mid: Long,
    val money: Double,
    val uname: String,
    val moral: Int,
    val wbi_img: MyWbi,
)

@Serializable
data class MyWbi(
    val img_url: String,
    val sub_url: String,
)

//用户名片信息
@Serializable
data class UserCardResult(val data: UserCardData)

@Serializable
data class CardData(
    val name: String,
    val face: String,
    val fans: Long,
    val friend: Long,
    val attention: Long,
    val sign: String,
    val level_info: UserLevel
)

@Serializable
data class UserCardData(
    val card: CardData,
    val space: UserSpaceData,
)

@Serializable
data class UserLevel(
    val current_level: Int,
)

@Serializable
data class UserSpaceData(
    val s_img: String,
)

//用户卡片投稿视频信息
@Serializable
data class UserVideoResult(val data: UserVideoAllList)

@Serializable
data class UserVideoAllList(val list: UserVideoList)

@Serializable
data class UserVideoList(
//    val tlist: MutableList<UserVideoTList>,
    val vlist: MutableList<UserVideoVList>,
)

@Serializable
data class UserVideoVList(
    val comment: Long,
    val typeid: Int,
    val play: Long,
    val pic: String,
    val title: String,
    val length: String,
    val bvid: String,
    val aid: Long,
)

//@Serializable
//data class UserVideoYListData(
//
//
//)

//我的关注列表
@Serializable
data class MyStarResult(val data: MyStarList)

@Serializable
data class MyStarList(val list: MutableList<MyStarData>)

@Serializable
data class MyStarData(
    val face: String,
    val mid: Long,
    val uname: String,
    val sign: String,
)

//我的历史列表
@Serializable
data class HistoryResult(val data: HistoryList)

@Serializable
data class HistoryList(val list: MutableList<HistoryData>, val cursor: HistoryCursor)

@Serializable
data class HistoryCursor(
    val max: Long,
    val view_at: Long,
    val business: String,
)

@Serializable
data class HistoryData(
    val title: String,
    val cover: String,
    val author_name: String,
    val author_face: String,
    val author_mid: Long,
    val duration: Long,
    val history: HistoryDetailData,
)

@Serializable
data class HistoryDetailData(
    val bvid: String,
    val oid: Long,//就是aid
    val cid: Long,
)

//我的收藏
@Serializable
data class FavourResult(val data: FavourList)

@Serializable
data class FavourList(val list: MutableList<FavourData>)

@Serializable
data class FavourData(
    val title: String,
    val fid: Long,
    val mid: Long,
    val id: Long,
    val media_count: Int,
)

//我的详细收藏列表
@Serializable
data class FavourDetailResult(val data: FavourDetailList)

@Serializable
data class FavourDetailList(val medias: MutableList<FavourDetailData>)


@Serializable
data class FavourDetailData(
    val title: String,
    val cover: String,
    val duration: Long,
    val upper: FavourDetailUpper,
    val pubtime: Long,
    val bvid: String,
    val id: Long,
    val ugc: FavourDetailUgc,
)

@Serializable
data class FavourDetailUpper(
    val mid: Long,
    val name: String,//就是aid
    val face: String,
)

@Serializable
data class FavourDetailUgc(
    val first_cid: Long,
)

//我的搜索提示
@Serializable
data class SearchTintResult(val result: SearchTintTag)

@Serializable
data class SearchTintTag(val tag: MutableList<SearchTintData>)

@Serializable
data class SearchTintData(
    val value: String,
    val name: String,
)

//搜索结果
@Serializable
data class SearchResult(val data: SearchDataResult)

@Serializable
data class SearchDataResult(val result: MutableList<SearchDataDetail>)


//@Serializable
//data class SearchData(
//    val data: MutableList<SearchDataDetail>,
//    val result_type: String
//)

@Serializable
data class SearchDataDetail(
    val author: String,
    val id: Long,
    val aid: Long,
    val bvid: String,
    val mid: Long,
    val typeid: Int,
    val title: String,
    val pic: String,
    val play: Long,
    val tag: String,
    val duration: String,
)


//评论区详细
@Serializable
data class CommentResult(val data: CommentDataResult)

@Serializable
data class CommentDataResult(val replies: MutableList<CommentReplies>)

@Serializable
data class CommentReplies(
    val rpid: Long,
    val mid: Long,
    val count: Long,
    val ctime: Long,
    val like: Long,
    val member: CommentRepliesUser,
    val reply_control: CommentRepliesTime,
    val content: CommentRepliesContent,
    val replies: MutableList<CommentReplies>?=null,
)

@Serializable
data class CommentRepliesContent(
    val message: String,
    val pictures:MutableList<CommentImage>?=null,
)
@Serializable
data class CommentImage(
    val img_src: String,
    val img_width: Long,
    val img_height: Long,
    val img_size:Float,
    )

@Serializable
data class CommentRepliesUser(
    val uname: String,
    val avatar: String,
    val level_info: CommentRepliesUserLevel,

    )

@Serializable
data class CommentRepliesUserLevel(
    val current_level: Int,
)

@Serializable
data class CommentRepliesTime(
    val time_desc: String,
//    val sub_reply_entry_text:String?,
)