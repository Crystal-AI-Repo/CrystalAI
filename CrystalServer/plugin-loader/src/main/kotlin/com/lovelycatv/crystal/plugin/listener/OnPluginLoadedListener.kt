package com.lovelycatv.crystal.plugin.listener

import com.lovelycatv.crystal.plugin.data.RawLoadedPlugin

/**
 * @author lovelycat
 * @since 2025-03-02 22:34
 * @version 1.0
 */
fun interface OnPluginLoadedListener {
    fun onLoaded(rawLoadedPlugin: RawLoadedPlugin)
}