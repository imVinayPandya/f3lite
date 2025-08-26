package in.vinaypandya.f3lite.client;

import net.fabricmc.api.ClientModInitializer;

public class F3liteClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Keybinds.register();
        HudOverlay.register();
    }
}
