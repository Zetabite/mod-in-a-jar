package zetabite.mods_in_a_jar;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.random.RandomGenerator;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zetabite.mods_in_a_jar.item.ModRegistry;
import zetabite.mods_in_a_jar.item.model.ModCookieModel;

import java.util.HashMap;
import java.util.Map;

public class ModsInAJar implements ModInitializer {
	public static final String MODID = "mods_in_a_jar";
	public static final String MODNAME = "Mods in a Jar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODNAME);
	public static final Map<String, ModCookieModel> modCookieModels = new HashMap<>();
	public static final ItemGroup ITEM_GROUP = QuiltItemGroup.builder(new Identifier(MODID, "general")).icon(() -> new ItemStack(ModRegistry.COOKIE_JAR_ITEM)
	).build();
	private static final String[] messages = new String[] {
			"{} is quite jarring.",
			"Pickling {} for fine taste.",
			"{}. Another one for the mod jar."
	};

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Going on mod hunt.");

		for (ModContainer modContainer : QuiltLoader.getAllMods()) {
			int random = RandomGenerator.createThreaded().range(0, messages.length - 1);
			String message = messages[random];
			LOGGER.info(message, modContainer.metadata().name());
			//modCookieModels.put(modContainer.metadata().id(), new ModCookieModel(modContainer.metadata()));
		}

		ModRegistry.onInitialize();
	}
}
