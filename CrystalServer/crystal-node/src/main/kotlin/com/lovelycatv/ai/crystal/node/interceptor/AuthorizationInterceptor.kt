package com.lovelycatv.ai.crystal.node.interceptor

import com.lovelycatv.ai.crystal.node.utils.JwtUtils
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.security.SignatureException
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.toJSONString
import org.springframework.web.servlet.HandlerInterceptor

class AuthorizationInterceptor(
    private val secretKey: String
) : HandlerInterceptor {

    override fun preHandle(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse, handler: Any): Boolean {
        val fxWriteUnauthorizedResult = fun (message: String) {
            servletResponse.contentType = "application/json;charset=utf-8"
            servletResponse.writer.write(Result.unauthorized(message).toJSONString())
            servletResponse.writer.flush()
            servletResponse.writer.close()
        }

        return try {
            val tokenStr = servletRequest.getHeader("Authorization").replace("Bearer ", "")
            if (JwtUtils.validateToken(tokenStr, secretKey)) {
                val claims = JwtUtils.getClaimsFromToken(tokenStr, secretKey)
                if (claims != null) {
                    // val username = claims.subject
                    true
                } else {
                    fxWriteUnauthorizedResult.invoke("Invalid payloads")
                    false
                }
            } else {
                fxWriteUnauthorizedResult.invoke("Token is invalid or expired")
                false
            }
        } catch (e: SignatureException) {
            fxWriteUnauthorizedResult.invoke("Invalid token")
            false
        } catch (e: ExpiredJwtException) {
            fxWriteUnauthorizedResult.invoke("Token is expired")
            false
        } catch (e: MalformedJwtException) {
            fxWriteUnauthorizedResult.invoke("Invalid token format")
            false
        } catch (e: Exception) {
            fxWriteUnauthorizedResult.invoke("Unexpected exception: " + e.message)
            false
        }
    }
}