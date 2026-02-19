package com.example.stormwatch.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stormwatch.data.settings.SettingsStore
import com.example.stormwatch.ui.theme.*

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))

    val settingsState by viewModel.settingsState.collectAsState()
    val (units, lang) = settingsState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B0420), Color(0xFF120A2D), Color(0xFF070317))
                )
            )
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {

            SettingsGroup(
                title = "Language"
            ) {
                OptionRow(
                    options = listOf(
                        Option("Arabic", "ar"),
                        Option("English", "en"),
                    ),
                    selectedValue = lang,
                    onSelect = { value ->
                        when (value) {
                            "ar" -> viewModel.updateLanguage("ar")
                            "en" -> viewModel.updateLanguage("en")
                            "default" -> viewModel.updateLanguage("default")
                        }
                    }
                )
            }

            SettingsGroup(
                title = "Temp Unit"
            ) {
                OptionRow(
                    options = listOf(
                        Option("°C/ m/s", "metric"),
                        Option("°K  m/s", "standard"),
                        Option("°F  mph", "imperial"),
                    ),
                    selectedValue = units,
                    onSelect = { value ->
                        viewModel.updateUnits(value)
                    }
                )
            }

            /*SettingsGroup(
                title = "Location"
            ) {
                OptionRow(
                    options = listOf(
                        Option("Gps", "gps"),
                        Option("Map", "map"),
                    ),
                    selectedValue = ,
                    onSelect = {  = it }
                )
            }
*/

        }
    }
}

data class Option(val label: String, val value: String)

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0x18FFFFFF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun OptionRow(
    options: List<Option>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { opt ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedValue == opt.value,
                    onClick = { onSelect(opt.value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF64E7D6),
                        unselectedColor = Color(0xB3FFFFFF)
                    )
                )
                Text(
                    text = opt.label,
                    color = Color(0xE6FFFFFF),
                    fontSize = 13.sp
                )
            }
        }
    }
}