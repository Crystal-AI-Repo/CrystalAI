package com.lovelycatv.crystal.plugin.config

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream

/**
 * @author lovelycat
 * @since 2025-03-02 22:15
 * @version 1.0
 */
class YAMLConfiguration(inputStream: InputStream) : IConfiguration {
    private val yaml: Yaml = Yaml()
    private val dataMap: Map<String, Any>

    constructor(yamlFile: File) : this(yamlFile.inputStream())

    init {
        this.dataMap = yaml.load(inputStream)
    }



    private fun getValue(path: String): Any? {
        return path.split(".").fold(dataMap as Any?) { map, key ->
            (map as? Map<*, *>)?.get(key)
        }
    }

    override fun getString(key: String): String? {
        return this.getValue(key) as? String
    }

    @Suppress("UNCHECKED_CAST")
    override fun getStringList(key: String): List<String>? {
        return this.getValue(key) as? List<String>
    }

    override fun getInt(key: String): Int? {
        return this.getValue(key) as? Int
    }

    override fun getLong(key: String): Long? {
        return this.getValue(key) as? Long
    }

    override fun getShort(key: String): Short? {
        return this.getValue(key) as? Short
    }

    override fun getDouble(key: String): Double? {
        return this.getValue(key) as? Double
    }

    override fun getFloat(key: String): Float? {
        return this.getValue(key) as? Float
    }

    override fun getBoolean(key: String): Boolean? {
        return this.getValue(key) as? Boolean
    }
}