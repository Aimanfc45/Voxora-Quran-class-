package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

enum class CompassSensorState {
    ACTIVE,
    CALIBRATING,
    UNAVAILABLE
}

data class CompassData(
    val azimuthDegrees: Float = 0f, // Device heading relative to Magnetic/True North (0..360)
    val pitchDegrees: Float = 0f,
    val rollDegrees: Float = 0f,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    val sensorState: CompassSensorState = CompassSensorState.ACTIVE,
    val isSensorAvailable: Boolean = true
)

/**
 * Sensor-based Compass orientation engine for Voxora Qiblah Finder.
 * Listens to TYPE_ROTATION_VECTOR (high accuracy) with fallback to
 * TYPE_ACCELEROMETER + TYPE_MAGNETIC_FIELD.
 */
class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val rotationVectorSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magneticSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _compassData = MutableStateFlow(
        CompassData(
            isSensorAvailable = (rotationVectorSensor != null) || (accelerometerSensor != null && magneticSensor != null)
        )
    )
    val compassData: StateFlow<CompassData> = _compassData.asStateFlow()

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false

    private var smoothedAzimuth = 0f
    private var isListening = false

    fun startListening() {
        if (isListening || sensorManager == null) return

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
            isListening = true
        } else if (accelerometerSensor != null && magneticSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI)
            isListening = true
        } else {
            _compassData.value = _compassData.value.copy(
                sensorState = CompassSensorState.UNAVAILABLE,
                isSensorAvailable = false
            )
        }
    }

    fun stopListening() {
        if (!isListening || sensorManager == null) return
        sensorManager.unregisterListener(this)
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        var azimuthDegrees = 0f
        var pitchDegrees = 0f
        var rollDegrees = 0f

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            pitchDegrees = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
            rollDegrees = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

            azimuthDegrees = (azimuthDegrees + 360f) % 360f
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
            lastAccelerometerSet = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
            lastMagnetometerSet = true
        }

        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR && lastAccelerometerSet && lastMagnetometerSet) {
            val success = SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                pitchDegrees = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                rollDegrees = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                azimuthDegrees = (azimuthDegrees + 360f) % 360f
            }
        }

        // Apply circular exponential smoothing filter
        smoothedAzimuth = smoothAzimuth(smoothedAzimuth, azimuthDegrees, alpha = 0.25f)

        val sensorState = when (event.accuracy) {
            SensorManager.SENSOR_STATUS_UNRELIABLE, SensorManager.SENSOR_STATUS_ACCURACY_LOW -> CompassSensorState.CALIBRATING
            else -> CompassSensorState.ACTIVE
        }

        _compassData.value = CompassData(
            azimuthDegrees = smoothedAzimuth,
            pitchDegrees = pitchDegrees,
            rollDegrees = rollDegrees,
            accuracy = event.accuracy,
            sensorState = sensorState,
            isSensorAvailable = true
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val sensorState = when (accuracy) {
            SensorManager.SENSOR_STATUS_UNRELIABLE, SensorManager.SENSOR_STATUS_ACCURACY_LOW -> CompassSensorState.CALIBRATING
            else -> CompassSensorState.ACTIVE
        }
        _compassData.value = _compassData.value.copy(
            accuracy = accuracy,
            sensorState = sensorState
        )
    }

    private fun smoothAzimuth(prev: Float, current: Float, alpha: Float): Float {
        var diff = current - prev
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        var result = prev + alpha * diff
        return (result + 360f) % 360f
    }
}
