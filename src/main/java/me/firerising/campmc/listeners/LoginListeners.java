package me.firerising.campmc.listeners;

import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.claim.ClaimManager;
import me.firerising.campmc.member.ClaimMember;
import me.firerising.campmc.member.ClaimRole;
import me.firerising.campmc.Main;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginListeners implements Listener {

    private final Main plugin;

    public LoginListeners(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginb(PlayerLoginEvent event) {
        ClaimManager claimManager = plugin.getClaimManager();

        Chunk chunk = event.getPlayer().getLocation().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        ClaimMember member = claim.getMember(event.getPlayer());
        if (member == null) {
            claim.addMember(event.getPlayer(), ClaimRole.VISITOR);
            member = claim.getMember(event.getPlayer());
        }

        member.setPresent(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ClaimManager claimManager = plugin.getClaimManager();

        Chunk chunk = event.getPlayer().getLocation().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        ClaimMember member = claim.getMember(player);
        if (member != null) {
            if (member.getRole() == ClaimRole.VISITOR)
                claim.removeMember(member);
            else
                member.setPresent(false);
            plugin.getTrackerTask().toggleFlyOff(player);
        }
    }
}
