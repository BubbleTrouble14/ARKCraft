package com.uberverse.arkcraft.common.item.explosives;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import com.uberverse.arkcraft.common.entity.projectile.EntityGrenade;

public class ItemGrenade extends Item
{

	public ItemGrenade()
	{
		super();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i)
	{
		if (!entityplayer.inventory.hasItem(this)) { return; }

		int j = getMaxItemUseDuration(itemstack) - i;
		float f = j / 20F;
		f = (f * f + f * 2.0F) / 3F;
		if (f < 0.1F) { return; }
		if (f > 1.0F)
		{
			f = 1.0F;
		}

		if (entityplayer.capabilities.isCreativeMode || entityplayer.inventory.inventoryItem(this))
		{
			world.playSoundAtEntity(entityplayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.isRemote)
			{
				EntityGrenade entiyGrenade = new EntityGrenade(world, entityplayer);
				world.spawnEntity(entiyGrenade);
			}
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 0x11940;
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if (playerIn.inventory.hasItem(this))
		{
			playerIn.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

}
