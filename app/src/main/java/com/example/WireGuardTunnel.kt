package com.example

import com.wireguard.android.backend.Tunnel

class WireGuardTunnel(
  private val tunnelName: String = "MOHVPN_SECURE_TUNNEL"
) : Tunnel {

  override fun getName(): String = tunnelName

  override fun onStateChange(newState: Tunnel.State) {
    // Invoked by the WireGuard Go backend when state transitions
  }
}
