package in.vinaypandya.f3lite.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
    private static KeyBinding toggleHudKey;
    private static final KeyBinding.Category F3_LITE = KeyBinding.Category.create(Identifier.of("f3lite", "main"));

    public static void register() {
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle HUD",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                F3_LITE
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                HudOverlay.toggle();
            }
        });
    }
}
