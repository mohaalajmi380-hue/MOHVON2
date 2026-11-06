package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
  sheetState: SheetState,
  uiState: VpnUiState,
  onDismiss: () -> Unit,
  onToggleKillSwitch: (Boolean) -> Unit,
  onToggleSplitTunneling: (Boolean) -> Unit,
  onToggleAppExclusion: (String) -> Unit,
  onSelectProtocol: (VpnProtocol) -> Unit,
  onToggleDnsProtection: (Boolean) -> Unit
) {
  val scrollState = rememberScrollState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CyberBackground,
    tonalElevation = 8.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "إعدادات الأمان والميزات المتقدمة",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "تخصيص البروتوكول ومفتاح القطع والتوجيه المنفصل",
            fontSize = 12.sp,
            color = NeonCyan
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(36.dp)
            .background(CyberSurface, CircleShape)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "إغلاق",
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section 1: Kill Switch
      SettingsCard(
        icon = Icons.Default.Shield,
        iconTint = NeonRed,
        title = "مفتاح القطع التلقائي (Kill Switch)",
        description = "حظر كافة حزم البيانات تلقائياً عند انقطاع الاتصال لمنع تسريب عنوان IP الأصلي أو بياناتك الحساسة."
      ) {
        Switch(
          checked = uiState.isKillSwitchActive,
          onCheckedChange = onToggleKillSwitch,
          colors = SwitchDefaults.colors(
            checkedThumbColor = NeonRed,
            checkedTrackColor = NeonRed.copy(alpha = 0.3f),
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = CyberSurfaceVariant
          )
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Section 2: Split Tunneling
      SettingsCard(
        icon = Icons.Default.Security,
        iconTint = NeonPurple,
        title = "التوجيه المنفصل (Split Tunneling)",
        description = "اختر التطبيقات التي تتصل بالإنترنت مباشرة دون المرور عبر نفق VPN (مثل التطبيقات البنكية المحلية)."
      ) {
        Switch(
          checked = uiState.isSplitTunnelingActive,
          onCheckedChange = onToggleSplitTunneling,
          colors = SwitchDefaults.colors(
            checkedThumbColor = NeonPurple,
            checkedTrackColor = NeonPurple.copy(alpha = 0.3f),
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = CyberSurfaceVariant
          )
        )
      }

      if (uiState.isSplitTunnelingActive) {
        Spacer(modifier = Modifier.height(10.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
        ) {
          Text(
            text = "التطبيقات المستثناة من الـ VPN:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = NeonPurple
          )
          Spacer(modifier = Modifier.height(8.dp))

          uiState.excludedApps.forEach { app ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleAppExclusion(app.packageName) }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = app.isExcluded,
                onCheckedChange = { onToggleAppExclusion(app.packageName) },
                colors = CheckboxDefaults.colors(
                  checkedColor = NeonPurple,
                  checkmarkColor = CyberBackground,
                  uncheckedColor = TextMuted
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = app.appName,
                  fontSize = 13.sp,
                  color = if (app.isExcluded) TextPrimary else TextSecondary,
                  fontWeight = if (app.isExcluded) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                  text = if (app.isExcluded) "مستثنى (اتصال مباشر)" else "يمر عبر النفق الآمن",
                  fontSize = 11.sp,
                  color = if (app.isExcluded) NeonAmber else TextMuted
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Section 3: Protocol Selector
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(CyberSurface)
          .border(1.dp, CyberBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
          .padding(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.VpnKey,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "بروتوكول التشفير (VPN Protocol)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        VpnProtocol.values().forEach { protocol ->
          val isSelected = uiState.selectedProtocol == protocol
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) NeonCyanDim.copy(alpha = 0.15f) else Color.Transparent)
              .clickable { onSelectProtocol(protocol) }
              .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = isSelected,
              onClick = { onSelectProtocol(protocol) },
              colors = RadioButtonDefaults.colors(
                selectedColor = NeonCyan,
                unselectedColor = TextMuted
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = protocol.displayName,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) NeonCyan else TextPrimary
                )
                if (protocol.isRecommended) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .background(NeonGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "موصى به (الأسرع)",
                      fontSize = 10.sp,
                      color = NeonGreen,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
              Text(
                text = "خوارزمية التشفير: ${protocol.cipher}",
                fontSize = 11.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Section 4: DNS Leak Protection
      SettingsCard(
        icon = Icons.Default.Lock,
        iconTint = NeonGreen,
        title = "حماية تسريب الـ DNS و IPv6",
        description = "توجيه كافة استعلامات الـ DNS عبر خوادم WireGuard المشفرة الخاصة بنا لمنع تتبع مزود الخدمة (ISP)."
      ) {
        Switch(
          checked = uiState.isDnsLeakProtected,
          onCheckedChange = onToggleDnsProtection,
          colors = SwitchDefaults.colors(
            checkedThumbColor = NeonGreen,
            checkedTrackColor = NeonGreen.copy(alpha = 0.3f),
            uncheckedThumbColor = TextMuted,
            uncheckedTrackColor = CyberSurfaceVariant
          )
        )
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
fun SettingsCard(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  description: String,
  trailingContent: @Composable () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(CyberSurface)
      .border(1.dp, CyberBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .background(iconTint.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = description,
          fontSize = 11.sp,
          color = TextSecondary,
          lineHeight = 16.sp
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    trailingContent()
  }
}
