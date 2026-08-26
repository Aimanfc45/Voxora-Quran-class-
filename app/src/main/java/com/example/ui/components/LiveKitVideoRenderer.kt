package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.livekit.android.renderer.SurfaceViewRenderer
import io.livekit.android.room.Room
import io.livekit.android.room.track.VideoTrack

@Composable
fun LiveKitVideoRenderer(
    room: Room?,
    videoTrack: VideoTrack?,
    modifier: Modifier = Modifier
) {
    if (room == null || videoTrack == null) {
        Box(modifier = modifier.background(Color.Black))
        return
    }

    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            SurfaceViewRenderer(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                try {
                    room.initVideoRenderer(this)
                    videoTrack.addRenderer(this)
                } catch (_: Exception) {
                }
            }
        },
        update = { renderer ->
            try {
                videoTrack.addRenderer(renderer)
            } catch (_: Exception) {
            }
        },
        onRelease = { renderer ->
            try {
                videoTrack.removeRenderer(renderer)
                renderer.release()
            } catch (_: Exception) {
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
