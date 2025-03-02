package com.lovelycatv.crystal.plugin.api

import com.lovelycatv.crystal.plugin.listener.OnPluginEnabledListener
import com.lovelycatv.crystal.plugin.listener.OnPluginLoadedListener
import com.lovelycatv.crystal.plugin.listener.OnPluginPostEnableListener

/**
 * @author lovelycat
 * @since 2025-03-02 22:33
 * @version 1.0
 */
object PluginLoaderHook {
    private val _onPluginLoadedListeners: MutableList<OnPluginLoadedListener> = mutableListOf()
    val onPluginLoadedListeners: List<OnPluginLoadedListener> get() = _onPluginLoadedListeners

    private val _onPluginEnabledListeners: MutableList<OnPluginEnabledListener> = mutableListOf()
    val onPluginEnabledListeners: List<OnPluginEnabledListener> get() = _onPluginEnabledListeners

    private val _onPluginPostEnabledListeners: MutableList<OnPluginPostEnableListener> = mutableListOf()
    val onPluginPostEnabledListeners: List<OnPluginPostEnableListener> get() = _onPluginPostEnabledListeners

    fun addOnPluginLoadedListener(listener: OnPluginLoadedListener) {
        _onPluginLoadedListeners.add(listener)
    }

    fun addOnPluginEnabledListener(listener: OnPluginEnabledListener) {
        _onPluginEnabledListeners.add(listener)
    }

    fun addOnPluginPostEnabledListener(listener: OnPluginPostEnableListener) {
        _onPluginPostEnabledListeners.add(listener)
    }
}