package com.uberverse.arkcraft.common.engram;

import com.uberverse.arkcraft.common.block.IExperienceSource;
import com.uberverse.arkcraft.common.engram.EngramManager.Engram;
import com.uberverse.arkcraft.common.entity.IArkLevelable;
import com.uberverse.arkcraft.common.item.Qualitable.ItemQuality;

import net.minecraft.inventory.IInventory;

public class CraftingOrder implements IExperienceSource
{
	private Engram engram;
	private int count;
	private ItemQuality itemQuality;

	public CraftingOrder(Engram engram, int count, ItemQuality itemQuality)
	{
		this.engram = engram;
		this.count = count;
		if (engram != null && engram.isQualitable()) this.itemQuality = itemQuality;
	}

	public CraftingOrder(Engram engram, ItemQuality itemQuality)
	{
		this(engram, 1, itemQuality);
	}

	public CraftingOrder(Engram engram, int count)
	{
		this(engram, count, null);
	}

	public CraftingOrder(Engram engram)
	{
		this(engram, 1);
	}

	public Engram getEngram()
	{
		return engram;
	}

	public void setEngram(Engram engram)
	{
		this.engram = engram;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public void decreaseCount(int decrease)
	{
		count -= decrease;
	}

	public void increaseCount(int increase)
	{
		count += increase;
	}

	public ItemQuality getItemQuality()
	{
		return itemQuality;
	}

	public void setItemQuality(ItemQuality itemQuality)
	{
		this.itemQuality = itemQuality;
	}

	public boolean isQualitable()
	{
		return engram.isQualitable();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CraftingOrder) return ((CraftingOrder) obj).getEngram().equals(engram);
		else if (obj instanceof Engram) return ((Engram) obj).equals(engram);
		return false;
	}

	public boolean canCraft(IInventory inventory, int amount)
	{
		if (isQualitable()) return engram.canCraft(inventory, amount, itemQuality);
		else return engram.canCraft(inventory, amount);
	}

	public boolean canCraft(IInventory inventory)
	{
		return canCraft(inventory, 1);
	}

	public boolean matches(Engram engram, ItemQuality itemQuality)
	{
		return this.engram == engram && (!isQualitable() || this.itemQuality == itemQuality);
	}

	public int getCraftingDuration()
	{
		// TODO change itemquality multiplier
		return (int) (engram.getCraftingTime() * (isQualitable() ? itemQuality.multiplierTreshold : 1));
	}

	@Override
	public void grantXP(IArkLevelable leveling)
	{
		leveling.addXP(getEngram().getExperience() * (isQualitable() ? getItemQuality().multiplierTreshold : 1));
	}
}