package com.uberverse.arkcraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.uberverse.arkcraft.client.event.ClientEventHandler;

public class GuiOverlayGetResources extends Gui
{
	private static final Minecraft mc = Minecraft.getMinecraft();
	public boolean allowGuiOpen;
	public static int count = 0;
	
	/*
	public void test()
	{
		System.out.println(ClientEventHandler.openOverlay());
		//ClientEventHandler evt = new ClientEventHandler();
		if (allowGuiOpen != ClientEventHandler.openOverlay())
		{
			allowGuiOpen = true;
			System.out.println(allowGuiOpen);
		}
	}	*/
	
	@SubscribeEvent
	public void renderGUIOverlay(RenderGameOverlayEvent.Post e)
	{
		
		System.out.println(ClientEventHandler.openOverlay());
		//ClientEventHandler evt = new ClientEventHandler();
		if (allowGuiOpen != ClientEventHandler.openOverlay())
		{
			allowGuiOpen = true;
			System.out.println(allowGuiOpen);
		}
		
		EntityPlayer p = mc.thePlayer;
		ItemStack stack = p.getCurrentEquippedItem();
		ClientEventHandler handler = new ClientEventHandler();
	//	System.out.println("GuiOverlay" + handler.doOverlay());
		
	
			if (e.type.equals(ElementType.HELMET) )
			{
		//		System.out.println("ran code");
			
			/*
			if (stack != null && handler.doOverlay())
			{
				String text = "";
				if (!p.capabilities.isCreativeMode)
				{
					ItemRangedWeapon weapon = (ItemRangedWeapon) stack.getItem();
					text = weapon.getAmmoQuantity(stack) + "/" + weapon.getAmmoQuantityInInventory(
							stack, p);
				}
				else
				{
					text = '\u221e' + "";
				}
				int x = e.resolution.getScaledWidth() - 4 - mc.fontRendererObj.getStringWidth(text);
				int y = 20;
				drawString(mc.fontRendererObj, text, x, y - 16, 0xFFFFFFFF);
			}*/
		}	
			
		
	}
}
