package me.firerising.campmc.tasks;

import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.claim.ClaimSetting;
import me.firerising.campmc.member.ClaimMember;
import me.firerising.campmc.member.ClaimRole;
import me.firerising.campmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrackerTask extends BukkitRunnable {

    private static TrackerTask instance;
    private static Main plugin;

    private final Map<UUID, TrackedPlayer> trackedPlayers = new HashMap<>();

    public TrackerTask(Main plug) {
        plugin = plug;
    }

    public static TrackerTask startTask(Main plug) {
        plugin = plug;
        if (instance == null) {
            instance = new TrackerTask(plugin);
            instance.runTaskTimerAsynchronously(plugin, 10, 20);
        }

        return instance;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            TrackedPlayer trackedPlayer = trackedPlayers.computeIfAbsent(player.getUniqueId(), t -> new TrackedPlayer(player.getUniqueId()));
            Claim claim = plugin.getClaimManager().getClaim(player.getLocation().getChunk());
            if (claim == null) continue;

            if (!player.hasPermission("ultimateclaims.admin.invisible")) {
                ClaimMember member = claim.getMember(player);
                if (member == null) {
                    claim.addMember(player, ClaimRole.VISITOR);
                    member = claim.getMember(player);
                }
                member.setPresent(true);

                if (claim.isBanned(player.getUniqueId())
                        && !player.hasPermission("ultimateclaims.bypass.ban")

                        || claim.isLocked()
                        && claim.getMember(player).getRole() == ClaimRole.VISITOR
                        && !player.hasPermission("ultimateclaims.bypass.lock"))
                    member.eject(trackedPlayer.getLastBeforeClaim());
            }

            if ((claim.getClaimSettings().isEnabled(ClaimSetting.FLY)
                    && player.hasPermission("ultimateclaims.fly")
                    || player.hasPermission("Ultimateclaims.bypass.fly"))
                    && !player.getAllowFlight()
                    && player.getGameMode() != GameMode.CREATIVE) {
                trackedPlayer.setWasFlyActivated(true);
                player.setAllowFlight(true);
            }
        }
        for (Claim claim : plugin.getClaimManager().getRegisteredClaims()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ClaimMember member = claim.getMember(player);

                if (member == null || !member.isPresent()) continue;

                Claim in = plugin.getClaimManager().getClaim(player.getLocation().getChunk());

                if (in == claim) continue;
                member.setPresent(false);
                toggleFlyOff(player);
            }
        }
    }

    public void addLastBefore(Player player, Location location) {
        TrackedPlayer trackedPlayer = trackedPlayers.get(player.getUniqueId());

        if (trackedPlayer == null)
            return;

        trackedPlayer.setLastBeforeClaim(location);
    }

    public void toggleFlyOff(Player player) {
        TrackedPlayer trackedPlayer = trackedPlayers.get(player.getUniqueId());

        if (trackedPlayer == null)
            return;

        if (!trackedPlayer.wasFlyActivated()) return;

        trackedPlayer.setWasFlyActivated(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFallDistance(0F);
    }

    private static class TrackedPlayer {

        private final UUID uuid;
        private Location lastBeforeClaim;
        private boolean wasFlyActivated;

        public TrackedPlayer(UUID uuid) {
            this.uuid = uuid;
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public Location getLastBeforeClaim() {
            return lastBeforeClaim;
        }

        public void setLastBeforeClaim(Location lastBeforeClaim) {
            this.lastBeforeClaim = lastBeforeClaim;
        }

        public boolean wasFlyActivated() {
            return wasFlyActivated;
        }

        public void setWasFlyActivated(boolean wasFlyActivated) {
            this.wasFlyActivated = wasFlyActivated;
        }
    }
}
