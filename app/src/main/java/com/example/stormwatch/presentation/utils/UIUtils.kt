package com.example.stormwatch.presentation.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.stormwatch.data.model.DayForecast
import com.example.stormwatch.ui.theme.GlassBorder
import com.example.stormwatch.ui.theme.GlassDark
import com.example.stormwatch.ui.theme.OnGlass
import com.example.stormwatch.ui.theme.SubtleOnGlass
import kotlin.math.roundToInt

@Composable
fun MetricItem(value: String, label: String, icon: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Image(painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(OnGlass)
        )

        Text(value, color = OnGlass, fontWeight = FontWeight.SemiBold)
        Text(label, color = SubtleOnGlass)
    }
}


@Composable
fun DayTab(day: DayForecast) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                day.date.takeLast(5),
                color = OnGlass,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                "${day.minTemp.roundToInt()}° / ${day.maxTemp.roundToInt()}°",
                color = SubtleOnGlass,
                fontSize = 14.sp
            )
        }

        val iconUrl = "https://openweathermap.org/img/wn/${day.icon}@2x.png"
        AsyncImage(
            model = iconUrl,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}


@Composable
fun HourChip(hour: String, desc: String, icon: String) {
    val iconUrl = "https://openweathermap.org/img/wn/$icon@2x.png"

    MyCard(
        modifier = Modifier.width(90.dp),
        containerColor = GlassDark
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
            Text(hour, color = OnGlass)

            AsyncImage(
                model = iconUrl,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Text("$desc ", color = SubtleOnGlass, textAlign = TextAlign.Center )
        }
    }
}


@Composable
fun MyCard(
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

