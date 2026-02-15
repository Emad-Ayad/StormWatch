package com.example.stormwatch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.stormwatch.ui.theme.*






@Composable
fun HomeScreen(navController: NavHostController){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Image(
            painter = painterResource(id = R.drawable.weather_background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Welcome To Your Storm Watch 👋",
                color = SubtleOnGlass,
            )

            CurrentWeatherCard()
            MetricsRow()
            HourlyStrip()
            ForecastTabs()


        }
    }
}

@Composable
private fun MyCard(
    modifier: Modifier = Modifier,
    containerColor: Color = GlassDark,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = modifier.drawBehind {
            drawRoundRect(
                color = GlassBorder,
                size = size,
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
        },
        colors = CardDefaults.cardColors(containerColor),
        shape =  RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(padding), content = content)
    }
}

@Composable
private fun CurrentWeatherCard() {
    MyCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(Modifier.weight(1f)) {
                Text("Cairo", color = OnGlass, fontWeight = FontWeight.SemiBold)

                Text("18.26.07", color = OnGlass,fontSize = 18.sp,
                    fontWeight = FontWeight.Medium)

                Text("24 January 2026",
                    color = SubtleOnGlass,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium)

                Spacer(Modifier.height(12.dp))

                Text("22°", color = OnGlass,fontSize = 38.sp,
                    fontWeight = FontWeight.Medium)

                Text("Stormy", color = AccentYellow, fontWeight = FontWeight.Medium)
            }


            Box(
                modifier = Modifier.height(120.dp).width(170.dp),
                contentAlignment = Alignment.Center
            ) {
               Image(
                    painter = painterResource(id = R.drawable.defult_weather),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun MetricsRow() {
    MyCard(containerColor = GlassDarkStrong) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricItem("20 km/h", "Wind",R.drawable.wind)
            MetricItem("22%", "Humidity",R.drawable.humidity)
            MetricItem("94%", "Rain",R.drawable.rain)
            MetricItem("1015 hPa", "Pressure",R.drawable.pressure)
        }
    }
}

@Composable
private fun MetricItem(value: String, label: String, icon: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(icon), contentDescription = null,modifier = Modifier.size(30.dp))
        Text(value, color = OnGlass, fontWeight = FontWeight.SemiBold)
        Text(label, color = SubtleOnGlass)
    }
}

@Composable
private fun ForecastTabs() {
    MyCard(containerColor = GlassDarkStrong, padding = PaddingValues(0.dp)) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Today", color = OnGlass, fontWeight = FontWeight.Bold)
            Text("Tomorrow", color = SubtleOnGlass)
            Text("17/8", color = SubtleOnGlass)
        }
    }
}

@Composable
private fun HourlyStrip() {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HourChip("2 AM", "Clear", "01n")
        HourChip("3 AM", "Clear", "01n")
        HourChip("4 AM", "Clear", "01n")
        HourChip("5 AM", "Clear", "01n")
        HourChip("6 AM", "Clear", "01d")
        HourChip("7 AM", "Clear", "01d")
    }
}

@Composable
private fun HourChip(hour: String, desc: String, icon: String) {
    MyCard(
        modifier = Modifier.width(90.dp),
        containerColor = GlassDark
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
            Text(hour, color = OnGlass)

            Image(
                painter = painterResource(id = R.drawable.defult_weather),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Text(desc, color = SubtleOnGlass)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewHome() {
    val navController = rememberNavController()
    HomeScreen(navController)
}
