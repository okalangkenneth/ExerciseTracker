package com.healthtrack.exercise

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.healthtrack.exercise.ui.theme.ExerciseTrackerTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    // Permission launcher for activity recognition
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize permission launcher
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d("MainActivity", "Activity recognition permission granted")
                viewModel.updateUsePedometer(true)
            } else {
                Log.d("MainActivity", "Activity recognition permission denied")
                viewModel.updateUsePedometer(false)
            }
        }

        // Check and request permission
        checkSensorPermission()

        setContent {
            ExerciseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        onShowTimePickerDialog = { showTimePickerDialog(viewModel) }
                    )
                }
            }
        }
    }

    private fun checkSensorPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permission = Manifest.permission.ACTIVITY_RECOGNITION

            when {
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Activity recognition permission granted")
                    viewModel.updateUsePedometer(true)
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    // Show explanation to the user
                    AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This app needs step counter permission to track your walking progress")
                        .setPositiveButton("OK") { _, _ ->
                            permissionLauncher.launch(permission)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
                else -> {
                    permissionLauncher.launch(permission)
                }
            }
        } else {
            // For API levels below 29, no permission needed
            viewModel.updateUsePedometer(true)
        }
    }

    private fun showTimePickerDialog(viewModel: MainViewModel) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour: Int, minute: Int ->
                val timeString = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                viewModel.updateReminderTime(timeString)
                scheduleReminder(hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun scheduleReminder(hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
