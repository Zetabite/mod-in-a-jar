package zetabite.mods_in_a_jar.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import zetabite.mods_in_a_jar.item.ModCookie;
import zetabite.mods_in_a_jar.item.ModRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CookieJarEntity extends BlockEntity {
	private final Stack<ItemStack> cookieStack = new Stack<>();
	private static final int MAX_COOKIES = 8;

	public CookieJarEntity(BlockPos blockPos, BlockState blockState) {
		super(ModRegistry.COOKIE_JAR_ENTITY, blockPos, blockState);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		if (nbt.contains("Cookies")) {
			NbtList cookies = nbt.getList("Cookies", 10);

			for (int i = 0; i < cookies.size(); i++) {
				ItemStack cookie = ItemStack.fromNbt(cookies.getCompound(i));
				if (!cookie.isEmpty()) cookieStack.push(cookie);
			}
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		NbtList cookieList = new NbtList();

		for (ItemStack cookie : new ArrayList<>(cookieStack)) {
			cookieList.add(cookie.getNbt());
		}
		nbt.put("Cookies", cookieList);
	}

	public boolean addCookie(ItemStack cookie) {
		if (cookie.getItem() instanceof ModCookie) {
			if (cookieStack.size() < MAX_COOKIES) {
				cookieStack.push(cookie.split(1));
				return true;
			}
		} else if (Registry.ITEM.getId(cookie.getItem()).getPath().equals("cookie")) {
			if (cookieStack.size() < MAX_COOKIES) {
				cookieStack.push(cookie.split(1));
				return true;
			}
		}
		return false;
	}

	public ItemStack takeCookie() {
		if (cookieStack.size() > 0) {
			return cookieStack.pop();
		}
		return ItemStack.EMPTY;
	}

	public List<ItemStack> getCookies() {
		return new ArrayList<>(cookieStack);
	}
}
