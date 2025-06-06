package de.hysky.skyblocker.config.configs;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.resource.language.I18n;

public class SlayersConfig {
	@SerialEntry
	public HighlightSlayerEntities highlightMinis = HighlightSlayerEntities.GLOW;

	@SerialEntry
	public HighlightSlayerEntities highlightBosses = HighlightSlayerEntities.GLOW;

	@SerialEntry
	public boolean displayBossbar = true;

	public enum HighlightSlayerEntities {
		OFF, GLOW, HITBOX;

		@Override
		public String toString() {
			return I18n.translate("skyblocker.config.slayer.highlightBosses." + name());
		}
	}

	@SerialEntry
	public boolean bossSpawnAlert = true;

	@SerialEntry
	public boolean miniBossSpawnAlert = true;

	@SerialEntry
	public boolean slainTime = true;

	@SerialEntry
	public boolean enableHud = true;

	@SerialEntry
	public EndermanSlayer endermanSlayer = new EndermanSlayer();

	@SerialEntry
	public VampireSlayer vampireSlayer = new VampireSlayer();

	@SerialEntry
	public BlazeSlayer blazeSlayer = new BlazeSlayer();

	public static class EndermanSlayer {
		@SerialEntry
		public boolean enableYangGlyphsNotification = true;

		@SerialEntry
		public boolean highlightBeacons = true;

		@SerialEntry
		public boolean highlightNukekubiHeads = true;

		@SerialEntry
		public NukekubiHeadHighlightStyle nukekubiHeadHighlightStyle = NukekubiHeadHighlightStyle.GLOW;

		@SerialEntry
		public boolean nukekubiHeadProximityAlert = true;

		@SerialEntry
		public int nukekubiHeadProximityRange = 30;

		@SerialEntry
		public boolean nukekubiHeadDirectionalIndicator = true;

		@SerialEntry
		public boolean nukekubiHeadContinuousSound = true;

		@SerialEntry
		public boolean lazerTimer = true;

		public enum NukekubiHeadHighlightStyle {
			GLOW, SOLID_COLOR;

			@Override
			public String toString() {
				return I18n.translate("skyblocker.config.slayer.endermanSlayer.nukekubiHeadHighlightStyle." + name());
			}
		}
	}

	public static class VampireSlayer {
		@SerialEntry
		public boolean enableEffigyWaypoints = true;

		@SerialEntry
		public boolean compactEffigyWaypoints;

		@SerialEntry
		public int effigyUpdateFrequency = 5;

		@SerialEntry
		public boolean enableHolyIceIndicator = true;

		@SerialEntry
		public int holyIceIndicatorTickDelay = 5;

		@SerialEntry
		public int holyIceUpdateFrequency = 5;

		@SerialEntry
		public boolean enableHealingMelonIndicator = true;

		@SerialEntry
		public float healingMelonHealthThreshold = 4f;

		@SerialEntry
		public boolean enableSteakStakeIndicator = true;

		@SerialEntry
		public int steakStakeUpdateFrequency = 5;

		@SerialEntry
		public boolean enableManiaIndicator = true;

		@SerialEntry
		public int maniaUpdateFrequency = 5;
	}

	public static class BlazeSlayer {
		@SerialEntry
		public FirePillar firePillarCountdown = FirePillar.SOUND_AND_VISUAL;

		@SerialEntry
		public Boolean attunementHighlights = true;

		public enum FirePillar {
			OFF,
			VISUAL,
			SOUND_AND_VISUAL;

			@Override
			public String toString() {
				return I18n.translate("skyblocker.config.slayer.blazeSlayer.enableFirePillarAnnouncer.mode." + name());
			}
		}
	}
}
