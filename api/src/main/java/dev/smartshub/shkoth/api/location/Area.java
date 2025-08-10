package dev.smartshub.shkoth.api.location;

import org.bukkit.Location;

public record Area(
        String worldName,
        Corner corner1,
        Corner corner2
) {

    public boolean contains(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        if (!worldName.equals(location.getWorld().getName())) {
            return false;
        }

        int minX = Math.min(corner1.x(), corner2.x());
        int maxX = Math.max(corner1.x(), corner2.x());

        int minY = Math.min(corner1.y(), corner2.y());
        int maxY = Math.max(corner1.y(), corner2.y());

        int minZ = Math.min(corner1.z(), corner2.z());
        int maxZ = Math.max(corner1.z(), corner2.z());

        int locX = location.getBlockX();
        int locY = location.getBlockY();
        int locZ = location.getBlockZ();

        return locX >= minX && locX <= maxX &&
                locY >= minY && locY <= maxY &&
                locZ >= minZ && locZ <= maxZ;
    }

    public Location getCenter() {
        int centerX = (corner1.x() + corner2.x()) / 2;
        int centerY = (corner1.y() + corner2.y()) / 2;
        int centerZ = (corner1.z() + corner2.z()) / 2;

        return new Location(
                org.bukkit.Bukkit.getWorld(worldName),
                centerX,
                centerY,
                centerZ
        );
    }
}
