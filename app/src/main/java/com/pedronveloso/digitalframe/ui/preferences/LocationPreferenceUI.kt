package com.pedronveloso.digitalframe.ui.preferences

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.preferences.PreferenceItem
import com.pedronveloso.digitalframe.preferences.location.LocationData
import com.pedronveloso.digitalframe.ui.MyTypography
import com.pedronveloso.digitalframe.ui.controls.SaveButton
import com.pedronveloso.digitalframe.utils.log.LogStore
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.Executor
import kotlin.coroutines.resume

@Composable
fun LocationPreferenceComposable(preference: PreferenceItem.LocationPref) {
    val logger = LogStoreProvider.getLogStore()
    val coroutineScope = rememberCoroutineScope()
    val locationManager =
        LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var showLocationSettingsDialog by remember { mutableStateOf(false) }

    val initialLocation = preference.initialValueProvider.invoke()
    var latitude by remember { mutableStateOf(initialLocation.latitude.toString()) }
    var longitude by remember { mutableStateOf(initialLocation.longitude.toString()) }
    var latError by remember { mutableStateOf(false) }
    var lonError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {
                    isLoading = true
                    coroutineScope.launch {
                        getCurrentLocationWithTimeout(
                            locationManager = locationManager,
                            onLocationReceived = { location ->
                                logger.log("Location received - perm granted: $location")
                                latitude = location.latitude.toString()
                                longitude = location.longitude.toString()
                                isLoading = false
                            },
                            logger = logger,
                            onError = {
                                logger.log("Error getting location after permission granted")
                                isLoading = false
                                // You might want to show an error message to the user here
                            }
                        )
                    }
                } else {
                    // Permission denied
                    logger.log("Location permission denied")
                    // You might want to show a message to the user explaining why the permission is needed
                }
            },
        )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = preference.title, style = MyTypography.titleMedium)
        preference.description?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = it, style = MyTypography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = latitude,
            onValueChange = {
                latitude = it
                latError = !isValidLatitude(it)
                hasChanges = true
            },
            isError = latError,
            label = { stringResource(id = R.string.pref_location_lat) },
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (latError) {
            Text(
                stringResource(id = R.string.pref_location_lat_error),
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = longitude,
            onValueChange = {
                longitude = it
                lonError = !isValidLongitude(it)
                hasChanges = true
            },
            isError = lonError,
            label = { Text(stringResource(id = R.string.pref_location_lon)) },
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (lonError) {
            Text(
                stringResource(id = R.string.pref_location_lon_error),
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            GetCurrentLocationButton(
                isLoading = isLoading,
                onGetCurrent = {
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                        ) != PackageManager.PERMISSION_GRANTED -> {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        !isHighAccuracyLocationEnabled(context) -> {
                            showLocationSettingsDialog = true
                        }
                        else -> {
                            logger.log("Getting current location")
                            isLoading = true
                            coroutineScope.launch {
                                getCurrentLocationWithTimeout(
                                    locationManager = locationManager,
                                    onLocationReceived = { location ->
                                        logger.log("Location received: $location")
                                        latitude = location.latitude.toString()
                                        longitude = location.longitude.toString()
                                        isLoading = false
                                        hasChanges = true
                                    },
                                    logger = logger,
                                    onError = { reason ->
                                        logger.error(reason)
                                        isLoading = false
                                        // Handle error, maybe show a message to the user
                                    }
                                )
                            }
                        }
                    }
                },
            )

            Spacer(
                modifier =
                Modifier
                    .height(8.dp)
                    .width(8.dp),
            )

            SaveButton(
                onSave = {
                    if (!latError && !lonError) {
                        logger.log("Location saved: $latitude, $longitude")
                        preference.onChangeCallback?.invoke(
                            LocationData(
                                latitude.toDouble(),
                                longitude.toDouble(),
                            ),
                        )
                        hasChanges = false
                    }
                },
                enabled = hasChanges && !latError && !lonError,
                buttonText = stringResource(id = R.string.pref_location_save),
            )

            if (showLocationSettingsDialog) {
                AlertDialog(
                    onDismissRequest = { showLocationSettingsDialog = false },
                    title = { Text(stringResource(R.string.pref_location_high_accuracy_title)) },
                    text = { Text(stringResource(R.string.pref_location_high_accuracy_description)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLocationSettingsDialog = false
                                openLocationSettings(context)
                            }
                        ) {
                            Text(stringResource(R.string.pref_location_high_accuracy_settings))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLocationSettingsDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }


    }
}

@Composable
fun GetCurrentLocationButton(
    isLoading: Boolean,
    onGetCurrent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(onClick = onGetCurrent, modifier = modifier) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
        } else {
            Text(stringResource(R.string.pref_location_get_from_device))
        }
    }
}


suspend fun getCurrentLocationWithTimeout(
    locationManager: LocationManager,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    timeoutDuration: Long = 120000, // 120 seconds
    onLocationReceived: (Location) -> Unit,
    logger: LogStore,
    onError: (String) -> Unit
) = withContext(dispatcher) {
    try {
        withTimeout(timeoutDuration) {
            suspendCancellableCoroutine { continuation ->
                getCurrentLocation(
                    locationManager = locationManager,
                    executor = CoroutineScope(dispatcher).asExecutor(),
                    logger = logger,
                    onLocationReceived = { location ->
                        onLocationReceived(location)
                        continuation.resume(Unit)
                    },
                    onError = { reason ->
                        onError("Error getting location: $reason")
                        continuation.resume(Unit)
                    }
                )

                continuation.invokeOnCancellation {
                    // For API 30+, we don't need to do anything here as the coroutine cancellation will handle it
                    // For older versions, we rely on the timeout in getCurrentLocation
                }
            }
        }
    } catch (e: TimeoutCancellationException) {
        onError("Timeout getting location")
    }
}

// Extension function to convert CoroutineScope to Executor
fun CoroutineScope.asExecutor(): Executor = Executor { command ->
    launch { command.run() }
}

private fun getCurrentLocation(
    locationManager: LocationManager,
    executor: Executor,
    logger: LogStore,
    onLocationReceived: (Location) -> Unit,
    onError: (String) -> Unit
) {
    logger.log("Starting getCurrentLocation")
    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

    fun tryNextProvider(index: Int) {
        if (index >= providers.size) {
            logger.error("No location provider available")
            onError("No location provider available")
            return
        }

        val provider = providers[index]
        logger.log("Trying provider: $provider")
        if (locationManager.isProviderEnabled(provider)) {
            logger.log("Provider $provider is enabled, requesting location")
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // For API 30 and above
                    locationManager.getCurrentLocation(
                        provider,
                        null,
                        executor
                    ) { location ->
                        if (location != null) {
                            logger.log("Location received from $provider")
                            onLocationReceived(location)
                        } else {
                            logger.error("Null location received from $provider")
                            tryNextProvider(index + 1)
                        }
                    }
                } else {
                    // For API 21-29
                    val locationListener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            logger.log("Location received from $provider")
                            locationManager.removeUpdates(this)
                            onLocationReceived(location)
                        }

                        override fun onProviderDisabled(provider: String) {
                            logger.error("Provider disabled: $provider")
                            locationManager.removeUpdates(this)
                            tryNextProvider(index + 1)
                        }

                        override fun onProviderEnabled(provider: String) {
                            logger.log("Provider enabled: $provider")
                        }

                        @Deprecated("Deprecated in Java",
                            ReplaceWith("logger.log(\"Provider status changed: \$provider, status: \$status\")")
                        )
                        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                            logger.log("Provider status changed: $provider, status: $status")
                        }
                    }

                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(provider, locationListener, Looper.getMainLooper())

                    // Set a timeout for older versions
                    executor.execute {
                        Thread.sleep(60000) // 60 seconds timeout
                        locationManager.removeUpdates(locationListener)
                        logger.error("Location request timed out for $provider")
                        tryNextProvider(index + 1)
                    }
                }
            } catch (e: SecurityException) {
                logger.error("Security exception for $provider: ${e.message}")
                onError("Permission denied for $provider: ${e.message}")
            } catch (e: Exception) {
                logger.error("Error getting location from $provider: ${e.message}")
                tryNextProvider(index + 1)
            }
        } else {
            logger.error("Provider disabled: $provider")
            tryNextProvider(index + 1)
        }
    }

    tryNextProvider(0)
}


private fun isHighAccuracyLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

private fun openLocationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

fun isValidLatitude(value: String): Boolean = value.toDoubleOrNull()?.let { it in -90.0..90.0 } ?: false

fun isValidLongitude(value: String): Boolean = value.toDoubleOrNull()?.let { it in -180.0..180.0 } ?: false
