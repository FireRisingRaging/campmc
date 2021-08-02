package me.firerising.campmc.dynmap;

import com.songoda.core.utils.TimeUtils;
import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.claim.region.ClaimCorners;
import me.firerising.campmc.claim.region.RegionCorners;
import me.firerising.campmc.settings.Settings;
import me.firerising.campmc.Main;
import org.bukkit.Bukkit;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class DynmapManager {
    private final Main plugin;
    private final DynmapAPI dynmapAPI;
    private MarkerSet markerSet; // null when not ready or disabled

    private int taskID = -1;

    public DynmapManager(Main plugin) {
        this.plugin = plugin;
        this.dynmapAPI = (DynmapAPI) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("dynmap"));
        reload();

        for (Claim claim : plugin.getClaimManager().getRegisteredClaims())
            refresh(claim);
    }

    /**
     * Creates new {@link AreaMarker} for a {@link Claim} and deletes old ones
     */
    public void refresh(Claim claim) {
        if (markerSet == null) return;

        List<RegionCorners> corners = claim.getCorners();

        if (corners == null) return;

        // Remove AreaMarkers of now unclaimed Chunks
        for (AreaMarker aMarker : markerSet.getAreaMarkers()) {
            if (!aMarker.getMarkerID().startsWith(claim.getId() + ":")) continue;

            for (RegionCorners r : new HashSet<>(corners)) {
                for (ClaimCorners c : r.getClaimCorners()) {
                    if (!aMarker.getMarkerID().equals(claim.getId() + ":" + c.chunkID)) continue;
                    aMarker.deleteMarker();
                    break;
                }
            }
        }

        for (RegionCorners r : new HashSet<>(corners)) {
            for (ClaimCorners c : r.getClaimCorners()) {
                if (markerSet.findAreaMarker(claim.getId() + ":" + c.chunkID) == null) {
                    markerSet.createAreaMarker(claim.getId() + ":" + c.chunkID, "Claim #" + claim.getId(),
                            false, claim.getFirstClaimedChunk().getWorld(), c.x, c.z, false);
                }
                refreshDescription(claim);
            }
        }
    }

    /**
     * Update the description of existing {@link AreaMarker} for a {@link Claim}
     */
    public void refreshDescription(Claim claim) {
        if (markerSet == null) return;

        String markerDesc = Settings.DYNMAP_BUBBLE.getString()
                .replace("${Claim}", claim.getName())
                .replace("${Owner}", claim.getOwner().getName())
                .replace("${OwnerUUID}", claim.getOwner().getUniqueId().toString())
                .replace("${MemberCount}", claim.getMembers().size() + "")
                .replace("${PowerLeft}",
                        TimeUtils.makeReadable(claim.getPowerCell().getTotalPower() * 60 * 1000));

        for (AreaMarker aMarker : markerSet.getAreaMarkers()) {
            if (!aMarker.getMarkerID().startsWith(claim.getId() + ":")) continue;
            aMarker.setDescription(markerDesc);
        }
    }

    public void reload() {
        this.markerSet = dynmapAPI.getMarkerAPI().getMarkerSet("UltimateClaims.chunks");

        if (markerSet != null)
            this.markerSet.deleteMarkerSet();

        if (Settings.DYNMAP_ENABLED.getBoolean()) {
            this.markerSet = dynmapAPI.getMarkerAPI().createMarkerSet("UltimateClaims.chunks",
                    Settings.DYNMAP_LABEL.getString(), dynmapAPI.getMarkerAPI().getMarkerIcons(), false);

            int updateInterval = Settings.DYNMAP_UPDATE_INTERVAL.getInt();

            Runnable task = () -> {
                if (this.plugin.getClaimManager() != null)
                    for (Claim claim : this.plugin.getClaimManager().getRegisteredClaims())
                        refreshDescription(claim);
            };

            if (taskID == -1) {
                task.run();

                if (updateInterval > 0) {
                    taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, task,
                            20 * updateInterval, 20 * updateInterval);
                }
            }
            return;
        }
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
        }

        this.markerSet = null;
    }
}