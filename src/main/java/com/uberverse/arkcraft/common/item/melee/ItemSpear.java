package com.uberverse.arkcraft.common.item.melee;

import com.uberverse.arkcraft.ARKCraft;
import com.uberverse.arkcraft.common.entity.projectile.EntitySpear;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSpear extends ItemSword
{
	public ItemSpear(ToolMaterial m)
	{
		super(m);
		this.setMaxStackSize(1);
		this.setFull3D();
		this.setCreativeTab(ARKCraft.tabARK);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i)
	{
		if (!entityplayer.inventory.hasItemStack(itemstack)) { return; }

		int j = getMaxItemUseDuration(itemstack) - i;
		float f = j / 20F;
		f = (f * f + f * 2.0F) / 3F;
		if (f < 0.1F) { return; }
		if (f > 1.0F)
		{
			f = 1.0F;
		}

		boolean crit = false;
		if (!entityplayer.onGround && !entityplayer.isInWater())
		{
			crit = true;
		}

		if (entityplayer.capabilities.isCreativeMode || entityplayer.inventory.consumeInventoryItem(this))
		{
			world.playSoundAtEntity(entityplayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.isRemote)
			{
				EntitySpear entitySpear = new EntitySpear(world, entityplayer, f * (1.0F + (crit ? 0.5F : 0F)));
				entitySpear.setIsCritical(crit);
				world.spawnEntity(entitySpear);
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack)
	{
		return 0x11940;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		return EnumAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (playerIn.inventory.hasItemStack(itemStackIn))
		{
			playerIn.setItemInUse(itemStackIn, getMaxItemUseDuration(itemStackIn));
		}
		return itemStackIn;
	}
}