package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class VpnViewModel : ViewModel() {

  private val initialServers = listOf(
    VpnServer(
      id = "de_fra_1",
      countryAr = "ألمانيا",
      countryEn = "Germany",
      city = "Frankfurt #04",
      flagEmoji = "🇩🇪",
      pingMs = 18,
      ipAddress = "185.220.101.5",
      loadPercent = 32,
      category = ServerCategory.FASTEST,
      isFavorite = true
    ),
    VpnServer(
      id = "sa_ruh_1",
      countryAr = "السعودية",
      countryEn = "Saudi Arabia",
      city = "Riyadh #01",
      flagEmoji = "🇸🇦",
      pingMs = 9,
      ipAddress = "178.135.24.12",
      loadPercent = 45,
      category = ServerCategory.FASTEST,
      isFavorite = true
    ),
    VpnServer(
      id = "ae_dxb_1",
      countryAr = "الإمارات",
      countryEn = "UAE",
      city = "Dubai #02",
      flagEmoji = "🇦🇪",
      pingMs = 14,
      ipAddress = "94.200.88.4",
      loadPercent = 38,
      category = ServerCategory.FASTEST
    ),
    VpnServer(
      id = "us_nyc_1",
      countryAr = "الولايات المتحدة",
      countryEn = "USA",
      city = "New York #12",
      flagEmoji = "🇺🇸",
      pingMs = 82,
      ipAddress = "198.51.100.45",
      loadPercent = 64,
      category = ServerCategory.STREAMING,
      isFavorite = true
    ),
    VpnServer(
      id = "uk_lon_1",
      countryAr = "المملكة المتحدة",
      countryEn = "United Kingdom",
      city = "London #07",
      flagEmoji = "🇬🇧",
      pingMs = 35,
      ipAddress = "185.190.22.8",
      loadPercent = 51,
      category = ServerCategory.STREAMING
    ),
    VpnServer(
      id = "sg_sin_1",
      countryAr = "سنغافورة",
      countryEn = "Singapore",
      city = "Singapore #03",
      flagEmoji = "🇸🇬",
      pingMs = 68,
      ipAddress = "103.245.15.90",
      loadPercent = 29,
      category = ServerCategory.GAMING,
      isFavorite = true
    ),
    VpnServer(
      id = "jp_tyo_1",
      countryAr = "اليابان",
      countryEn = "Japan",
      city = "Tokyo #05",
      flagEmoji = "🇯🇵",
      pingMs = 95,
      ipAddress = "153.120.40.11",
      loadPercent = 42,
      category = ServerCategory.GAMING
    ),
    VpnServer(
      id = "nl_ams_1",
      countryAr = "هولندا",
      countryEn = "Netherlands",
      city = "Amsterdam #09",
      flagEmoji = "🇳🇱",
      pingMs = 22,
      ipAddress = "194.187.249.2",
      loadPercent = 40,
      category = ServerCategory.FASTEST
    )
  )

  private val initialApps = listOf(
    InstalledApp("com.alrajhicentral.bank", "مصرف الراجحي (Al Rajhi)", true),
    InstalledApp("com.stcpay.android", "STC Pay", true),
    InstalledApp("com.whatsapp", "WhatsApp Messenger", false),
    InstalledApp("com.google.android.youtube", "YouTube", false),
    InstalledApp("com.netflix.mediaclient", "Netflix", false),
    InstalledApp("com.pubg.imobile", "PUBG Mobile", false),
    InstalledApp("com.android.chrome", "Google Chrome", false)
  )

  private var wireGuardRepository: WireGuardRepository? = null

  private val _uiState = MutableStateFlow(
    VpnUiState(
      selectedServer = initialServers.first(),
      servers = initialServers,
      excludedApps = initialApps
    )
  )
  val uiState: StateFlow<VpnUiState> = _uiState.asStateFlow()

  private var timerJob: Job? = null
  private var statsJob: Job? = null

  fun setRepository(repository: WireGuardRepository) {
    this.wireGuardRepository = repository
    observeRepository()
  }

  private fun observeRepository() {
    val repo = wireGuardRepository ?: return
    statsJob?.cancel()
    statsJob = viewModelScope.launch {
      repo.tunnelStats.collect { stats ->
        if (_uiState.value.status == ConnectionStatus.CONNECTED) {
          _uiState.update {
            it.copy(
              downloadSpeedMbps = stats.rxRateMbps,
              uploadSpeedMbps = stats.txRateMbps
            )
          }
        }
      }
    }
  }

  fun toggleConnection() {
    when (_uiState.value.status) {
      ConnectionStatus.DISCONNECTED -> startConnecting()
      ConnectionStatus.CONNECTING -> cancelConnecting()
      ConnectionStatus.CONNECTED -> disconnect()
    }
  }

  private fun startConnecting() {
    _uiState.update { it.copy(status = ConnectionStatus.CONNECTING) }

    viewModelScope.launch {
      val server = _uiState.value.selectedServer
      val repo = wireGuardRepository

      if (repo != null) {
        repo.connect(
          server = server,
          protocol = _uiState.value.selectedProtocol,
          isKillSwitch = _uiState.value.isKillSwitchActive
        )
      } else {
        delay(700)
      }

      if (_uiState.value.status == ConnectionStatus.CONNECTING) {
        _uiState.update {
          it.copy(
            status = ConnectionStatus.CONNECTED,
            currentIp = server.ipAddress,
            durationSeconds = 0
          )
        }
        startTimerLoop()
      }
    }
  }

  private fun cancelConnecting() {
    disconnect()
  }

  fun disconnect() {
    timerJob?.cancel()
    viewModelScope.launch {
      wireGuardRepository?.disconnect()
    }
    _uiState.update {
      it.copy(
        status = ConnectionStatus.DISCONNECTED,
        currentIp = it.originalIp,
        downloadSpeedMbps = 0f,
        uploadSpeedMbps = 0f,
        durationSeconds = 0
      )
    }
  }

  private fun startTimerLoop() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_uiState.value.status == ConnectionStatus.CONNECTED) {
        delay(1000)
        _uiState.update { it.copy(durationSeconds = it.durationSeconds + 1) }
      }
    }
  }

  fun selectServer(server: VpnServer) {
    val wasConnected = _uiState.value.status == ConnectionStatus.CONNECTED
    _uiState.update { it.copy(selectedServer = server) }

    if (wasConnected) {
      // Reconnect with the new selected server
      disconnect()
      startConnecting()
    }
  }

  fun toggleFavorite(serverId: String) {
    _uiState.update { state ->
      val updatedList = state.servers.map { s ->
        if (s.id == serverId) s.copy(isFavorite = !s.isFavorite) else s
      }
      val updatedSelected = if (state.selectedServer.id == serverId) {
        state.selectedServer.copy(isFavorite = !state.selectedServer.isFavorite)
      } else {
        state.selectedServer
      }
      state.copy(servers = updatedList, selectedServer = updatedSelected)
    }
  }

  fun setCategoryFilter(category: ServerCategory) {
    _uiState.update { it.copy(selectedCategory = category) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun toggleKillSwitch(enabled: Boolean) {
    _uiState.update { it.copy(isKillSwitchActive = enabled) }
  }

  fun toggleSplitTunneling(enabled: Boolean) {
    _uiState.update { it.copy(isSplitTunnelingActive = enabled) }
  }

  fun toggleAppExclusion(packageName: String) {
    _uiState.update { state ->
      val updatedApps = state.excludedApps.map { app ->
        if (app.packageName == packageName) app.copy(isExcluded = !app.isExcluded) else app
      }
      state.copy(excludedApps = updatedApps)
    }
  }

  fun setProtocol(protocol: VpnProtocol) {
    val wasConnected = _uiState.value.status == ConnectionStatus.CONNECTED
    _uiState.update { it.copy(selectedProtocol = protocol) }
    if (wasConnected) {
      disconnect()
      startConnecting()
    }
  }

  fun toggleDnsProtection(enabled: Boolean) {
    _uiState.update { it.copy(isDnsLeakProtected = enabled) }
  }
}
