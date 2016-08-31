package com.uberverse.arkcraft.rework.gui;

import com.uberverse.arkcraft.I18n;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiTranslatedButton extends GuiButtonExt
{
	// TODO maybe change current buttons to translated ones (display issues with overlap)
	public GuiTranslatedButton(int buttonId, int x, int y, String textKey, int spacing)
	{
		super(buttonId, x, y, Minecraft.getMinecraft().fontRendererObj.getStringWidth(I18n.format(textKey)) + spacing,
				Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + spacing, I18n.format(textKey));
	}
}