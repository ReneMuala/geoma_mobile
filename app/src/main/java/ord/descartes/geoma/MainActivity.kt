package ord.descartes.geoma

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ord.descartes.geoma.SceneDrawer.renderScene
import ord.descartes.geoma.engine.IncrementalTimeoutCounter
import ord.descartes.geoma.engine.Marker
import ord.descartes.geoma.engine.Model
import ord.descartes.geoma.engine.Navigator
import ord.descartes.geoma.engine.OSMEntityClass
import ord.descartes.geoma.engine.Path
import ord.descartes.geoma.engine.Point
import ord.descartes.geoma.engine.Region
import ord.descartes.geoma.engine.Scene
import ord.descartes.geoma.engine.Target
import ord.descartes.geoma.ui.theme.GeomaTheme
import java.time.Duration
import android.Manifest
import android.os.Looper
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContent {
            GeomaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        ctx = applicationContext,
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        sensorManager = sensorManager
                    )
                }
            }
        }
    }
}


object SceneDrawer {
    fun DrawScope.renderScene(scene: Scene, azimuth: Float, textMeasurer: TextMeasurer) {
        scene.entities.forEach {
            when (it) {
                is Region -> {
                    val baseColor = Color(255f / 255f, 69f / 255f, 0f);
                    val (color, width) = Pair(baseColor.copy(alpha = 0.5f), 1f)
                    for (i in 0..<it.points.size - 1) {
                        val start = it.points[i];
                        val end = it.points[i + 1];
                        drawLine(
                            color,
                            Offset(start.x.toFloat(), start.y.toFloat()),
                            Offset(end.x.toFloat(), end.y.toFloat()),
                            strokeWidth = width
                        )
                    }

                    val topLeft = it.topLeft
                    val bottomRight = it.bottomRight
                    val origin = topLeft.plus(bottomRight.minus(topLeft).divide(2.0))
                    val textLayoutResult = textMeasurer.measure(
                        text = it.name,
                        style = TextStyle(
                            fontSize = 2.sp,
                            fontWeight = FontWeight.Bold,
                            color = baseColor,
                        )
                    )

                    rotate(
                        degrees = azimuth,
                        pivot = Offset(origin.x.toFloat(), origin.y.toFloat())
                    ) {
                        drawText(
                            textLayoutResult = textLayoutResult,
//                            text = textToDraw,
                            topLeft = Offset(
                                x = origin.x.toFloat() - textLayoutResult.size.width / 2,
                                y = origin.y.toFloat() - textLayoutResult.size.height / 2
                            )
                        )
                    }
//                    println("Region")
                }

                is Path -> {
                    val (color, width) = if (it.isSolution) {
                        Pair(Color.Blue, 1f)
                    } else {
                        Pair(Color.LightGray.copy(alpha = 0.25f), 5f)
                    }
                    for (i in 0..<it.points.size - 1) {
                        val start = it.points[i];
                        val end = it.points[i + 1];
                        drawLine(
                            color,
                            Offset(start.x.toFloat(), start.y.toFloat()),
                            Offset(end.x.toFloat(), end.y.toFloat()),
                            strokeWidth = width
                        )
                    }
//                    println("path")
                }

                is Target -> {
                    val size1 = 4f
                    val size2 = 16f;
                    val baseColor = Color.Red
                    val accentColor = baseColor.copy(alpha = 100f / 255f)
                    drawCircle(
                        color = accentColor,
                        radius = size2,
                        center = Offset(it.origin.x.toFloat(), it.origin.y.toFloat()),
                    )
                    drawCircle(
                        color = baseColor,
                        radius = size1,
                        center = Offset(it.origin.x.toFloat(), it.origin.y.toFloat()),
                    )
//                    println("target")
                }

                is Marker -> {
                    val size1 = 4f
                    val size2 = 8f;
                    val baseColor = Color.Blue
                    val accentColor = baseColor.copy(alpha = 100f / 255f)
                    rotate(
                        degrees = azimuth,
                        pivot = Offset(it.origin.x.toFloat(), it.origin.y.toFloat())
                    ) {
                        drawRoundRect(
                            color = accentColor,
                            topLeft = Offset(
                                it.origin.x.toFloat() - size2 / 2,
                                it.origin.y.toFloat() - size2 / 2
                            ),
                            size = Size(size2, size1),
                            cornerRadius = CornerRadius(1f, 1f)
                        )
                        drawRoundRect(
                            color = baseColor,
                            topLeft = Offset(
                                it.origin.x.toFloat() - size2 / 2,
                                it.origin.y.toFloat() - size2 / 2 + size1
                            ),
                            size = Size(size2, size2),
                            cornerRadius = CornerRadius(1f, 1f)
                        )
                    }
//                    println("marker")
                }
            }
        }
    }
}

