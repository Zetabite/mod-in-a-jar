package zetabite.mods_in_a_jar;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import zetabite.mods_in_a_jar.item.ModRegistry;

@Environment(EnvType.CLIENT)
public class ModsInAJarClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		ModRegistry.onInitializeClient();
	}
}
