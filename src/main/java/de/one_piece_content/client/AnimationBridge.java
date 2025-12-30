package de.one_piece_content.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AnimationBridge {

    // Cached Reflection Objects
    private static Object cachedContainer = null;
    private static Method isCastingMethod = null;
    private static Field isCastingField = null;
    private static Method getSpellIdMethod = null;
    private static Field spellIdField = null;

    private static boolean initialized = false;
    private static int debugTicks = 0;

    // Timer to persist animation state for short/instant casts
    private static int castingTimer = 0;
    private static final int ANIMATION_DURATION = 20; // 1 second

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null)
                return;
            Object player = client.player;

            if (!initialized) {
                de.one_piece_content.ExampleMod.LOGGER.info("AnimationBridge: Initializing discovery...");
                initialized = true;

                // 1. Check Player Interfaces
                for (Class<?> iface : player.getClass().getInterfaces()) {
                    if (iface.getName().toLowerCase().contains("spell")) {
                        de.one_piece_content.ExampleMod.LOGGER
                                .info("AnimationBridge: Found Spell Interface: " + iface.getName());
                        cachedContainer = player;
                        scanClass(iface);
                    }
                }

                // 2. Scan Player Class itself
                de.one_piece_content.ExampleMod.LOGGER.info("AnimationBridge: Scanning Player class directly.");
                scanClass(player.getClass());
                if (cachedContainer == null)
                    cachedContainer = player;

                // Chat Feedback
                if (isCastingMethod != null || isCastingField != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("AnimationBridge: HOOKED! Found casting detection."),
                            false);
                } else {
                    client.player.sendMessage(net.minecraft.text.Text
                            .literal("AnimationBridge: FAILED to find Casting detection methods. Check Logs."), false);
                }
            }

            // Per-tick logic
            boolean currentlyCasting = false;
            try {
                if (isCastingMethod != null) {
                    Object res = isCastingMethod.invoke(cachedContainer);
                    if (res instanceof Boolean)
                        currentlyCasting = (Boolean) res;
                } else if (isCastingField != null) {
                    currentlyCasting = isCastingField.getBoolean(cachedContainer);
                }
            } catch (Exception e) {
            }

            String spellStr = "";
            if (currentlyCasting) {
                try {
                    Object rawSpellObj = null;
                    if (getSpellIdMethod != null) {
                        rawSpellObj = getSpellIdMethod.invoke(cachedContainer);
                    } else if (spellIdField != null) {
                        rawSpellObj = spellIdField.get(cachedContainer);
                    }

                    if (rawSpellObj != null) {
                        // If it's a Spell object, toString might not give ID directly, but usually does
                        // in debug form.
                        // Or we can try to find 'getId' on it?
                        // Let's rely on toString for now as per plan.
                        spellStr = rawSpellObj.toString();

                        // If toString is generic, try to get ID field reflectively from the Spell
                        // Object
                        if (!spellStr.contains(":")) {
                            try {
                                // Try to find getId or getSpellId on the Spell object result
                                Class<?> spellCls = rawSpellObj.getClass();
                                try {
                                    Method m = spellCls.getMethod("getId"); // Common pattern
                                    Object id = m.invoke(rawSpellObj);
                                    if (id != null)
                                        spellStr = id.toString();
                                } catch (Exception ex) {
                                    Field f = spellCls.getField("id");
                                    Object id = f.get(rawSpellObj);
                                    if (id != null)
                                        spellStr = id.toString();
                                }
                            } catch (Exception deepEx) {
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }

            // Logic: If spell is casting (and is sand_spikes), REFILL the timer.
            if (currentlyCasting && spellStr.contains("sand_spikes")) {
                castingTimer = ANIMATION_DURATION;

                // Log casting state occasionally
                if (debugTicks % 60 == 0) {
                    String msg = "AnimationBridge: CASTING! Spell: " + spellStr;
                    de.one_piece_content.ExampleMod.LOGGER.info(msg);
                    client.player.sendMessage(net.minecraft.text.Text.literal(msg), true);
                }
                debugTicks++;
            }

            boolean active = castingTimer > 0;
            if (castingTimer > 0) {
                castingTimer--;
            } else {
                if (debugTicks > 0)
                    debugTicks = 0;
            }

            updateOnePieceMorph(player, active);
        });
    }

    public static void triggerSandSpikeAnimation(int duration) {
        castingTimer = duration;
    }

    private static void scanClass(Class<?> cls) {
        // Broad scan for anything related to Casting or Spell ID
        for (Method m : cls.getMethods()) {
            String name = m.getName(); // Case sensitive check first for exact matches from logs

            // EXACT MATCHES FROM LOGS
            if (name.equals("isCastingSpell") && m.getReturnType() == boolean.class) {
                isCastingMethod = m;
                de.one_piece_content.ExampleMod.LOGGER.info("AnimationBridge: LOCKED isCasting Method: " + name);
            }
            if (name.equals("getCurrentSpell")) {
                getSpellIdMethod = m;
                de.one_piece_content.ExampleMod.LOGGER.info("AnimationBridge: LOCKED SpellID Method: " + name);
            }

            // Fallback Heuristics
            if (isCastingMethod == null && name.equals("isCasting")) {
                isCastingMethod = m;
            }
            if (getSpellIdMethod == null && (name.equals("getSpellId") || name.equals("getCurrentSpellId"))) {
                getSpellIdMethod = m;
            }
        }

        for (Field f : cls.getFields()) {
            String name = f.getName();
            if (f.getType() == boolean.class && (name.equals("isCasting") || name.equals("casting"))) {
                if (isCastingMethod == null && isCastingField == null)
                    isCastingField = f;
            }
            if (name.equals("spellId") || name.equals("currentSpellId")) {
                if (getSpellIdMethod == null && spellIdField == null)
                    spellIdField = f;
            }
        }
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
                    Method setCasting = renderer.getClass().getMethod("setCastingSandSpike", boolean.class);
                    setCasting.invoke(renderer, active);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
