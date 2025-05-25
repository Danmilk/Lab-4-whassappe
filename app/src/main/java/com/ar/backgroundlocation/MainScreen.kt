package com.ar.backgroundlocation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices

@Composable
fun App() {
    val context = LocalContext.current

    // Estado para el mensaje de WhatsApp
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón para iniciar el servicio de ubicación
        Button(onClick = {
            Toast.makeText(context, "Service Start button clicked", Toast.LENGTH_SHORT).show()
            Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_SERVICE_START
                context.startService(this)
            }
        }) {
            Text(text = "Start Service")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón para detener el servicio
        Button(onClick = {
            Toast.makeText(context, "Service Stop button clicked", Toast.LENGTH_SHORT).show()
            Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_SERVICE_STOP
                context.startService(this)
            }
        }) {
            Text(text = "Stop Service")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Caja de texto para escribir el mensaje
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Mensaje para WhatsApp") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para enviar mensaje + ubicación
        Button(
            onClick = {
                // Obtener última ubicación conocida
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                fusedClient.lastLocation
                    .addOnSuccessListener { loc ->
                        val lat = loc?.latitude ?: 0.0
                        val lon = loc?.longitude ?: 0.0
                        // Construir texto con Google Maps link
                        val textToSend = buildString {
                            append(message)
                            append("\nMi ubicación: https://maps.google.com/?q=$lat,$lon")
                        }
                        // Lanzar WhatsApp con el texto
                        val uri = "https://api.whatsapp.com/send?text=${Uri.encode(textToSend)}"
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    .addOnFailureListener {
                        Toast
                            .makeText(context, "No se pudo obtener ubicación", Toast.LENGTH_SHORT)
                            .show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enviar mensaje y ubicación")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    App()
}
