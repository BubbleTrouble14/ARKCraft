package com.arkcraft.common.network.player;

import com.arkcraft.init.ARKCraftItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Used so the player can poop
 *
 * @author William
 */
public class PlayerPoop implements IMessage {
	boolean doIt; // Not used yet
	// TODO error here -> check

	/**
	 * Don't use
	 */
	public PlayerPoop() {
	}

	public PlayerPoop(boolean doIt) {
		this.doIt = doIt;
	}

	static void processMessage(PlayerPoop message, EntityPlayerMP player) {
		if (player != null) {
			player.dropItem(ARKCraftItems.player_feces, 1);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.doIt = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.doIt);
	}

	public static class Handler implements IMessageHandler<PlayerPoop, IMessage> {
		@Override
		public IMessage onMessage(final PlayerPoop message, MessageContext ctx) {
			if (ctx.side != Side.SERVER) {
				System.err.println("MPUpdateDoCraft received on wrong side:" + ctx.side);
				return null;
			}
			final EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServer().addScheduledTask(() -> processMessage(message, player));
			return null;
		}
	}
}
