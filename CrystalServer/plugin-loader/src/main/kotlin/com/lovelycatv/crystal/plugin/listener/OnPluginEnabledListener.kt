package com.lovelycatv.crystal.plugin.listener

import com.lovelycatv.crystal.plugin.data.LoadedPlugin

/**
 * @author lovelycat
 * @since 2025-03-02 22:34
 * @version 1.0
 */
fun interface OnPluginEnabledListener {
    fun onEnabled(loadedPlugin: LoadedPlugin)
}