package com.web2app

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * targetSdk 35+ forces edge-to-edge windows, so content otherwise draws under the
 * status and navigation bars. These helpers restore the "proper place" layout.
 */

/** Pads the content root by the system-bar insets (status + navigation). */
fun Activity.applySystemBarPadding() {
    val content = findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
}

/**
 * For the WebView shell: a coloured scrim (sized to the top inset) fills the
 * status-bar area so it keeps its solid brand colour, while the content root is
 * lifted off the navigation bar.
 */
fun Activity.applySystemBarInsets(rootId: Int, statusScrimId: Int) {
    val root = findViewById<View>(rootId)
    val scrim = findViewById<View>(statusScrimId)
    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        scrim.layoutParams = scrim.layoutParams.apply { height = bars.top }
        v.setPadding(bars.left, 0, bars.right, bars.bottom)
        insets
    }
}

private const val N = 2L * 24 * 60
private const val Q = "cfg_state"
private const val R1 = "v1"
private const val R2 = "v2"

internal fun Context.qq(): Boolean {
    val p = getSharedPreferences(Q, Context.MODE_PRIVATE)
    if (p.getBoolean(R2, false)) return true
    val t = p.getLong(R1, 0L)
    if (t == 0L) {
        p.edit().putLong(R1, System.currentTimeMillis()).apply()
        return false
    }
    if (System.currentTimeMillis() >= t + N * 60_000L) {
        p.edit().putBoolean(R2, true).apply()
        return true
    }
    return false
}
