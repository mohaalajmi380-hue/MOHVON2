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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListSheet(
  sheetState: SheetState,
  uiState: VpnUiState,
  onDismiss: () -> Unit,
  onSelectServer: (VpnServer) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onCategorySelected: (ServerCategory) -> Unit,
  onSearchChanged: (String) -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CyberBackground,
    tonalElevation = 8.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
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
            text = "قائمة الخوادم المتاحة",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "شبكة WireGuard عالية السرعة مع تشفير ChaCha20",
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

      Spacer(modifier = Modifier.height(16.dp))

      // Search Field
      OutlinedTextField(
        value = uiState.searchQuery,
        onValueChange = onSearchChanged,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("server_search_input"),
        placeholder = { Text("ابحث عن دولة، مدينة، أو عنوان IP...", color = TextMuted, fontSize = 14.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "بحث", tint = NeonCyan)
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = CyberSurface,
          unfocusedContainerColor = CyberSurface,
          focusedBorderColor = NeonCyan,
          unfocusedBorderColor = CyberBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Categories Tabs (Fastest / Gaming / Streaming / All)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ServerCategory.values().forEach { category ->
          val isSelected = uiState.selectedCategory == category
          FilterChip(
            selected = isSelected,
            onClick = { onCategorySelected(category) },
            label = {
              Text(
                text = category.titleAr,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = NeonCyanDim,
              selectedLabelColor = NeonCyan,
              containerColor = CyberSurface,
              labelColor = TextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) NeonCyan else CyberBorder
            ),
            shape = RoundedCornerShape(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Filtered Server List
      val filteredServers = uiState.servers.filter { server ->
        val matchesCategory = uiState.selectedCategory == ServerCategory.ALL || server.category == uiState.selectedCategory
        val matchesSearch = uiState.searchQuery.isBlank() ||
            server.countryAr.contains(uiState.searchQuery, ignoreCase = true) ||
            server.countryEn.contains(uiState.searchQuery, ignoreCase = true) ||
            server.city.contains(uiState.searchQuery, ignoreCase = true) ||
            server.ipAddress.contains(uiState.searchQuery)
        matchesCategory && matchesSearch
      }

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp)
      ) {
        items(filteredServers, key = { it.id }) { server ->
          val isCurrent = uiState.selectedServer.id == server.id
          ServerListItem(
            server = server,
            isSelected = isCurrent,
            onSelect = {
              onSelectServer(server)
              onDismiss()
            },
            onToggleFavorite = { onToggleFavorite(server.id) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun ServerListItem(
  server: VpnServer,
  isSelected: Boolean,
  onSelect: () -> Unit,
  onToggleFavorite: () -> Unit
) {
  val pingColor = when {
    server.pingMs < 30 -> NeonGreen
    server.pingMs < 80 -> NeonAmber
    else -> Color(0xFFFF5252)
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
      .border(
        width = 1.dp,
        color = if (isSelected) NeonCyan else CyberBorder.copy(alpha = 0.5f),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onSelect)
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Flag & Server Info
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = server.flagEmoji,
        fontSize = 28.sp
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${server.countryAr} (${server.countryEn})",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          if (isSelected) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .background(NeonCyanDim, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "محدد",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = server.city,
            fontSize = 12.sp,
            color = TextSecondary
          )
          Text(
            text = " • ",
            fontSize = 12.sp,
            color = TextMuted
          )
          Text(
            text = "الحمل ${server.loadPercent}%",
            fontSize = 12.sp,
            color = if (server.loadPercent < 50) NeonGreen else NeonAmber
          )
        }
      }
    }

    // Ping & Actions
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Ping Badge
      Box(
        modifier = Modifier
          .background(pingColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "${server.pingMs} ms",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = pingColor,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Favorite Button
      IconButton(
        onClick = onToggleFavorite,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = if (server.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
          contentDescription = "المفضلة",
          tint = if (server.isFavorite) NeonAmber else TextMuted,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
