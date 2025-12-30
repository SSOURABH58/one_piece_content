package de.one_piece_content;

import de.one_piece_api.server.ability.PassiveAbility;
import de.one_piece_api.server.ability.PassiveAbilityRegistry;
import de.one_piece_content.entity.OnePieceEntities;
import de.one_piece_content.spells.SpellHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "one_piece_content";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Example Content initializing");
		de.one_piece_content.config.SandSpikeConfig.load();
		LOGGER.info("Registering Sounds...");
		// MySounds.register();
		LOGGER.info("Registering SpellHandlers...");
		SpellHandler.register();
		LOGGER.info("Registering Entities...");
		OnePieceEntities.register();
		LOGGER.info("Initialization Complete.");

		PassiveAbilityRegistry.register(new PassiveAbility.Builder(id("master_of_the_seas"))
				.condition(entity -> {
					if (entity instanceof PlayerEntity player) {
						return player.isTouchingWater();
					}
					return false;
				})
				.addStatusEffect(StatusEffects.DOLPHINS_GRACE, 1, 0, true, false, false)
				.addStatusEffect(StatusEffects.WATER_BREATHING, 1, 0, true, false, false)
				.addCustomEffect(entity -> {

				}).build());
	}

	public static Identifier id(String name) {
		return Identifier.of(MOD_ID, name);
	}

}