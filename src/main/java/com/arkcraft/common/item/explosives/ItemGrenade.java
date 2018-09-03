package com.arkcraft.common.item.explosives;

import com.arkcraft.common.entity.projectile.EntityGrenade;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ItemGrenade extends Item {

	public ItemGrenade() {
		super();
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLiving, int timeLeft) {
		if (!(entityLiving instanceof EntityPlayer)) return;
		EntityPlayer entityplayer = (EntityPlayer) entityLiving;
		if (!entityplayer.inventory.hasItemStack(itemstack)) {
			return;
		}

		int j = getMaxItemUseDuration(itemstack) - timeLeft;
		float f = j / 20F;
		f = (f * f + f * 2.0F) / 3F;
		if (f < 0.1F) {
			return;
		}
		if (f > 1.0F) {
			f = 1.0F;
		}

		if (entityplayer.capabilities.isCreativeMode || entityplayer.inventory.hasItemStack(itemstack)) {
			world.playSound(entityplayer, entityplayer.getPosition(), SoundEvent.REGISTRY.getObject(new ResourceLocation("random.bow")), SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.isRemote) {
				EntityGrenade entiyGrenade = new EntityGrenade(world, entityplayer);
				world.spawnEntity(entiyGrenade);
			}
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 0x11940;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemstack = playerIn.getHeldItem(hand);
		//if (playerIn.inventory.hasItem(this)) {
		playerIn.setActiveHand(hand);
		//playerIn.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		//}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

}
