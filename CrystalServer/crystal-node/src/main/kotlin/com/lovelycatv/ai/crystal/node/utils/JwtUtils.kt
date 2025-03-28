package com.lovelycatv.ai.crystal.node.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

object JwtUtils {
    fun generateToken(username: String, expiredIn: Long, secretKey: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiredIn))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun getClaimsFromToken(token: String?, secretKey: String): Claims? {
        return if (token == null)
            null
        else Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body
    }

    fun isTokenExpired(token: String?, secretKey: String): Boolean {
        if (token == null) return false
        val expiration = getClaimsFromToken(token, secretKey)?.expiration ?: Date(0)
        return expiration.before(Date())
    }

    fun validateToken(token: String?, secretKey: String): Boolean {
        return !isTokenExpired(token, secretKey)
    }
}