package zetabite.mods_in_a_jar.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import zetabite.mods_in_a_jar.block.CookieJar;
import zetabite.mods_in_a_jar.block.entity.CookieJarEntity;

import static zetabite.mods_in_a_jar.ModsInAJar.ITEM_GROUP;
import static zetabite.mods_in_a_jar.ModsInAJar.MODID;

public class ModRegistry {
	public static final Item MOD_COOKIE;
	public static final Block COOKIE_JAR_BLOCK;
	public static final BlockItem COOKIE_JAR_ITEM;
	public static final BlockEntityType<CookieJarEntity> COOKIE_JAR_ENTITY;
	public static final Identifier COOKIE_JAR_SOUND_ID = new Identifier(MODID, "cookie_jar");
	public static SoundEvent COOKIE_JAR_SOUND_EVENT = new SoundEvent(COOKIE_JAR_SOUND_ID);

	static {
		MOD_COOKIE = Registry.register(Registry.ITEM, new Identifier(MODID, "mod_cookie"), new ModCookie());
		COOKIE_JAR_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "cookie_jar"), new CookieJar(QuiltBlockSettings.copyOf(Blocks.GLASS).nonOpaque()));
		COOKIE_JAR_ITEM = Registry.register(Registry.ITEM, new Identifier(MODID, "cookie_jar"), new BlockItem(COOKIE_JAR_BLOCK, (new QuiltItemSettings()).group(ITEM_GROUP)));
		COOKIE_JAR_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "cookie_jar"), QuiltBlockEntityTypeBuilder.create(CookieJarEntity::new, COOKIE_JAR_BLOCK).build());
	}

	public static void onInitialize() {
		DispenserBlock.registerBehavior(MOD_COOKIE, new ItemDispenserBehavior() {
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				Position position = DispenserBlock.getOutputLocation(pointer);
				stack.decrement(1);
				spawnItem(pointer.getWorld(), ModCookie.create(), 6, direction, position);
				return stack;
			}
		});
		Registry.register(Registry.SOUND_EVENT, COOKIE_JAR_SOUND_ID, COOKIE_JAR_SOUND_EVENT);
	}

	@Environment(EnvType.CLIENT)
	public static void onInitializeClient() {
		BlockRenderLayerMap.put(RenderLayer.getCutout(), COOKIE_JAR_BLOCK);
	}
}
