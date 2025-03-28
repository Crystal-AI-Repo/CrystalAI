package com.lovelycatv.crystal.openapi.exception

/**
 * @author lovelycat
 * @since 2025-03-23 21:44
 * @version 1.0
 */
class PlatformNotSupportException(name: String) : RuntimeException("Platform $name is not supported yet.")