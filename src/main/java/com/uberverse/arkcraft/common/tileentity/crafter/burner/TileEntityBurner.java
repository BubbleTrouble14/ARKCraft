package com.uberverse.arkcraft.common.tileentity.crafter.burner;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import com.uberverse.arkcraft.common.burner.BurnerManager.BurnerFuel;
import com.uberverse.arkcraft.common.burner.BurnerManager.BurnerRecipe;
import com.uberverse.arkcraft.common.burner.IBurner;
import com.uberverse.arkcraft.common.item.ItemFuel;
import com.uberverse.arkcraft.common.tileentity.IDecayer;
import com.uberverse.arkcraft.common.tileentity.crafter.TileEntityArkCraft;

/**
 * @author Lewis_McReu
 */
public abstract class TileEntityBurner extends TileEntityArkCraft implements IInventory, ITickable, IBurner, IDecayer
{
	private ItemStack[] inventory;
	/** the currently active recipes */
	private Map<BurnerRecipe, Integer> activeRecipes = new HashMap<>();
	/** the ticks burning left */
	private int burningTicks;
	private boolean burning;
	private BurnerFuel currentFuel;

	public TileEntityBurner()
	{
		super();
		inventory = new ItemStack[getSizeInventory()];
		burning = false;
	}

	@Override
	public void updateBurner() {
		IBurner.super.updateBurner();
	}

	@Override
	public void setBurning(boolean burning)
	{
		this.burning = burning;
	}

	@Override
	public int getBurningTicks()
	{
		return burningTicks;
	}

	@Override
	public void setBurningTicks(int burningTicks)
	{
		this.burningTicks = burningTicks;
	}

	@Override
	public boolean isBurning()
	{
		return burning;
	}

	@Override
	public Map<BurnerRecipe, Integer> getActiveRecipes()
	{
		return activeRecipes;
	}

	@Override
	public void sync()
	{
		markDirty();
		markBlockForUpdate(getWorld(), pos);
	}

	@Override
	public World getWorldIA()
	{
		return world;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		IBurner.super.writeToNBT(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		IBurner.super.readFromNBT(compound);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return new SPacketUpdateTileEntity(this.pos, 0, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return index >= 0 && index < getSizeInventory() ? inventory[index] : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		getStackInSlot(index).stackSize -= count;
		return new ItemStack(getStackInSlot(index).getItem(), count);
	}

	/*@Override
	public ItemStack getStackInSlotOnClosing(int index)
	{
		return getStackInSlot(index);
	}*/

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		if (index >= 0) inventory[index] = stack;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return Integer.MAX_VALUE;
	}

	// TODO option to do tribe access stuff here
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{}

	@Override
	public void closeInventory(EntityPlayer player)
	{}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return stack != null ? stack.getItem() instanceof ItemFuel : false;
	}

	@Override
	public int getField(int id)
	{
		return id == 0 ? burningTicks : id == 1 ? burning ? 1 : 0 : 0;
	}

	@Override
	public void setField(int id, int value)
	{
		switch (id)
		{
		case 0:
			burningTicks = value;
			break;
		case 1:
			burning = value == 0 ? false : true;
			break;
		}
	}

	@Override
	public int getFieldCount()
	{
		return 2;
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < getSizeInventory(); i++)
			inventory[i] = null;
	}

	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return null;
	}

	@Override
	public IInventory getIInventory()
	{
		return this;
	}

	@Override
	public BlockPos getPosition()
	{
		return pos;
	}

	@Override
	public BurnerFuel getCurrentFuel()
	{
		return currentFuel;
	}

	@Override
	public void setCurrentFuel(BurnerFuel fuel)
	{
		this.currentFuel = fuel;
	}

	@Override
	public ItemStack[] getInventory()
	{
		return inventory;
	}
	@Override
	public void update() {
		//TODO: Called on server update
	}
}
