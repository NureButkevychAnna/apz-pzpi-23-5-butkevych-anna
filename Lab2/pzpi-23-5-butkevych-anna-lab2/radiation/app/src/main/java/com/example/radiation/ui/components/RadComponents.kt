package com.example.radiation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.radiation.R
import com.example.radiation.ui.theme.*

@Composable
fun RadSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        content = content,
    )
}

@Composable
fun AlertLevelBadge(level: String?) {
    val (bg, fg, label) = when (level.normalizeAlertLevel()) {
        "warning"  -> Triple(WarningBg,  WarningAmber,       stringResource(R.string.alert_level_warning))
        "danger"   -> Triple(DangerBg,   DangerRed,          stringResource(R.string.alert_level_danger))
        "critical" -> Triple(CriticalBg, CriticalPurpleLight,stringResource(R.string.alert_level_critical))
        else       -> Triple(WarningBg,  WarningAmber,       stringResource(R.string.alert_level_warning))
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(fg)
        )
        Text(text = label, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
    }
}

fun alertBorderColor(level: String?): Color = when (level.normalizeAlertLevel()) {
    "warning"  -> Color(0xFF3D2E00)
    "danger"   -> Color(0xFF3D1515)
    "critical" -> Color(0xFF2D1555)
    else       -> Color(0xFF3D2E00)
}

private fun String?.normalizeAlertLevel(): String = this?.lowercase().orEmpty()

@Composable
fun RadTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = OnSurfaceDisabled) },
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = OnBackground,
            unfocusedTextColor = OnBackground,
            focusedBorderColor = PrimaryBlueLight,
            unfocusedBorderColor = BorderColor,
            cursorColor = PrimaryBlueLight,
            focusedLabelColor = PrimaryBlueLight,
            unfocusedLabelColor = OnSurfaceDisabled,
            focusedContainerColor = SurfaceVariant,
            unfocusedContainerColor = SurfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
    )
}

@Composable
fun RadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = PrimaryBlueLight,
            disabledContainerColor = SurfaceVariant,
            disabledContentColor = OnSurfaceDisabled,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 0.5.sp)
    }
}

@Composable
fun RadOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlueLight),
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun RadScaffold(
    topBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        topBar = topBar,
        snackbarHost = snackbarHost,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back), tint = OnBackground)
            }
        }
        Text(
            text = title,
            color = PrimaryBlueLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        actions()
    }
}