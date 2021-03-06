package com.uberverse.arkcraft.common.block.unused;

import com.uberverse.arkcraft.ARKCraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ContainerBlockBase extends Block
{
	private int guiID;

	public ContainerBlockBase(Material mat, String name, float hardness, int guiID)
	{
		super(mat);
		this.guiID = guiID;
		this.setCreativeTab(ARKCraft.tabARK);
		this.setHardness(hardness);
		this.setUnlocalizedName(name);
		GameRegistry.registerBlock(this, name);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
			EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if (playerIn.isSneaking()) { return false; }
		playerIn.openGui(ARKCraft.instance(), guiID, worldIn, side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
		return true;	
	}
	
	
}
