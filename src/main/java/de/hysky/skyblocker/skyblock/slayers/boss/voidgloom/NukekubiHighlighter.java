package de.hysky.skyblocker.skyblock.slayers.boss.voidgloom;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.item.HeadTextures;
import de.hysky.skyblocker.skyblock.slayers.SlayerManager;
import de.hysky.skyblocker.skyblock.slayers.SlayerType;
import de.hysky.skyblocker.skyblock.entity.glow.adder.SlayerGlowAdder;
import de.hysky.skyblocker.utils.ColorUtils;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.render.WorldRenderExtractionCallback;
import de.hysky.skyblocker.utils.render.primitive.PrimitiveCollector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.List;

public class NukekubiHighlighter {

    private static float[] colorComponents = {1.0f, 0.0f, 1.0f};
    private static float alpha = 0.75f;
    private static float lineWidth = 3.0f;

    @Init
    public static void init() {
        Color initialColor = SkyblockerConfigManager.get().slayers.endermanSlayer.nukekubiLineColor;
        configCallback(initialColor);
        WorldRenderExtractionCallback.EVENT.register(NukekubiHighlighter::extractRendering);
    }

    private static void extractRendering(PrimitiveCollector collector) {
        var config = SkyblockerConfigManager.get().slayers.endermanSlayer;

        if (!config.highlightNukekubiLines) {
            return;
        }

        if (!Utils.isInTheEnd()) {
            return;
        }

        if (!SlayerManager.isInSlayerType(SlayerType.VOIDGLOOM)) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null) {
            return;
        }

        alpha = config.nukekubiLineAlpha;
        lineWidth = config.nukekubiLineWidth;

        List<ArmorStandEntity> heads = client.world.getEntitiesByClass(
                ArmorStandEntity.class,
                client.player.getBoundingBox().expand(50),
                NukekubiHighlighter::isNukekubiHead
        );

        if (heads.isEmpty()) {
            return;
        }

        for (ArmorStandEntity entity : heads) {
            Vec3d pos = entity.getPos();
            collector.submitLineFromCursor(pos, colorComponents, alpha, lineWidth);
        }
    }

    private static boolean isNukekubiHead(ArmorStandEntity entity) {
        return entity.isMarker()
                && SlayerGlowAdder.isNukekubiHead(entity);
    }

    public static void configCallback(Color color) {
        float[] components = ColorUtils.getFloatComponents(color.getRGB());
        colorComponents = new float[] {components[0], components[1], components[2]};
    }
}