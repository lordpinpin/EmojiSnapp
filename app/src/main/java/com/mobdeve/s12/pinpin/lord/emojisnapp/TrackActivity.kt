package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityTrackBinding

class TrackActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityTrackBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var trackAdapter: TrackAdapter? = null

    private var currentPitch: Float = 0f
    private var currentRoll: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emojiList = EmojiFactory.getEmojisWithPositiveUnlockThreshold()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        val progress = intent.getIntExtra("CUR_LEVEL", 25)

        trackAdapter = TrackAdapter(emojiList, progress, this)

        binding.collectionRv.layoutManager = layoutManager
        binding.collectionRv.adapter = trackAdapter
        binding.collectionRv.scrollToPosition(0)

        // Makes B&W
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        binding.backBtn.paint.colorFilter = colorFilter

        binding.backBtn.setOnClickListener {
            finish() // Ends the current activity
        }

        // Set up the sensor manager and accelerometer
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
    }

    override fun onResume() {
        super.onResume()
        // Register the sensor listener when the activity is resumed
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener when the activity is paused to prevent memory leaks
        sensorManager.unregisterListener(this)
    }

    // Handle sensor changes (accelerometer data)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        // Calculate pitch and roll from the accelerometer data
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        // Extract orientation angles (azimuth, pitch, roll) from the rotation matrix
        val orientationAngles = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // Get pitch and roll angles (in degrees)
        var pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat() // Front-back tilt
        var roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()  // Side-to-side tilt

        // Clamp the values to a specific range to prevent excessive tilting
        pitch = clampTilt(pitch, -30f, 30f)  // Example limits for pitch
        roll = clampTilt(roll, -30f, 30f)    // Example limits for roll

        // Update the tilt angles in the adapter
        trackAdapter?.updateTiltAngles(currentPitch, currentRoll)
    }

    private fun clampTilt(value: Float, min: Float, max: Float): Float {
        return Math.max(min, Math.min(max, value))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation, but you can handle sensor accuracy changes if necessary
    }
}