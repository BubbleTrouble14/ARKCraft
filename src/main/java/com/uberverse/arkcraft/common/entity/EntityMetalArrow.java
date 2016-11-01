package com.uberverse.arkcraft.common.entity;

import com.uberverse.arkcraft.init.ARKCraftRangedWeapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityMetalArrow extends EntityArkArrow
{
	public EntityMetalArrow(World worldIn)
	{
		super(worldIn);
		this.setDamage(5);
	}

	public EntityMetalArrow(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
		this.setDamage(5);
	}

	public EntityMetalArrow(World worldIn, EntityLivingBase shooter, float speed, float inaccuracy, double damage, int range)
	{
		super(worldIn, shooter, speed, inaccuracy, damage, range);
		this.setDamage(5);
	}
	
	@Override
	public ItemStack getPickupItem()
	{
		return new ItemStack(ARKCraftRangedWeapons.metal_arrow, 1);
	}
}