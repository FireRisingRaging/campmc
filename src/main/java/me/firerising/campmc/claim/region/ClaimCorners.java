package me.firerising.campmc.claim.region;

import org.bukkit.Chunk;

public class ClaimCorners {
    public final String chunkID;
    public final double[] x, z;

    public ClaimCorners(Chunk chunk, double[] x, double[] z) {
        this.chunkID = chunk.getX() + ";" + chunk.getZ();

        this.x = x;
        this.z = z;
    }
}