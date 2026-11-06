package com.example

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CyberConnectButton(
  status: ConnectionStatus,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")

  // Pulse animation for rings
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.18f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scalePulse"
  )

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.25f,
    targetValue = 0.65f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alphaPulse"
  )

  // Fast Rotation for connecting state
  val rotateAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation"
  )

  // Dynamic Neon Theme Color based on status
  val activeColor by animateColorAsState(
    targetValue = when (status) {
      ConnectionStatus.DISCONNECTED -> NeonCyan
      ConnectionStatus.CONNECTING -> NeonAmber
      ConnectionStatus.CONNECTED -> NeonGreen
    },
    animationSpec = tween(500),
    label = "neonColor"
  )

  val secondaryGlowColor = when (status) {
    ConnectionStatus.DISCONNECTED -> NeonPurple
    ConnectionStatus.CONNECTING -> NeonCyan
    ConnectionStatus.CONNECTED -> NeonCyan
  }

  val isConnecting = status == ConnectionStatus.CONNECTING
  val isConnected = status == ConnectionStatus.CONNECTED

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Entire outer circle is clickable with visual touch bounds
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(230.dp)
        .clip(CircleShape)
        .clickable(
          onClick = onClick
        )
        .testTag("vpn_connect_button")
    ) {
      // Outer Ambient Glow Aura
      Canvas(
        modifier = Modifier
          .size(220.dp)
          .scale(if (isConnecting) 1.08f else pulseScale)
      ) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              activeColor.copy(alpha = if (isConnected) 0.35f else pulseAlpha * 0.45f),
              secondaryGlowColor.copy(alpha = 0.15f),
              Color.Transparent
            )
          )
        )
      }

      // Outer Concentric Ring
      Canvas(
        modifier = Modifier
          .size(200.dp)
          .scale(if (isConnected) pulseScale else 1f)
      ) {
        drawCircle(
          color = activeColor.copy(alpha = 0.5f),
          style = Stroke(width = 2.dp.toPx())
        )
      }

      // Middle Dynamic Ring (Spinning dashed arc when connecting)
      Canvas(
        modifier = Modifier
          .size(175.dp)
          .rotate(if (isConnecting) rotateAngle else 0f)
      ) {
        if (isConnecting) {
          drawArc(
            color = activeColor,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
          )
        } else {
          drawCircle(
            color = activeColor.copy(alpha = 0.7f),
            style = Stroke(width = 2.5.dp.toPx())
          )
        }
      }

      // Inner Core Button
      Box(
        modifier = Modifier
          .size(144.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                CyberSurface,
                CyberBackground
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        // Inner Neon Accent Ring
        Canvas(modifier = Modifier.size(144.dp)) {
          drawCircle(
            brush = Brush.sweepGradient(
              listOf(activeColor, secondaryGlowColor, activeColor)
            ),
            style = Stroke(width = 3.dp.toPx())
          )
        }

        // Center Icon & Status Visual
        Column(
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          when (status) {
            ConnectionStatus.DISCONNECTED -> {
              Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "اتصال",
                tint = activeColor,
                modifier = Modifier.size(46.dp)
              )
            }
            ConnectionStatus.CONNECTING -> {
              Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "جارٍ الاتصال",
                tint = activeColor,
                modifier = Modifier
                  .size(46.dp)
                  .rotate(rotateAngle)
              )
            }
            ConnectionStatus.CONNECTED -> {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "متصل",
                tint = activeColor,
                modifier = Modifier.size(46.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = when (status) {
              ConnectionStatus.DISCONNECTED -> "CONNECT"
              ConnectionStatus.CONNECTING -> "LINKING..."
              ConnectionStatus.CONNECTED -> "SECURE"
            },
            color = activeColor,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // State Subtitle Text
    Text(
      text = when (status) {
        ConnectionStatus.DISCONNECTED -> "انقر لبدء التشفير والحماية"
        ConnectionStatus.CONNECTING -> "جارٍ تبادل مفاتيح WireGuard..."
        ConnectionStatus.CONNECTED -> "الاتصال نشط ومحمي بنجاح"
      },
      color = when (status) {
        ConnectionStatus.DISCONNECTED -> TextSecondary
        ConnectionStatus.CONNECTING -> NeonAmber
        ConnectionStatus.CONNECTED -> NeonGreen
      },
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
