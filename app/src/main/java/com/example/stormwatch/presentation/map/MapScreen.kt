package com.example.stormwatch.presentation.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.stormwatch.data.model.GeoCodingDto

@Composable
fun MapScreen(navController: NavHostController, onLocationSelected: (GeoCodingDto) -> Unit) {

    val mapViewModel: MapViewModel = viewModel(factory =MapViewModelFactory() )
    val results by mapViewModel.results.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(31.5041873, 31.8368546),
            10f
        )
    }

    Column {

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                mapViewModel.searchCity(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            label = { Text("Search city") }
        )

        LazyColumn {
            items(results) { city ->
                Text(
                    text = "${city.name}, ${city.country}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedLatLng = LatLng(city.lat, city.lon)
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(selectedLatLng!!, 10f)

                            searchText = "${city.name}, ${city.country}"
                        }.padding(12.dp)
                )
            }
        }

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
                            mapViewModel.reverseGeocodeAndSelect(
                                it.latitude,
                                it.longitude,
                                onLocationSelected
                            )
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
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}