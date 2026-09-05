package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import io.livekit.android.renderer.SurfaceViewRenderer
import io.livekit.android.room.Room
import io.livekit.android.room.track.VideoTrack
import livekit.org.webrtc.RendererCommon

@Composable
fun LiveKitVideoRenderer(
    room: Room?,
    videoTrack: VideoTrack?,
    modifier: Modifier = Modifier,
    isMirror: Boolean = false,
    scalingType: RendererCommon.ScalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT
) {
    if (room == null || videoTrack == null) {
        Box(modifier = modifier.background(Color(0xFF064E3B)))
        return
    }

    var currentAttachedTrack by remember { mutableStateOf<VideoTrack?>(null) }

    AndroidView(
        factory = { ctx ->
            SurfaceViewRenderer(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                try {
                    room.initVideoRenderer(this)
                    setScalingType(scalingType)
                    setMirror(isMirror)
                    setEnableHardwareScaler(true)
                    videoTrack.addRenderer(this)
                    currentAttachedTrack = videoTrack
                } catch (_: Exception) {
                }
            }
        },
        update = { renderer ->
            try {
                renderer.setMirror(isMirror)
                renderer.setScalingType(scalingType)
                if (currentAttachedTrack != videoTrack) {
                    currentAttachedTrack?.removeRenderer(renderer)
                    videoTrack.addRenderer(renderer)
                    currentAttachedTrack = videoTrack
                }
            } catch (_: Exception) {
            }
        },
        onRelease = { renderer ->
            try {
                videoTrack.removeRenderer(renderer)
                currentAttachedTrack?.removeRenderer(renderer)
                renderer.release()
            } catch (_: Exception) {
            }
        },
        modifier = modifier
    )
}

