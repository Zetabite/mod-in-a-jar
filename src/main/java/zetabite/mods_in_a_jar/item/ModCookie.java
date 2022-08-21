package zetabite.mods_in_a_jar.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.quiltmc.loader.api.ModMetadata;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static zetabite.mods_in_a_jar.ModsInAJar.ITEM_GROUP;

public class ModCookie extends Item {
	public static final String EFFECTS_KEY = "Effects";
	public static final String EFFECT_ID_KEY = "EffectId";
	public static final String EFFECT_DURATION_KEY = "EffectDuration";
	public static final String EFFECT_STRENGTH_KEY = "EffectStrength";
	private static final Map<String, ModCookieEffect> EFFECT_CACHE = new HashMap<>();

	public ModCookie() {
		super((new QuiltItemSettings()).maxCount(64).food(FoodComponents.COOKIE).group(ITEM_GROUP));
	}

	public static void addEffectToCookie(ItemStack cookie, StatusEffect effect, int duration, int strength) {
		NbtCompound nbtCompound = cookie.getOrCreateNbt();
		NbtList nbtList = nbtCompound.getList(EFFECTS_KEY, 9);
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.putInt(EFFECT_ID_KEY, StatusEffect.getRawId(effect));
		nbtCompound2.putInt(EFFECT_DURATION_KEY, duration);
		nbtCompound2.putInt(EFFECT_STRENGTH_KEY, strength);
		nbtList.add(nbtCompound2);
		nbtCompound.put(EFFECTS_KEY, nbtList);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		NbtCompound nbtCompound = stack.getNbt();

		if (nbtCompound != null && nbtCompound.contains(EFFECTS_KEY, 9)) {
			NbtList nbtList = nbtCompound.getList(EFFECTS_KEY, 10);

			for(int i = 0; i < nbtList.size(); ++i) {
				int duration = 160;
				int strength = 1;
				NbtCompound effectDataCompound = nbtList.getCompound(i);

				if (effectDataCompound.contains(EFFECT_DURATION_KEY, 3)) {
					duration = effectDataCompound.getInt(EFFECT_DURATION_KEY);
				}
				if (effectDataCompound.contains(EFFECT_STRENGTH_KEY)) {
					strength = effectDataCompound.getInt(EFFECT_STRENGTH_KEY);
				}

				StatusEffect statusEffect = StatusEffect.byRawId(effectDataCompound.getInt(EFFECT_ID_KEY));
				if (statusEffect != null) {
					user.addStatusEffect(new StatusEffectInstance(statusEffect, duration, strength));
				}
			}
		}

		if (user instanceof PlayerEntity && ((PlayerEntity)user).getAbilities().creativeMode) {
			return itemStack;
		} else {
			itemStack.decrement(1);
			return itemStack;
		}
	}

	public static ItemStack create() {
		int random = RandomGenerator.createThreaded().range(0, QuiltLoader.getAllMods().size() - 1);
		ModMetadata modMetadata = Collections.list(Collections.enumeration(QuiltLoader.getAllMods())).get(random).metadata();
		ModCookieEffect modCookieEffect = EFFECT_CACHE.computeIfAbsent(modMetadata.id(), ModCookieEffect::getEffect);
		ItemStack modCookieItemStack = new ItemStack(ModRegistry.MOD_COOKIE);
		NbtCompound nbtCompound = modCookieItemStack.getOrCreateNbt();
		nbtCompound.putString("ModName", modMetadata.name());
		nbtCompound.putString("ModID", modMetadata.id());
		addEffectToCookie(modCookieItemStack, StatusEffect.byRawId(modCookieEffect.getEffectID()), modCookieEffect.getEffectDuration(), modCookieEffect.getEffectStrength());
		return modCookieItemStack;
	}

	@Override
	public Text getName(ItemStack stack) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();

		if (nbtCompound.contains("ModName")) {
			return Text.of(nbtCompound.getString("ModName") + " " + Text.translatable(this.getTranslationKey()).getString());
		}
		return Text.translatable(this.getTranslationKey());
	}

	public record ModCookieEffect(int effectID, int effectDuration, int effectStrength) {
		public static ModCookieEffect getEffect(String modid) {
			int effectCount = Registry.STATUS_EFFECT.size();

			int hash = 0;

			for (int i = 0; i < modid.length(); i++) {
				hash += modid.charAt(i);
			}

			return new ModCookieEffect(hash % effectCount, hash, hash % 42);
		}

		public int getEffectID() {
			return effectID;
		}

		public int getEffectDuration() {
			return effectDuration;
		}

		public int getEffectStrength() {
			return effectStrength;
		}
	}
}
