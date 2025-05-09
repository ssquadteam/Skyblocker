package de.hysky.skyblocker.skyblock.slayers.boss.enderman;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.config.configs.SlayersConfig;
import de.hysky.skyblocker.skyblock.slayers.SlayerManager;
import de.hysky.skyblocker.skyblock.slayers.SlayerType;
import de.hysky.skyblocker.utils.ItemUtils;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.render.RenderHelper;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks NukeKubi Heads during the Enderman Slayer boss fight.
 * <p>
 * Features:
 * <ul>
 *     <li>Solid color highlighting option</li>
 *     <li>Proximity alerts with sound and action bar messages</li>
 *     <li>Directional indicators to guide players to nearby heads</li>
 * </ul>
 */
public class NukekubiHeadTracker {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final String NUKEKUBI_HEAD_TEXTURE = "eyJ0aW1lc3RhbXAiOjE1MzQ5NjM0MzU5NjIsInByb2ZpbGVJZCI6ImQzNGFhMmI4MzFkYTRkMjY5NjU1ZTMzYzE0M2YwOTZjIiwicHJvZmlsZU5hbWUiOiJFbmRlckRyYWdvbiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWIwNzU5NGUyZGYyNzM5MjFhNzdjMTAxZDBiZmRmYTExMTVhYmVkNWI5YjIwMjllYjQ5NmNlYmE5YmRiYjRiMyJ9fX0=";
    private static final float[] NUKEKUBI_COLOR_COMPONENTS = {0.6f, 0f, 0.6f}; // Vibrant purple
    private static final int SOUND_INTERVAL_TICKS = 20; // Play sound every second
    private static final int PROXIMITY_CHECK_INTERVAL = 5; // Check for nearby heads every 5 ticks

    private static final List<ArmorStandEntity> nearbyHeads = new ArrayList<>();
    private static int soundTimer = 0;

    @Init
    public static void init() {
        // Register the render event for directional indicators
        WorldRenderEvents.AFTER_TRANSLUCENT.register(NukekubiHeadTracker::render);

        // Schedule proximity checks
        Scheduler.INSTANCE.scheduleCyclic(NukekubiHeadTracker::checkForNearbyHeads, PROXIMITY_CHECK_INTERVAL);
    }

    /**
     * Checks if an armor stand is a NukeKubi head by comparing its head texture.
     *
     * @param entity The armor stand entity to check
     * @return true if the entity is a NukeKubi head, false otherwise
     */
    public static boolean isNukekubiHead(ArmorStandEntity entity) {
        return entity.isMarker() &&
               entity.hasStackEquipped(EquipmentSlot.HEAD) &&
               ItemUtils.getHeadTexture(entity.getEquippedStack(EquipmentSlot.HEAD)).contains(NUKEKUBI_HEAD_TEXTURE);
    }

    /**
     * Checks for NukeKubi heads near the player and updates the nearbyHeads list.
     * Also handles proximity alerts and sounds.
     */
    private static void checkForNearbyHeads() {
        // Only run in the End during a slayer quest
        if (!Utils.isInTheEnd() || !SlayerManager.isInSlayerType(SlayerType.VOIDGLOOM) || CLIENT.player == null || CLIENT.world == null) {
            nearbyHeads.clear();
            return;
        }

        SlayersConfig.EndermanSlayer config = SkyblockerConfigManager.get().slayers.endermanSlayer;

        // Skip if no features are enabled
        if (!config.highlightNukekubiHeads && !config.nukekubiHeadProximityAlert &&
            !config.nukekubiHeadDirectionalIndicator && !config.nukekubiHeadContinuousSound) {
            nearbyHeads.clear();
            return;
        }

        // Find nearby NukeKubi heads
        nearbyHeads.clear();
        int proximityRange = config.nukekubiHeadProximityRange;

        CLIENT.world.getEntitiesByClass(ArmorStandEntity.class,
                CLIENT.player.getBoundingBox().expand(proximityRange),
                NukekubiHeadTracker::isNukekubiHead)
            .forEach(nearbyHeads::add);

        // Handle proximity alerts and sounds
        if (!nearbyHeads.isEmpty()) {
            // Show action bar message
            if (config.nukekubiHeadProximityAlert) {
                CLIENT.player.sendMessage(
                    Text.literal("⚠ ").formatted(Formatting.RED)
                        .append(Text.literal("NukeKubi Head Nearby! ").formatted(Formatting.GOLD))
                        .append(Text.literal("⚠").formatted(Formatting.RED)),
                    true
                );
            }

            // Play sound
            if (config.nukekubiHeadContinuousSound) {
                soundTimer++;
                if (soundTimer >= SOUND_INTERVAL_TICKS) {
                    CLIENT.getSoundManager().play(
                        PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f, 1.0f)
                    );
                    soundTimer = 0;
                }
            }
        } else {
            soundTimer = 0;
        }
    }

    /**
     * Renders directional indicators pointing to nearby NukeKubi heads.
     *
     * @param context The world render context
     */
    private static void render(WorldRenderContext context) {
        if (CLIENT.player == null || nearbyHeads.isEmpty()) {
            return;
        }

        SlayersConfig.EndermanSlayer config = SkyblockerConfigManager.get().slayers.endermanSlayer;

        // Render directional indicators if enabled
        if (config.nukekubiHeadDirectionalIndicator) {
            for (ArmorStandEntity head : nearbyHeads) {
                Vec3d headPos = head.getPos().add(0, 0.5, 0);
                RenderHelper.renderLineFromCursor(context, headPos, NUKEKUBI_COLOR_COMPONENTS, 1.0f, 3.0f);
            }
        }

        // Render solid color highlight if that style is selected
        if (config.highlightNukekubiHeads &&
            config.nukekubiHeadHighlightStyle == SlayersConfig.EndermanSlayer.NukekubiHeadHighlightStyle.SOLID_COLOR) {
            for (ArmorStandEntity head : nearbyHeads) {
                RenderHelper.renderFilledWithBeaconBeam(
                    context,
                    head.getBlockPos(),
                    NUKEKUBI_COLOR_COMPONENTS,
                    0.5f,
                    true
                );
            }
        }
    }
}
