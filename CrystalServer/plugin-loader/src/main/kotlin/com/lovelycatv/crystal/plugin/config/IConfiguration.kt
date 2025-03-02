package com.lovelycatv.crystal.plugin.config

/**
 * @author lovelycat
 * @since 2025-03-02 22:15
 * @version 1.0
 */
interface IConfiguration {
    fun getString(key: String): String?

    fun getStringList(key: String): List<String>?

    fun getInt(key: String): Int?

    fun getLong(key: String): Long?

    fun getShort(key: String): Short?

    fun getDouble(key: String): Double?

    fun getFloat(key: String): Float?

    fun getBoolean(key: String): Boolean?
}