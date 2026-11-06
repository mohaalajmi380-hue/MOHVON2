package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanDim
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleDim
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  viewModel: VpnViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()

  var showServerSheet by remember { mutableStateOf(false) }
  var showSettingsSheet by remember { mutableStateOf(false) }

  val serverSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Scaffold(
    containerColor = CyberBackground,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Bar / App Branding
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.linearGradient(
                  listOf(NeonCyan, NeonPurple)
                )
              )
              .padding(2.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(CyberBackground, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "MOH",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan,
                letterSpacing = 1.sp
              )
              Text(
                text = "VPN",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = NeonPurple,
                letterSpacing = 1.sp
              )
            }
            Text(
              text = "WIREGUARD EDITION",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = TextSecondary,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 1.5.sp
            )
          }
        }

        // Protocol Badge & Settings Button
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Kill Switch Chip
          if (uiState.isKillSwitchActive) {
            Box(
              modifier = Modifier
                .background(NeonRed.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .border(1.dp, NeonRed.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "KILL SWITCH",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = NeonRed,
                fontFamily = FontFamily.Monospace
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
          }

          IconButton(
            onClick = { showSettingsSheet = true },
            modifier = Modifier
              .size(40.dp)
              .background(CyberSurface, CircleShape)
              .border(1.dp, CyberBorder.copy(alpha = 0.5f), CircleShape)
              .testTag("settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "الإعدادات",
              tint = NeonCyan,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 2. IP & Protection Status HUD
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(CyberSurface)
          .border(1.dp, CyberBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (uiState.status == ConnectionStatus.CONNECTED) "عنوان الـ IP الآمن المشفر:" else "عنوان الـ IP الحقيقي المكشوف:",
              fontSize = 11.sp,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = uiState.currentIp,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = if (uiState.status == ConnectionStatus.CONNECTED) NeonGreen else NeonAmber
            )
          }

          Box(
            modifier = Modifier
              .background(
                when (uiState.status) {
                  ConnectionStatus.DISCONNECTED -> NeonRed.copy(alpha = 0.15f)
                  ConnectionStatus.CONNECTING -> NeonAmber.copy(alpha = 0.15f)
                  ConnectionStatus.CONNECTED -> NeonGreen.copy(alpha = 0.15f)
                },
                RoundedCornerShape(8.dp)
              )
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = when (uiState.status) {
                ConnectionStatus.DISCONNECTED -> "غير محمي"
                ConnectionStatus.CONNECTING -> "تأمين النفق..."
                ConnectionStatus.CONNECTED -> "محمي 100%"
              },
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = when (uiState.status) {
                ConnectionStatus.DISCONNECTED -> NeonRed
                ConnectionStatus.CONNECTING -> NeonAmber
                ConnectionStatus.CONNECTED -> NeonGreen
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 3. Central One-Tap Cyber Connect Button
      CyberConnectButton(
        status = uiState.status,
        onClick = { viewModel.toggleConnection() }
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 4. Live Speed & Duration HUD (Visible & Active)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Download Card
        SpeedMetricCard(
          modifier = Modifier.weight(1f),
          title = "التحميل (Download)",
          value = if (uiState.status == ConnectionStatus.CONNECTED) String.format(Locale.US, "%.1f", uiState.downloadSpeedMbps) else "0.0",
          unit = "MB/s",
          icon = Icons.Default.ArrowDownward,
          accentColor = NeonCyan
        )

        // Upload Card
        SpeedMetricCard(
          modifier = Modifier.weight(1f),
          title = "الرفع (Upload)",
          value = if (uiState.status == ConnectionStatus.CONNECTED) String.format(Locale.US, "%.1f", uiState.uploadSpeedMbps) else "0.0",
          unit = "MB/s",
          icon = Icons.Default.ArrowUpward,
          accentColor = NeonPurple
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Duration Timer HUD Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(CyberSurface)
          .border(1.dp, CyberBorder.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "مدة الجلسة المشفرة:",
            fontSize = 12.sp,
            color = TextSecondary
          )
        }

        val hours = uiState.durationSeconds / 3600
        val minutes = (uiState.durationSeconds % 3600) / 60
        val seconds = uiState.durationSeconds % 60
        val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

        Text(
          text = if (uiState.status == ConnectionStatus.CONNECTED) timeFormatted else "--:--:--",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = if (uiState.status == ConnectionStatus.CONNECTED) NeonCyan else TextMuted
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 5. Current Server Card with Quick Switch Trigger
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.linearGradient(
              listOf(CyberSurfaceVariant, CyberSurface)
            )
          )
          .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
          .clickable { showServerSheet = true }
          .padding(16.dp)
          .testTag("current_server_card")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = uiState.selectedServer.flagEmoji,
              fontSize = 32.sp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Text(
                text = "الخادم المختار",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = NeonCyan
              )
              Text(
                text = "${uiState.selectedServer.countryAr} (${uiState.selectedServer.countryEn})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = uiState.selectedServer.city,
                  fontSize = 12.sp,
                  color = TextSecondary
                )
                Text(
                  text = " • ",
                  fontSize = 12.sp,
                  color = TextMuted
                )
                Text(
                  text = "${uiState.selectedServer.pingMs} ms",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = NeonGreen,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .background(NeonCyanDim, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "تغيير",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
              contentDescription = "قائمة الخوادم",
              tint = TextMuted,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
    }
  }

  // Server List Bottom Sheet
  if (showServerSheet) {
    ServerListSheet(
      sheetState = serverSheetState,
      uiState = uiState,
      onDismiss = { showServerSheet = false },
      onSelectServer = { server -> viewModel.selectServer(server) },
      onToggleFavorite = { serverId -> viewModel.toggleFavorite(serverId) },
      onCategorySelected = { category -> viewModel.setCategoryFilter(category) },
      onSearchChanged = { query -> viewModel.setSearchQuery(query) }
    )
  }

  // Settings Bottom Sheet
  if (showSettingsSheet) {
    SettingsSheet(
      sheetState = settingsSheetState,
      uiState = uiState,
      onDismiss = { showSettingsSheet = false },
      onToggleKillSwitch = { enabled -> viewModel.toggleKillSwitch(enabled) },
      onToggleSplitTunneling = { enabled -> viewModel.toggleSplitTunneling(enabled) },
      onToggleAppExclusion = { pkg -> viewModel.toggleAppExclusion(pkg) },
      onSelectProtocol = { protocol -> viewModel.setProtocol(protocol) },
      onToggleDnsProtection = { enabled -> viewModel.toggleDnsProtection(enabled) }
    )
  }
}

@Composable
fun SpeedMetricCard(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  unit: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(CyberSurface)
      .border(1.dp, CyberBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          color = TextSecondary
        )
        Box(
          modifier = Modifier
            .size(24.dp)
            .background(accentColor.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(verticalAlignment = Alignment.Bottom) {
        Text(
          text = value,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          color = TextPrimary,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = unit,
          fontSize = 11.sp,
          color = accentColor,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(bottom = 2.dp)
        )
      }
    }
  }
}
