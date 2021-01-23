package com.supersidor.flightmap.util

import org.springframework.http.HttpCookie
import org.springframework.util.MultiValueMap
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component

@Component
class CookieUtility {
    fun getCookieValue(cookieMap: MultiValueMap<String?, HttpCookie>, cookieName: String?): String? {
        val cookies = cookieMap[cookieName] ?: return null
        return cookies.stream().findFirst().map { obj: HttpCookie -> obj.value }.orElse(null)
    }

    fun deleteCookie(response: ServerHttpResponse, cookieMap: MultiValueMap<String?, HttpCookie>, cookieName: String) {
        val cookies = cookieMap[cookieName]
        for (cookie in cookies!!) {
            val currentCookieName = cookie.name
            if (currentCookieName == cookieName) {
                val rcb = ResponseCookie.from(currentCookieName, "")
                rcb.path("/")
                rcb.maxAge(0)
                response.addCookie(rcb.build())
            }
        }
    }

    fun addCookie(response: ServerHttpResponse, cookieName: String?, cookieValue: String?, path: String?, age: Int) {
        val rcb = ResponseCookie.from(cookieName, cookieValue)
        rcb.path("/")
        rcb.httpOnly(true)
        rcb.maxAge(120)
        response.addCookie(rcb.build())
    }
}
