package org.weather.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.jordond.compass.Coordinates
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.mobile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.weather.app.data.model.WeatherResponse
import org.weather.app.presentation.WeatherScreen
import org.weather.app.presentation.WeatherViewModel
import weatherappkmm.composeapp.generated.resources.Res
import weatherappkmm.composeapp.generated.resources.bg_img
import weatherappkmm.composeapp.generated.resources.house

@Composable
@Preview
fun App() {
    val viewModel = WeatherViewModel()
    val weatherState by viewModel.weatherState.collectAsState()
    val geoLocation = remember { Geolocator.mobile() }
    var currentLocation by remember {
        mutableStateOf<Coordinates?>(
            Coordinates(
                viewModel.currentLocation.first,
                viewModel.currentLocation.second
            )
        )
    }
    LaunchedEffect(Unit) {
        val result = geoLocation.current()
        when (result) {
            is GeolocatorResult.Success -> {
                try {
                    currentLocation = result.data.coordinates
                    println("LOCATION: ${result.data.coordinates}")
                } catch (e: Exception) {
                    print("Exception ${e.message}")
                }
            }

            is GeolocatorResult.Error -> when (result) {
                is GeolocatorResult.NotSupported -> println("LOCATION ERROR: ${result.message}")
                is GeolocatorResult.NotFound -> println("LOCATION ERROR: ${result.message}")
                is GeolocatorResult.PermissionError -> println("LOCATION ERROR: ${result.message}")
                is GeolocatorResult.GeolocationFailed -> println("LOCATION ERROR: ${result.message}")
                else -> println("LOCATION ERROR: ${result.message}")
            }
        }
    }
    LaunchedEffect(currentLocation) {
        if (currentLocation?.longitude != null && currentLocation?.latitude != null && currentLocation!!.longitude != 0.0 && currentLocation!!.latitude != 0.0) {
            viewModel.fetchWeatherIfNeeded(
                currentLocation?.latitude ?: 0.0,
                currentLocation?.longitude ?: 0.0
            )
        }
    }
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A237E))
        ) {
            Image(
                painterResource(Res.drawable.bg_img),
                contentDescription = "Night Sky",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (currentLocation?.longitude != null && currentLocation?.latitude != null && currentLocation!!.longitude != 0.0 && currentLocation!!.latitude != 0.0) {
                AnimatedVisibility(true) {
                    ShowContent(weatherState, viewModel.cityName)
                }

            }
            Image(
                painter = painterResource(Res.drawable.house),
                contentDescription = "House",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(220.dp)
            )
        }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {


            }
        }
    }
}

@Composable
fun ShowContent(state: Result<WeatherResponse>?, cityName: String) {

    when {
        state == null -> Text("Loading...")
        state.isSuccess -> {
            val data = state.getOrNull()

            data?.let {
                WeatherScreen(data, cityName)
            }
        }

        state.isFailure -> {
            Text("Error: ${state.exceptionOrNull()?.message}")
        }
    }
}


