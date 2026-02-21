package com.example.stormwatch.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val settingsViewModel : SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(31.5041873, 31.8368546),
            10f
        )
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    Box(Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLatLng = latLng
            }
        ) {
            selectedLatLng?.let {
                Marker(state = MarkerState(position = it))
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {

            Button(
                onClick = {
                    selectedLatLng?.let {
                        settingsViewModel.saveMapLocation(it.latitude, it.longitude)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                enabled = selectedLatLng != null
            ) {
                Text("Use this location")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                Text("Cancel")
            }
        }
    }
}