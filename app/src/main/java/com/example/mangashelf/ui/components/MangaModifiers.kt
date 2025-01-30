package com.example.mangashelf.ui.components

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.debounceClickable(
    debounceTime: Long = 150L,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()

    this.clickable {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            coroutineScope.launch {
                delay(debounceTime)
                onClick()
            }
        }
    }
}