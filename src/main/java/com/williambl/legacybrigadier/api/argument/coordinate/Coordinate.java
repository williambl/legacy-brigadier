package com.williambl.legacybrigadier.api.argument.coordinate;

import com.williambl.legacybrigadier.api.command.ExtendedSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Vec3i;
import net.minecraft.util.maths.MathsHelper;
import net.minecraft.util.maths.Vec3d;
import org.lwjgl.util.vector.Vector2f;

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
     * Get the {@link Vec3d} of the absolute position represented by this Coordinate.
     * @param commandSource the commandSource whose position will be used to resolve relative coordinates.
     * @return the {@link Vec3d} of the position.
     */
    public Vec3d getVec3d(ExtendedSender commandSource) {
        Vec3d sourceCoords = commandSource.getPosition();
        if (this.x.type == CoordinateType.LOCAL) {
            return fromLocal(commandSource);
        }
        return Vec3d.getOrCreate(resolve(x, sourceCoords.x), resolve(y, sourceCoords.y), resolve(z, sourceCoords.z));
    }

    /**
     * Get the {@link Vec3i} of the absolute position represented by this Coordinate.
     * @param commandSource the commandSource whose position will be used to resolve relative coordinates.
     * @return the {@link Vec3i} of the position.
     */
    public Vec3i getVec3i(ExtendedSender commandSource) {
        Vec3d sourceCoords = commandSource.getPosition();
        if (this.x.type == CoordinateType.LOCAL) {
            Vec3d res = fromLocal(commandSource);
            new Vec3i((int) res.x, (int) res.y, (int) res.z);
        }
        return new Vec3i((int) resolve(x, sourceCoords.x), (int) resolve(y, sourceCoords.y), (int) resolve(z, sourceCoords.z));
    }

    private Vec3d fromLocal(ExtendedSender source) {
        Vector2f vec2f = source.getRotation();
        Vec3d vec3d = Vec3d.getOrCreate(source.getPosition().x, source.getPosition().y, source.getPosition().z);
        float f = MathsHelper.cos((vec2f.y + 90.0F) * 0.017453292F);
        float g = MathsHelper.sin((vec2f.y + 90.0F) * 0.017453292F);
        float h = MathsHelper.cos(-vec2f.x * 0.017453292F);
        float i = MathsHelper.sin(-vec2f.x * 0.017453292F);
        float j = MathsHelper.cos((-vec2f.x + 90.0F) * 0.017453292F);
        float k = MathsHelper.sin((-vec2f.x + 90.0F) * 0.017453292F);
        Vec3d vec3d2 = Vec3d.getOrCreate(f * h, i, g * h);
        Vec3d vec3d3 = Vec3d.getOrCreate(f * j, k, g * j);
        Vec3d vec3d4 = multiply(crossProduct(vec3d2, vec3d3), -1.0D);
        double d = vec3d2.x * this.z.coord + vec3d3.x * this.y.coord + vec3d4.x * this.x.coord;
        double e = vec3d2.y * this.z.coord + vec3d3.y * this.y.coord + vec3d4.y * this.x.coord;
        double l = vec3d2.z * this.z.coord + vec3d3.z * this.y.coord + vec3d4.z * this.x.coord;
        return Vec3d.getOrCreate((vec3d.x + d), (vec3d.y + e), (vec3d.z + l));
    }

    private static Vec3d multiply(Vec3d original, double amount) {
        return Vec3d.getOrCreate(original.x*amount, original.y*amount, original.z*amount);
    }

    private static Vec3d crossProduct(Vec3d orig, Vec3d arg) {
        return Vec3d.getOrCreate(orig.y * arg.z - orig.z * arg.y, orig.z * arg.x - orig.x * arg.z, orig.x * arg.y - orig.y * arg.x);
    }

    private static double resolve(CoordinatePart part, double relativeTo) {
        return part.type == CoordinateType.RELATIVE ? part.coord + relativeTo : part.coord;
    }

    public static class CoordinatePart {
        final double coord;
        final CoordinateType type;

        public CoordinatePart(double coord, CoordinateType type) {
            this.coord = coord;
            this.type = type;
        }

        /**
         * Checks that either all are {@link CoordinateType#LOCAL LOCAL}, or none are.
         *
         * @param x the first coordinate part
         * @param y the second coordinate part
         * @param z the third coordinate part
         *
         * @return whether all three match in locality
         */
        public static boolean allMatchLocality(CoordinatePart x, CoordinatePart y, CoordinatePart z) {
            return (x.type != CoordinateType.LOCAL || y.type == CoordinateType.LOCAL && z.type == CoordinateType.LOCAL)
                    && (y.type != CoordinateType.LOCAL || x.type == CoordinateType.LOCAL)
                    && (z.type != CoordinateType.LOCAL || x.type == CoordinateType.LOCAL);
        }
    }

    public enum CoordinateType {
        ABSOLUTE,
        RELATIVE,
        LOCAL
    }
}
