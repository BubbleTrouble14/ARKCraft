package com.uberverse.arkcraft.common.block.crafter;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.uberverse.arkcraft.ARKCraft;
import com.uberverse.arkcraft.common.proxy.CommonProxy;
import com.uberverse.arkcraft.common.tileentity.crafter.burner.TileEntityRefiningForge;

public class BlockRefiningForge extends BlockBurner
{
	public BlockRefiningForge()
	{
		super(Material.ROCK);
		setHardness(2.0f);
		this.setCreativeTab(ARKCraft.tabARK);
		this.setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(PART, EnumPart.BOTTOM));
	}

	@Override
	public int getId()
	{
		return CommonProxy.GUI.REFINING_FORGE.id;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		IBlockState state = getStateFromMeta(meta);
		if (state.getValue(PART).equals(EnumPart.BOTTOM))
			return new TileEntityRefiningForge();
		return null;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		if (state.getValue(PART) == EnumPart.BOTTOM) {
			BlockPos blockpos1 = pos.up();
			if (worldIn.getBlockState(blockpos1).getBlock() == this) {
				worldIn.setBlockToAir(blockpos1);
			}
		}
		else if (state.getValue(PART) == EnumPart.TOP) {
			BlockPos blockpos1 = pos.down();
			if (worldIn.getBlockState(blockpos1).getBlock() == this) {
				worldIn.setBlockToAir(blockpos1);
			}
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		if (state.getValue(PART) == EnumPart.BOTTOM)
			return super.getItemDropped(state, rand, fortune);
		else
			return null;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if (state.getValue(PART) == EnumPart.TOP)
			pos = pos.down();
		return super.getActualState(state, worldIn, pos);
	}

	@Override
	public String getHarvestTool(IBlockState state)
	{
		return "pick";
	}
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
			return true;
		if (state.getValue(PART).equals(EnumPart.TOP))
			pos = pos.down();
		playerIn.openGui(ARKCraft.instance(), getId(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		EnumFacing f = state.getValue(FACING);
		double xOffset = f == EnumFacing.WEST ? 0.75d : f == EnumFacing.EAST ? 0.25d : 0.5d;
		double yOffset = 1.9d;
		double zOffset = f == EnumFacing.NORTH ? 0.75d : f == EnumFacing.SOUTH ? 0.25d : 0.5d;
		IBlockState blockState = getActualState(getDefaultState(), worldIn, pos);
		boolean burning = blockState.getValue(BURNING);
		if (burning) {
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + xOffset, y + yOffset, z + zOffset, 0, 0.05, 0);
			for (int i = 0; i < 5; i++)
				if (rand.nextBoolean())
					worldIn.spawnParticle(EnumParticleTypes.FLAME, x + xOffset + ((double) rand.nextInt(2) - 1) / 10, y + yOffset, z + zOffset + ((double) rand.nextInt(2) - 1) / 10, 0, 0.05, 0);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
		int metaOld = meta;
		EnumPart part = (metaOld & 8) > 0 ? EnumPart.TOP : EnumPart.BOTTOM;
		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(PART, part);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		byte b0 = 0;
		int i = b0 | state.getValue(FACING).getHorizontalIndex();
		if (state.getValue(PART).equals(EnumPart.TOP))
			i |= 8;
		return i;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, BURNING, FACING, PART);
	}

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<EnumPart> PART = PropertyEnum.create("part", EnumPart.class);

	public static enum EnumPart implements IStringSerializable
	{
		TOP, BOTTOM;

		@Override
		public String toString()
		{
			return getName();
		}

		@Override
		public String getName()
		{
			return name().toLowerCase();
		}
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		IBlockState state = worldIn.getBlockState(pos);
		return state.getValue(PART) != EnumPart.TOP;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
}