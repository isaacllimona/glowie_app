package glowie.glowie_app.scriptEditor.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import glowie.glowie_app.common.content_copy
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ColorPickerDialog(
    uiState: ColorPickerDialogUIState,
) {
    LaunchedEffect(uiState.runColorLivePreview) {
        uiState.runColorLivePreview()
    }

    AlertDialog(
        onDismissRequest = uiState.onDismiss,
        title = { Text("Color Picker") },
        text = {
            ColorPicker(
                hsv = uiState.hsv,
                copiableStringHSV = uiState.copiableColorStringHSV,
                copiableStringRGB = uiState.copiableColorStringRGB,
                onColorChange = uiState.onColorChange,
            )
        },
        confirmButton = {},
    )
}

@Composable
fun ColorPicker(
    hsv: HSV,
    copiableStringHSV: String,
    copiableStringRGB: String,

    onColorChange: (HSV) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val hue = hsv.hue
        val saturation = hsv.saturation
        val value = hsv.value

        HueSaturationWheel(
            hue = hue,
            saturation = saturation,
            value = value,
            onColorChange = onColorChange,
            modifier = Modifier.size(240.dp),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Brightness",
            style = MaterialTheme.typography.bodyMedium,
        )

        Slider(
            value = value,
            onValueChange = { onColorChange(HSV(hue, saturation, it)) },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            thumb = {
                SliderDefaults.Thumb(
                    thumbSize = DpSize(24.dp, 24.dp),
                    interactionSource = remember { MutableInteractionSource() },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.hsv(hue, saturation, 1f, 1f)
                    ),
                )
            },
        )

        Spacer(Modifier.height(12.dp))

        CopiableString(copiableStringHSV)
        CopiableString(copiableStringRGB)
    }
}

@Composable
private fun CopiableString(
    string: String
) {
    val clipboard = LocalClipboardManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = string,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = {
                clipboard.setText(AnnotatedString(string))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
            contentPadding = ButtonDefaults.ContentPadding,
        ) {
            Icon(
                imageVector = content_copy,
                contentDescription = "Copy",
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun HueSaturationWheel(
    hue: Float,         // from 0-360
    saturation: Float,  // from 0-1
    value: Float,       // from 0-1
    onColorChange: (HSV) -> Unit,
    modifier: Modifier = Modifier,
) {
    var wheelSize by remember { mutableStateOf(IntSize.Zero) }

    Canvas(
        modifier = modifier
            .pointerInput(value) {  // recompose on value changes, otherwise it keeps the old lambda with the old value
                detectDragGestures { change, _ ->
                    change.consume()
                    val centerX = wheelSize.width / 2f
                    val centerY = wheelSize.height / 2f
                    val radius = min(centerX, centerY)

                    val dx = change.position.x - centerX
                    val dy = change.position.y - centerY
                    val distance = sqrt(dx * dx + dy * dy)

                    val angle = atan2(dy, dx)
                    val hueDeg = ((Math.toDegrees(angle.toDouble()) + 360) % 360).toFloat()
                    val sat = (distance / radius).coerceIn(0f, 1f)

                    onColorChange(HSV(hueDeg, sat, value))
                }
            }
    ) {
        wheelSize = IntSize(size.width.toInt(), size.height.toInt())
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = min(centerX, centerY)

        // Draw hue wheel
        for (angle in 0..359) {
            val startAngle = angle.toFloat()
            val color = Color.hsv(angle.toFloat(), 1f, 1f)
            drawArc(
                color = color,
                startAngle = startAngle - 0.5f,
                sweepAngle = 2f,
                useCenter = true,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            )
        }

        // Draw saturation overlay (white center to transparent edge)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color.White.copy(alpha = 0f)),
                center = Offset(centerX, centerY),
                radius = radius,
            ),
            radius = radius,
            center = Offset(centerX, centerY),
        )

        // Draw current position indicator
        val indicatorAngle = Math.toRadians(hue.toDouble())
        val indicatorDistance = saturation * radius
        val indicatorX = centerX + (indicatorDistance * cos(indicatorAngle)).toFloat()
        val indicatorY = centerY + (indicatorDistance * sin(indicatorAngle)).toFloat()

        drawCircle(
            color = Color.Black,
            radius = 8f,
            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 3f),
        )
        drawCircle(
            color = Color.White,
            radius = 8f,
            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 1.5f),
        )
    }
}
