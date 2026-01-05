package de.one_piece_content.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AnimationBridge {

    // Single-frame trigger flag
    private static boolean shouldTrigger = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null)
                return;

            // Only fire the trigger once per request
            if (shouldTrigger) {
                updateOnePieceMorph(client.player, true);
                shouldTrigger = false;
            }
        });
    }

    public static void triggerSandSpikeAnimation(int duration) {
        // Queue a trigger for the next tick
        shouldTrigger = true;
    }

    private static void updateOnePieceMorph(Object player, boolean active) {
        try {
            Class<?> storageClass = Class.forName("fr.onepiece.common.player.OnePlayerStorage");

            // Robust lookup: iterate methods to find one named "getOnePlayer"
            // This avoids guessing whether the parameter is 'Player' or 'PlayerEntity'
            Method getOnePlayer = Arrays.stream(storageClass.getMethods())
                    .filter(m -> m.getName().equals("getOnePlayer"))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchMethodException("getOnePlayer not found in OnePlayerStorage"));

            Object onePlayer = getOnePlayer.invoke(null, player);

            if (onePlayer != null) {
                Method getRenderer = onePlayer.getClass().getMethod("getRenderer");
                Object renderer = getRenderer.invoke(onePlayer);
                if (renderer != null) {
                    if (active) {
                        try {
                            Method trigger = renderer.getClass().getMethod("triggerSandSpike");
                            trigger.invoke(renderer);
                        } catch (NoSuchMethodException ignored) {
                            // triggerSandSpike might not exist on older versions, fallback or ignore
                        }
                    }

                    // Also invoke setCastingSandSpike if it exists, for backwards compatibility or
                    // state tracking
                    try {
                        Method setCasting = renderer.getClass().getMethod("setCastingSandSpike", boolean.class);
                        setCasting.invoke(renderer, active);
                    } catch (NoSuchMethodException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
