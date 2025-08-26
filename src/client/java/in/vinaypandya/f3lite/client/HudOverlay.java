package in.vinaypandya.f3lite.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class HudOverlay {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean enabled = true;

    // ---- appearance
    private static final float FONT_SCALE = 1.0f; // <— tweak font size here (1.0 = default)

    // ---- entity count cache (update once every 10 ticks ≈ 0.5s)
    private static int lastHostileCount = 0;
    private static int hostileTicker = 0;

    @SuppressWarnings("deprecation") // HudRenderCallback is deprecated but correct on 1.21.x
    public static void register() {
        HudRenderCallback.EVENT.register(HudOverlay::render);
    }

    public static void toggle() {
        enabled = !enabled;
    }

    private static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        if (!enabled || client.player == null || client.world == null) return;

        // If the vanilla HUD is hidden (F1), bail early or show a hint (uncomment to show hint)
        // if (client.options.hudHidden) {
        //     drawScaled(ctx, "HUD hidden (press F1)", 5, 5, 0xFFFFAA00, FONT_SCALE);
        //     return;
        // }

        // Smooth position & rotation
        Vec3d p = client.player.getPos();
        double xi = p.x, yi = p.y, zi = p.z;
        float yawi = client.player.getYaw();
        float pitchi = client.player.getPitch();

        // Discrete values that don’t need lerp
        BlockPos pos = client.player.getBlockPos();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        String biome = client.world.getBiome(pos)
                .getKey().map(k -> k.getValue().toString()).orElse("unknown");

        int fps = client.getCurrentFps();

        // ---- cached hostile count
        if ((hostileTicker++ % 5) == 0) { // ~2x per second
            Box box = client.player.getBoundingBox().expand(32.0, 16.0, 32.0);;
            lastHostileCount = client.world.getOtherEntities(
                    client.player,
                    box,
                    e -> e instanceof Monster
            ).size();
        }
        int hostileEntities = lastHostileCount;

        int surfaceY = client.world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());

        boolean inNether = client.world.getRegistryKey() == World.NETHER;
        boolean inOverworld = client.world.getRegistryKey() == World.OVERWORLD;
        String conv = inOverworld
                ? String.format("→ Nether: %.0f %.0f %.0f", xi / 8.0, yi, zi / 8.0)
                : inNether
                ? String.format("→ Overworld: %.0f %.0f %.0f", xi * 8.0, yi, zi * 8.0)
                : "Dim: " + client.world.getRegistryKey().getValue();

        // ---- layout (ARGB color must include alpha)
        int left = 5, line = 10, gap = 12, color = 0xFFFFEECC;

        drawScaled(ctx, String.format("XYZ: %.3f / %.3f / %.3f | Dir: %s", xi, yi, zi, dirLabel(yawi)), left, line, color, FONT_SCALE);
        line += scaled(gap);
        drawScaled(ctx, String.format("Yaw: %.1f Pitch: %.1f", yawi, pitchi), left, line, color, FONT_SCALE);
        line += scaled(gap);
        drawScaled(ctx, "Biome: " + biome, left, line, color, FONT_SCALE);
        line += scaled(gap);
        drawScaled(ctx, "Surface Y: " + surfaceY, left, line, color, FONT_SCALE);
        line += scaled(gap);
        drawScaled(ctx, "Chunk: " + chunkX + " " + (pos.getY() >> 4) + " " + chunkZ, left, line, color, FONT_SCALE);
        line += scaled(gap);
        drawScaled(ctx, "Hostile Entities: " + hostileEntities, left, line, color, FONT_SCALE);
        line += scaled(gap);
        drawScaled(ctx, conv, left, line, color, FONT_SCALE);
        line += scaled(gap);
        drawScaled(ctx, "FPS: " + fps, left, line, color, FONT_SCALE);
    }

    private static int scaled(int v) {
        return Math.max(1, (int) Math.floor(v * FONT_SCALE));
    }

    // Draw text with scale by manipulating the matrix on DrawContext
    private static void drawScaled(DrawContext ctx, String s, int x, int y, int argb, float scale) {
        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(x, y);   // move to screen position
        ctx.getMatrices().scale(scale, scale);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(s), 0, 0, argb);
        ctx.getMatrices().popMatrix();
    }

    // Compact compass label from yaw (0..360)
    private static String dirLabel(float yawDeg) {
        float yaw = (yawDeg % 360 + 360) % 360; // normalize
        String[] dirs = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
        return dirs[Math.round(yaw / 45f) % 8];
    }
}
