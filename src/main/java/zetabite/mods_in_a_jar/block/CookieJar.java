package zetabite.mods_in_a_jar.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import zetabite.mods_in_a_jar.block.entity.CookieJarEntity;
import zetabite.mods_in_a_jar.item.ModCookie;

import static zetabite.mods_in_a_jar.item.ModRegistry.COOKIE_JAR_SOUND_EVENT;

public class CookieJar extends BlockWithEntity {
	private static final VoxelShape SHAPE = makeShape();

	public CookieJar(Settings settings) {
		super(settings);
	}

	public static VoxelShape makeShape(){
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.8125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.8125, 0.3125, 0.6875, 0.875, 0.6875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.875, 0.25, 0.75, 1, 0.75));

		return shape;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!(world instanceof ServerWorld)) return ActionResult.SUCCESS;
		if (world.getBlockEntity(pos) instanceof CookieJarEntity cookieJar) {
			if (player.getStackInHand(hand).getItem() instanceof ModCookie) {
				if (cookieJar.addCookie(player.getStackInHand(hand))) {
					world.playSound(null, pos, COOKIE_JAR_SOUND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
					return ActionResult.CONSUME;
				}
			} else if (Registry.ITEM.getId(player.getStackInHand(hand).getItem()).getPath().equals("cookie")) {
				if (cookieJar.addCookie(player.getStackInHand(hand))) {
					world.playSound(null, pos, COOKIE_JAR_SOUND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
					return ActionResult.CONSUME;
				}
			}
			ItemStack cookie = cookieJar.takeCookie();

			if (!cookie.isEmpty()) {
				if (!player.getInventory().insertStack(cookie)) {
					ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), cookie);
				}
				world.playSound(null, pos, COOKIE_JAR_SOUND_EVENT, SoundCategory.BLOCKS, 1f, 1f);
				return ActionResult.CONSUME;
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
		if (world instanceof ServerWorld serverWorld) {
			if (world.getBlockEntity(pos) instanceof CookieJarEntity cookieJar) {
				for (ItemStack cookie : cookieJar.getCookies()) {
					ItemScatterer.spawn(serverWorld, pos.getX(), pos.getY(), pos.getZ(), cookie);
				}
			}
		}
		super.onBroken(world, pos, state);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CookieJarEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
