package org.weather.app.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.weather.app.data.model.WeatherResponse

@Composable
fun WeatherScreen(data: WeatherResponse?, cityName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$cityName Weather",
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "${data?.current_weather?.temperature} °",
            fontSize = 72.sp,
            color = Color.White,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Wind: ${data?.current_weather?.windspeed} km/h",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}