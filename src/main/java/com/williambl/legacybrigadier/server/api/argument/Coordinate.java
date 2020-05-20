package com.williambl.legacybrigadier.server.api.argument;

import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Vec3i;

@Environment(EnvType.SERVER)
public class Coordinate {

    final CoordinatePart x;
    final CoordinatePart y;
    final CoordinatePart z;

    Coordinate(CoordinatePart x, CoordinatePart y, CoordinatePart z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get the {@link Vec3i} of the absolute position represented by this Coordinate.
     * @param commandSource the commandSource whose position will be used to resolve relative coordinates.
     * @return the {@link Vec3i} of the position.
     */
    public Vec3i getVec3i(CommandSourceHooks commandSource) {
        Vec3i sourceCoords = commandSource.getPosition();
        return new Vec3i(resolve(x, sourceCoords.x), resolve(y, sourceCoords.y), resolve(z, sourceCoords.z));
    }

    private static int resolve(CoordinatePart part, int relativeTo) {
        return part.isRelative ? part.coord + relativeTo : part.coord;
    }

    public static class CoordinatePart {
        final int coord;
        final boolean isRelative;

        public CoordinatePart(int coord, boolean isRelative) {
            this.coord = coord;
            this.isRelative = isRelative;
        }
    }

}
