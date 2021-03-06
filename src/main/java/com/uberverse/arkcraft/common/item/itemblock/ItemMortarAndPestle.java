package com.uberverse.arkcraft.common.item.itemblock;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.uberverse.arkcraft.init.ARKCraftBlocks;

public class ItemMortarAndPestle extends ItemBlockARK
{
	public ItemMortarAndPestle(Block block)
	{
		super(block);
		this.setMaxStackSize(1);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ)
	{
		boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
		BlockPos blockpos1 = flag ? pos : pos.offset(side);

		if (!playerIn.canPlayerEdit(blockpos1, side, stack))
		{
			return false;
		}
		else
		{
			Block block = worldIn.getBlockState(blockpos1).getBlock();

			if (!worldIn.canBlockBePlaced(block, blockpos1, false, side, (Entity) null, stack))
			{
				return false;
			}
			else if (ARKCraftBlocks.mortarAndPestle.canPlaceBlockAt(worldIn, blockpos1))
			{
				--stack.stackSize;
				worldIn.setBlockState(blockpos1, ARKCraftBlocks.mortarAndPestle.getDefaultState());
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
		BlockPos blockpos1 = flag ? pos : pos.offset(side);

		if (!playerIn.canPlayerEdit(blockpos1, side, stack))
		{
			return EnumActionResult.FAIL;
		}
		else
		{
			Block block = worldIn.getBlockState(blockpos1).getBlock();

			if (!worldIn.canBlockBePlaced(block, blockpos1, false, side, (Entity) null, stack))
			{
				return EnumActionResult.FAIL;
			}
			else if (ARKCraftBlocks.mortarAndPestle.canPlaceBlockAt(worldIn, blockpos1))
			{
				--stack.stackSize;
				worldIn.setBlockState(blockpos1, ARKCraftBlocks.mortarAndPestle.getDefaultState());
				return EnumActionResult.SUCCESS;
			}
			else
			{
				return EnumActionResult.FAIL;
			}
		}
	}
}
