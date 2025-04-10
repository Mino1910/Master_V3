package com.example.master_v3.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.master_v3.util.Utils
import com.google.ar.core.Anchor.TerrainAnchorState
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


@Composable
fun ARScreen() {
    val context = LocalContext.current // Context für Toast
    Box(modifier = Modifier.fillMaxSize()) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine = engine)
        val materialLoader = rememberMaterialLoader(engine = engine)
        val cameraNode = rememberARCameraNode(engine = engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine = engine)
        val collisionSystem = rememberCollisionSystem(view = view)
        val planeRenderer = remember { mutableStateOf(true) }
        val modelInstance = remember { mutableListOf<ModelInstance>() }
        val trackingFailureReason = remember { mutableStateOf<TrackingFailureReason?>(null) }
        val frame = remember { mutableStateOf<Frame?>(null) }

        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            planeRenderer = planeRenderer.value,
            cameraNode = cameraNode,
            materialLoader = materialLoader,
            onTrackingFailureChanged = {
                trackingFailureReason.value = it
            },
            onSessionUpdated = { session, updatedFrame ->
                frame.value = updatedFrame
                if (childNodes.isEmpty()) {
                    val earth = session.earth

                    Toast.makeText(context, "Tracking State: ${earth?.trackingState}", Toast.LENGTH_SHORT).show()

                    val trackingFailure = trackingFailureReason.value
                    if (trackingFailure != null) {
                        Toast.makeText(context, "Tracking State: ${trackingFailure}", Toast.LENGTH_SHORT).show()
                    }

                    // Wenn das Tracking erfolgreich ist
                    if (earth?.trackingState == TrackingState.TRACKING) {
                        val cameraPose = earth.cameraGeospatialPose

                        val userLat = cameraPose.latitude
                        val userLng = cameraPose.longitude

                        val targetLat = 47.079725
                        val targetLng = 15.451217
                        val targetAlt = 0.0 // Verwende denselben Wert für Höhe

                        // Berechne die Entfernung zwischen Benutzer und Modell
                        val distance = haversine(userLat, userLng, targetLat, targetLng)

                        // Zeige eine Toast-Nachricht, wenn der Benutzer nah am Modell ist
                        Toast.makeText(context, "Du bist $distance Meter vom Modell entfernt", Toast.LENGTH_SHORT).show()

                        // Modell platzieren, wenn noch nicht vorhanden und der Benutzer nahe genug ist
//                        if (childNodes.isEmpty()) {
//                            // Verwende resolveAnchorOnTerrainAsync, um den TerrainAnchor zu erstellen
//                            earth.resolveAnchorOnTerrainAsync(
//                                targetLat, targetLng, targetAlt, 0f, 0f, 0f, 1f,
//                                { anchor, state ->
//                                    if (state == TerrainAnchorState.SUCCESS) {
//                                        // Wenn der Anchor erfolgreich platziert wurde, erstelle den Node
//                                        childNodes += Utils.createAnchorNode(
//                                            engine = engine,
//                                            anchor = anchor,
//                                            modelLoader = modelLoader,
//                                            materialLoader = materialLoader,
//                                            modelInstance = modelInstance,
//                                            model = "models/chicken.glb"
//                                        )
//                                        Toast.makeText(context, "Anker platziert", Toast.LENGTH_SHORT).show()
//                                    } else {
//                                        // Fehlerbehandlung, falls der Anchor nicht platziert werden konnte
//                                        Toast.makeText(context, "Fehler beim Platzieren des Ankers", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            )
//                        }
                    }
                }
            },
            sessionConfiguration = { session, config ->
                config.geospatialMode = Config.GeospatialMode.ENABLED
                config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }
        )
    }
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0 // Erdradius in Meter
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}