//object SceneCalculator {
//    val scene = Scene
//}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("NewApi", "MissingPermission")
@Composable
fun Greeting(
    ctx: Context,
    name: String,
    modifier: Modifier = Modifier,
    sensorManager: SensorManager
) {
    val textMeasurer = rememberTextMeasurer()
    var azimuth by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(10f) }
    var offsetX by remember { mutableFloatStateOf(407.42383f) }
    var offsetY by remember { mutableFloatStateOf(888.64746f) }
    val scene by remember { mutableStateOf(Scene()) }
    val zoom = 100000.0
    val marker by remember {
        mutableStateOf(
            Marker(
                Point(
                    -19.821568114779822,
                    34.83864086205174
                ).minus(Point(-19.8292906, 34.8313808)).times(zoom).abs()
            )
        )
    }
    var model by remember {
        mutableStateOf<Model?>(null)
    }
    var wasSceneComputed by remember { mutableStateOf(false) }

    // Configurar o listener do sensor
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    val orientationAngles = FloatArray(3)

                    // Converter o vetor de rotação para uma matriz
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    // Calcular os ângulos de orientação
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    azimuth += 45 + 5
                    if (azimuth < 0) azimuth += 360
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }
    // Registra o listener e limpa quando o Composable é descartado
    DisposableEffect(Unit) {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(
            sensorEventListener,
            rotationSensor,
            SensorManager.SENSOR_DELAY_UI
        )
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    if (!wasSceneComputed) {
        model = computeSceneReturningNavigator(ctx, scene, marker, zoom, model)
        offsetX = marker.origin.x.toFloat()
        offsetY = marker.origin.y.toFloat()
        wasSceneComputed = true
    }

    val context = LocalContext.current
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationState = remember { mutableStateOf<Location?>(null) }
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    DisposableEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10L // Intervalo para tempo
            ).apply {
                setMinUpdateIntervalMillis(100L) // Atualizacoes
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationState.value = locationResult.lastLocation
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            onDispose {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }

        onDispose { /* No-op if permission is not granted */ }
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect("permission") {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }

        coroutineScope.launch {
            while (true) {
                delay(5000)
                wasSceneComputed = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.cancel()
        }
    }

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                    }
                }
        ) {

            locationState.value?.let { location ->
                marker.origin =
                    Point(
                        location.latitude,
                        location.longitude
                    ).minus(Point(-19.8292906, 34.8313808)).times(zoom).abs()
                offsetX = marker.origin.x.toFloat()
                offsetY = marker.origin.y.toFloat()
            }

            translate(left = center.x - offsetX, top = center.y - offsetY + 300) {
                rotate(degrees = -azimuth, pivot = Offset(offsetX, offsetY)) {
                    scale(scale = scale, pivot = Offset(offsetX, offsetY)) {
                        renderScene(scene, azimuth, textMeasurer)
                    }
                }
            }
        }
    }
}

private fun loadScene(scene: Scene, ctx: Context, zoom: Double) {
    scene.entities.clear()
    try {
        val assets = ctx.assets.open("10012025_10021.xml")
        val pathCount = scene.addOSM(assets, {
            if (it.containsKey("name")) {
                println(it["name"]!!)
            }
            if (it.containsKey("name") && it["name"]!!.matches(Regex("Route.*"))) {
                OSMEntityClass.Path;
            } else if (it.containsKey("name") && (
                        it["name"]!!.matches(Regex("[A-Z].")) ||
                                it["name"]!!.matches(Regex("Bloco.*")) ||
                                    it["name"]!!.matches(Regex("Armaz.*")) ||
                                        it["name"]!!.matches(Regex("mar.*")))) {
                OSMEntityClass.Region;
            } else {
                OSMEntityClass.Undefined
            }
        }, 0.0, 0.0, zoom);
        Log.d("READER", "$pathCount paths readed")
//        scene.printEntities()
    } catch (e: Exception) {
        Log.e("READER", e.message.toString())
    }
}

@SuppressLint("NewApi")
private fun computeSceneReturningNavigator(
    ctx: Context,
    scene: Scene,
    marker: Marker,
    zoom: Double,
    _model: Model?
): Model? {
    var model = _model
    val target = Target(
        Point(
            -19.8189762,
            34.8351143
        ).minus(Point(-19.8292906, 34.8313808)).times(zoom).abs()
    )
    if (scene.entities.isEmpty()) {
        loadScene(scene, ctx, zoom)
        scene.addEntity(target)
        scene.addEntity(marker)
    } else {
        scene.entities.removeIf {
            it is Path && it.isSolution
        }
    }

    if (model == null) {
        model = Model(scene.paths, scene.obstacles)
    }
    val navigator = Navigator(marker, target, model)
    val results = navigator.navigate(IncrementalTimeoutCounter(3, Duration.ofSeconds(2)))
    if (results.isNotEmpty()) {
        val solutionPath = results.first()
        solutionPath.isSolution = true
        scene.addEntity(solutionPath)
    }
    return model
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeomaTheme {
//        Greeting("Android")
    }
}