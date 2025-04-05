package com.example.master_v3.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.master_v3.util.Utils
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView


@Composable
fun ARScreen() {

    Box(modifier = Modifier.fillMaxSize()) {

        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine = engine)
        val materialLoader = rememberMaterialLoader(engine = engine)
        val cameraNode = rememberARCameraNode(engine = engine)
        val childNodes = rememberNodes()
        val view = rememberView(engine = engine)
        val collisionSystem = rememberCollisionSystem(view = view)
        val planeRenderer = remember {
            mutableStateOf(true)
        }
        val modelInstance = remember {
            mutableListOf<ModelInstance>()
        }
        val trackingFailureReason = remember {
            mutableStateOf<TrackingFailureReason?>(null)
        }
        val frame = remember {
            mutableStateOf<Frame?>(null)
        }

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
                val earth = session.earth
                Log.d("hilfe", "Earth TrackingState: ${earth?.trackingState}")
//                if (childNodes.isEmpty()) {
//                    val currentFrame = frame.value
//                    val earth = currentFrame?.session?.earth
//
//                    if (earth?.trackingState == TrackingState.TRACKING) {
//                        // Zielkoordinaten (z.B. München Zentrum)
//                        val latitude = 48.1351
//                        val longitude = 11.5820
//                        val altitude = earth.cameraGeospatialPose.altitude - 1.5 // etwas unter Kamerahöhe
//
//                        // Erzeuge einen Geospatial Anchor an den GPS-Koordinaten
//                        val anchor = earth.createAnchor(
//                            latitude,
//                            longitude,
//                            altitude,
//                            0f, 0f, 0f, 1f // Rotation als Quaternion
//                        )
//
//                        val anchorNode = Utils.createAnchorNode(
//                            engine = engine,
//                            anchor = anchor,
//                            modelLoader = modelLoader,
//                            materialLoader = materialLoader,
//                            modelInstance = modelInstance,
//                            model = "models/chicken.glb"
//                        )
//                        childNodes += anchorNode
//                    }
//                }


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