package com.example

enum class ConnectionStatus {
  DISCONNECTED,
  CONNECTING,
  CONNECTED
}

enum class ServerCategory(val titleAr: String, val titleEn: String) {
  FASTEST("الأسرع", "Fastest"),
  GAMING("الألعاب", "Gaming"),
  STREAMING("البث المباشر", "Streaming"),
  ALL("جميع الخوادم", "All")
}

enum class VpnProtocol(val displayName: String, val cipher: String, val isRecommended: Boolean) {
  WIREGUARD("WireGuard", "ChaCha20-Poly1305", true),
  OPENVPN_UDP("OpenVPN (UDP)", "AES-256-GCM", false),
  OPENVPN_TCP("OpenVPN (TCP)", "AES-256-GCM", false)
}

data class VpnServer(
  val id: String,
  val countryAr: String,
  val countryEn: String,
  val city: String,
  val flagEmoji: String,
  val pingMs: Int,
  val ipAddress: String,
  val loadPercent: Int,
  val category: ServerCategory,
  val isFavorite: Boolean = false,
  val serverPublicKey: String? = null,
  val clientPrivateKey: String? = null,
  val clientAddress: String = "10.66.66.2/32",
  val dns: String = "1.1.1.1",
  val endpointPort: Int = 51820,
  val rawConfig: String? = null
)

data class InstalledApp(
  val packageName: String,
  val appName: String,
  val isExcluded: Boolean = false
)

data class VpnUiState(
  val status: ConnectionStatus = ConnectionStatus.DISCONNECTED,
  val selectedServer: VpnServer,
  val servers: List<VpnServer>,
  val searchQuery: String = "",
  val selectedCategory: ServerCategory = ServerCategory.ALL,
  val originalIp: String = "197.35.12.89",
  val currentIp: String = "197.35.12.89",
  val downloadSpeedMbps: Float = 0f,
  val uploadSpeedMbps: Float = 0f,
  val durationSeconds: Long = 0,
  val isKillSwitchActive: Boolean = true,
  val isSplitTunnelingActive: Boolean = false,
  val excludedApps: List<InstalledApp>,
  val selectedProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
  val isDnsLeakProtected: Boolean = true
)
