package com.jfkj.bililitebyxiayu.func

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException

class Https {
    lateinit var client: OkHttpClient
    lateinit var request:Request
    fun init(cookie: String): MutableMap<String, String> {
        // 将cookie字符串解析成map
        val cookies = cookie.split("; ")
            .associate { it.substringBefore("=") to it.substringAfter("=") }.toMutableMap()
        client = OkHttpClient.Builder()
            .cookieJar(MangerCookieJar(cookies))
            .build()
        return cookies
    }

    fun init(): OkHttpClient {
        client = OkHttpClient.Builder()
            .build()
        return client
    }

    fun get(
        url: String,
        params: Map<String, String>? = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        callback: (String) -> Unit
    ) {
        if (params!=null){
            val requestUrl = buildUrlWithParams(url, params)
            request = buildRequest(requestUrl.toString(), headers)
        }else{
            request = buildRequest(url, headers)
        }
        return enqueueRequest(request, callback)
    }

    fun post(
        url: String,
        params: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        callback: (String) -> Unit
    ) {
        val formBody = buildFormBody(params)
        val request = buildRequest(url, headers, formBody, HttpMethod.POST)
        return enqueueRequest(request, callback)
    }

    private fun buildUrlWithParams(url: String, params: Map<String, String>): HttpUrl {
        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()
        params.forEach { (key, value) ->
            urlBuilder?.addQueryParameter(key, value)
        }
        return urlBuilder?.build() ?: throw IllegalArgumentException("Invalid URL: $url")
    }

    private fun buildFormBody(params: Map<String, String>): RequestBody {
        val formBuilder = FormBody.Builder()
        params.forEach { (key, value) ->
            formBuilder.add(key, value)
        }
        return formBuilder.build()
    }

    private fun buildRequest(
        url: String,
        headers: Map<String, String>,
        requestBody: RequestBody? = null,
        method: HttpMethod = HttpMethod.GET
    ): Request {
        val requestBuilder = Request.Builder()
            .url(url)
            .headers(buildHeaders(headers))

        when (method) {
            HttpMethod.GET -> requestBuilder.get()
            HttpMethod.POST -> requestBody?.let { requestBuilder.post(it) }
        }

        return requestBuilder.build()
    }

    private fun buildHeaders(headers: Map<String, String>): Headers {
        val headersBuilder = Headers.Builder()
        headers.forEach { (key, value) ->
            headersBuilder.add(key, value)
        }
        return headersBuilder.build()
    }

    private fun enqueueRequest(request: Request, callback: (String) -> Unit) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Request failed:" + e.printStackTrace().toString())
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string() ?: "Request failed")
                } else {
                    callback("Request failed:" + response.body?.string())
                }
            }
        })
    }

    private enum class HttpMethod {
        GET, POST
    }
}

class MangerCookieJar(private val cookies: MutableMap<String, String> = mutableMapOf()) :
    CookieJar {
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            // 将新的 Cookie 添加到 Map 中
            this.cookies[cookie.name] = cookie.value
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        // 从 Map 中获取保存的 Cookie
        return cookies.map { (name, value) ->
            Cookie.Builder()
                .domain(url.host)
                .path("/")
                .name(name)
                .value(value)
                .build()
        }
    }
}

fun BuildUrl(url:String,data: MutableMap<String, String>): HttpUrl {
    val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()
    data.forEach { (key, value) ->
        urlBuilder?.addQueryParameter(key, value)
    }
    return urlBuilder?.build() ?: throw IllegalArgumentException("Invalid URL: $url")
}