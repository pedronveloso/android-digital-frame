package com.pedronveloso.digitalframe.ui.preferences

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
import java.util.concurrent.Executor

@SuppressLint("MissingPermission")
@Composable
fun LocationPreferenceComposable(preference: PreferenceItem.LocationPref) {
    val logger = LogStoreProvider.getLogStore()
    val locationManager =
        LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val initialLocation = preference.initialValueProvider.invoke()
    var latitude by remember { mutableStateOf(initialLocation.latitude.toString()) }
    var longitude by remember { mutableStateOf(initialLocation.longitude.toString()) }
    var latError by remember { mutableStateOf(false) }
    var lonError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val executor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        LocalContext.current.mainExecutor
    } else {
        ContextCompat.getMainExecutor(context)
    }

    // Permission handling
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation(locationManager, executor, onLocationReceived = { location ->
                    logger.log("Location received - perm granted: $location")
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    isLoading = false
                })
            }
        }
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
            },
            isError = latError,
            label = { stringResource(id = R.string.pref_location_lat) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (latError) {
            Text(
                stringResource(id = R.string.pref_location_lat_error),
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = longitude,
            onValueChange = {
                longitude = it
                lonError = !isValidLongitude(it)
            },
            isError = lonError,
            label = { Text(stringResource(id = R.string.pref_location_lon)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (lonError) {
            Text(
                stringResource(id = R.string.pref_location_lon_error),
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            SaveButton(
                onSave = {
                    if (!latError && !lonError) {
                        logger.log("Location saved: $latitude, $longitude")
                        preference.onChangeCallback?.invoke(
                            LocationData(
                                latitude.toDouble(),
                                longitude.toDouble()
                            )
                        )
                    }
                },
                enabled = !latError && !lonError,
                buttonText = stringResource(id = R.string.pref_location_save)
            )

            Spacer(modifier = Modifier
                .height(8.dp)
                .width(8.dp))

            GetCurrentLocationButton(
                isLoading = isLoading,
                onGetCurrent = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) -> {
                            logger.log("Getting current location")
                            isLoading = true
                            getCurrentLocation(
                                locationManager,
                                executor,
                                onLocationReceived = { location ->
                                    logger.log("Location received: $location")
                                    latitude = location.latitude.toString()
                                    longitude = location.longitude.toString()
                                    isLoading = false
                                })
                        }

                        else -> locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            )
        }
    }
}

@Composable
fun GetCurrentLocationButton(
    isLoading: Boolean,
    onGetCurrent: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onGetCurrent, modifier = modifier) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
        } else {
            Text("Get current")
        }
    }
}


@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    locationManager: LocationManager,
    executor: Executor,
    onLocationReceived: (Location) -> Unit
) {
    var locationListener: LocationListener? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        locationManager.getCurrentLocation(
            LocationManager.GPS_PROVIDER,
            null,
            executor
        ) { location ->
            location?.let {
                onLocationReceived(it)
            }
        }
    } else {
        locationListener = LocationListener { location ->
            onLocationReceived(location)
            locationListener?.let { locationManager.removeUpdates(it) }
        }


        locationManager.requestSingleUpdate(
            LocationManager.GPS_PROVIDER,
            locationListener,
            Looper.getMainLooper()
        )

    }
}

fun isValidLatitude(value: String): Boolean =
    value.toDoubleOrNull()?.let { it in -90.0..90.0 } ?: false

fun isValidLongitude(value: String): Boolean =
    value.toDoubleOrNull()?.let { it in -180.0..180.0 } ?: false
