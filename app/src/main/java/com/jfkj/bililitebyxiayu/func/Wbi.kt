package com.jfkj.bililitebyxiayu.func

import android.content.SharedPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

val mixinKeyEncTab = intArrayOf(
    46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
    33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
    61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
    36, 20, 34, 44, 52
)

fun getMixinKey(orig: String): String {
    return mixinKeyEncTab.joinToString("") { orig[it].toString() }.take(32)
}

fun encWbi(
    params: LinkedHashMap<String, Any>,
    imgKey: String,
    subKey: String
): LinkedHashMap<String, Any> {
    val mixinKey = getMixinKey(imgKey + subKey)
    val currTime = System.currentTimeMillis() / 1000
    params["wts"] = currTime
    val sortedParams = params.toSortedMap()
    val sanitizedParams = sortedParams.mapValues { entry ->
        entry.value.toString().filter { char -> char !in setOf('!', '\'', '(', ')', '*') }
    }
    val query = sanitizedParams.entries.joinToString("&") { "${it.key}=${it.value}" }
    val wbiSign = MessageDigest.getInstance("MD5").digest((query + mixinKey).toByteArray())
        .joinToString("") { byte -> "%02x".format(byte) }
    params["w_rid"] = wbiSign
    return params
}

//mid是从这里获取保存的
fun getWbiKeys(
    sharedPrefs: SharedPreferences?,
    params: LinkedHashMap<String, Any>,
    callback: (Pair<LinkedHashMap<String, Any>, String>) -> Unit
) {
    if (sharedPrefs != null) {
        if (sharedPrefs.getString("imgKey", "null")=="null") {
            val url = "https://api.bilibili.com/x/web-interface/nav"
            val headers = mapOf(
                "User-Agent" to user_agent
            )
            val cookie_str=sharedPrefs.getString("cookie", "null")?:"null"
            val okhttp = Https()
            okhttp.init(cookie_str)
            okhttp.get(url, null, headers, callback = {
                val json = Json { ignoreUnknownKeys = true }
                val Map: MyResult = json.decodeFromString(it)
                val imgUrl = Map.data.wbi_img.img_url
                val subUrl = Map.data.wbi_img.sub_url
                val imgKey = imgUrl.split('/').last().split('.').first()
                val subKey = subUrl.split('/').last().split('.').first()

                // 更新时间戳+写入key
                with(sharedPrefs.edit()) {
                    putString("imgKey", imgKey)
                    putString("subKey", subKey)
                    putString("mid", Map.data.mid.toString())
                    apply()
                }
                val signedParams = encWbi(params, imgKey, subKey)
                val query = signedParams.entries.joinToString("&") { "${it.key}=${it.value}" }
                callback(Pair(signedParams, query))
            })
        } else {
            val signedParams = encWbi(
                params,
                sharedPrefs.getString("imgKey", "") ?: "",
                sharedPrefs.getString("subKey", "") ?: ""
            )
            val query = signedParams.entries.joinToString("&") { "${it.key}=${it.value}" }
            callback(Pair(signedParams, query))
        }

    }
}
