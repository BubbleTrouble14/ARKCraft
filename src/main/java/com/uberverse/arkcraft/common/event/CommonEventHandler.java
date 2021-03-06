package com.uberverse.arkcraft.common.event;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import com.uberverse.arkcraft.ARKCraft;
import com.uberverse.arkcraft.common.arkplayer.ARKPlayer;
import com.uberverse.arkcraft.common.config.ModuleItemBalance;
import com.uberverse.arkcraft.common.item.IDecayable;
import com.uberverse.arkcraft.common.item.ranged.ItemRangedWeapon;
import com.uberverse.arkcraft.common.network.ReloadFinished;
import com.uberverse.arkcraft.init.ARKCraftItems;
import com.uberverse.arkcraft.util.Utils;
import com.uberverse.lib.LogHelper;

import com.google.common.collect.ImmutableSet;

public class CommonEventHandler
{
	public boolean destroy;
	public boolean destroyBlocks;
	public boolean startSwing;

	public static File f;
	public static PrintWriter out;

	public static void init()
	{
		CommonEventHandler handler = new CommonEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
	}

	/*@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing event)
	{
		if (event.getEntity() instanceof EntityPlayer)
		{
			ARKPlayer.register((EntityPlayer) event.getEntity());
			if (event.getEntity().world.isRemote) // On client
			{
				LogHelper.info("ARKPlayerEventHandler: Registered a new ARKPlayer on client.");
			}
			else
			{
				LogHelper.info("ARKPlayerEventHandler: Registered a new ARKPlayer on server.");
			}
		}
	}*///Replaced by PlayerCommonEventHandler.attachCapability

	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone event)
	{
		LogHelper.info("ARKPlayerEventHandler: Cloning player extended properties");
		ARKPlayer.get(event.getEntityPlayer()).copy(ARKPlayer.get(event.getOriginal()));
	}

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event)
	{
		// LogHelper.info("LIVING UPDATE EVENT");
		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			// Enable pooping once every (the value in the config) ticks
			if (player.ticksExisted % ModuleItemBalance.PLAYER.TICKS_BETWEEN_PLAYER_POOP == 0)
			{
				ARKPlayer.get(player).feelTheUrge();
			}
		}
	}

	// Immutable Set (Not able to edit the set)
	private static final Set<Item> INPUTS = ImmutableSet.of(Items.BONE, Items.BOOK, Items.WHEAT);

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event)
	{
		if (event.side.isServer())
		{
			World world = event.world;

			EntityItem itemToSpawn = null;
			if (!world.isRemote)
			{
				if (bookSpawnDelay > 0) bookSpawnDelay--;
				else
				{
					List<Entity> entitiesInWorld = world.loadedEntityList;
					for (Entity entityInWorld : entitiesInWorld)
					{
						// Make the set mutable each for loop.
						final Set<Item> remainingInputs = new HashSet<>(INPUTS); // Create
						ArrayList<EntityItem> foundEntityItems = new ArrayList<>();
						if (entityInWorld instanceof EntityItem)
						{
							EntityItem entityItemInWorld = (EntityItem) entityInWorld;
							if (entityItemInWorld.getEntityItem().getItem() == Items.BOOK)
							{
								LogHelper.info("Found an Entity in the world that is a book!");
								remainingInputs.remove(Items.BOOK);
								foundEntityItems.add(entityItemInWorld);
								AxisAlignedBB areaBound = entityItemInWorld.getEntityBoundingBox().expand(3, 3, 3);
								List<Entity> entitiesWithinBound = world.getEntitiesWithinAABBExcludingEntity(
										entityItemInWorld, areaBound);
								for (Entity entityWithinBound : entitiesWithinBound)
								{
									if (entityWithinBound instanceof EntityItem)
									{
										EntityItem entityItemWithinBound = (EntityItem) entityWithinBound;
										if (entityItemWithinBound.getEntityItem().getItem() == Items.BONE)
										{
											LogHelper.info("Found an Entity near the book that is a bone!");
											remainingInputs.remove(Items.BONE);
											if (!remainingInputs.contains(entityItemWithinBound)) foundEntityItems.add(
													entityItemWithinBound);
										}
										else if (entityItemWithinBound.getEntityItem().getItem() == Items.WHEAT)
										{
											LogHelper.info("Found an Entity near the book that is wheat!");
											remainingInputs.remove(Items.WHEAT);
											if (!remainingInputs.contains(entityItemWithinBound)) foundEntityItems.add(
													entityItemWithinBound);
										}
										if (remainingInputs.isEmpty())
										{
											LogHelper.info("All items have been found. The Items hashmap is empty.");
											for (EntityItem foundEntityItem : foundEntityItems)
											{
												Random random = new Random();
												bookSpawnDelay += 100 + random.nextInt(10);
												foundEntityItem.getEntityItem().stackSize--;
												if (foundEntityItem.getEntityItem().stackSize <= 0)
												{
													LogHelper.info("Deleting the Item: " + foundEntityItem
															.getEntityItem().getItem().toString());
													foundEntityItem.setDead();
												}
												itemToSpawn = new EntityItem(world, entityItemInWorld.posX,
														entityItemInWorld.posY, entityItemInWorld.posZ, new ItemStack(
																ARKCraftItems.info_book, 1));

											}
										}

									}
								}

							}
						}
						foundEntityItems.clear();
					}
					if (itemToSpawn != null)
					{
						// Spawn particle and item
						((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, false, itemToSpawn.posX,
								itemToSpawn.posY + 0.5D, itemToSpawn.posZ, 5, 0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
						world.spawnEntity(itemToSpawn);
					}

				}

				if (event.phase == Phase.START)
				{
					TickStorage t = tick.get(event.world.provider.getDimension());
					if (t == null)
					{
						t = new TickStorage();
						tick.put(event.world.provider.getDimension(), t);
					}
					if (t.tick > 20)
					{
						t.tick = 0;
						for (int i = 0; i < event.world.loadedTileEntityList.size(); i++)
						{
							if (event.world.loadedTileEntityList.get(i) instanceof IInventory)
							{// Check
								// for
								// inventories
								// every
								// second
								Utils.checkInventoryForDecayable((IInventory) event.world.loadedTileEntityList.get(i));
							}
						}
					}
					else
					{
						t.tick++;
					}
				}
			}
		}
	}

	private Map<Integer, TickStorage> tick = new HashMap<>();

	public static class TickStorage
	{
		private int tick;
	}

	public static int bookSpawnDelay = 0;

	public static int count;

	// for (int x = -checkSize; x <= checkSize; x++) {
	// for (int z = -checkSize; z <= checkSize; z++) {
	// for (int y = 0; y <= checkSize; y++) {

	private void destroyBlocks(World world, BlockPos pos)
	{
		Collection<BlockPos> done = new HashSet<>();
		Queue<BlockPos> queue = new LinkedList<>();

		queue.add(pos);

		while (!queue.isEmpty())
		{
			pos = queue.remove();
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();

			// world.destroyBlock(pos, true);

			for (int x = i - 1; x <= i + 1; x++)
			{
				for (int z = k - 1; z <= k + 1; z++)
				{
					for (int y = j - 1; y <= j + 1; y++)
					{
						if (x != i && y != j && k != z)
						{

							BlockPos n = new BlockPos(x, y, z);
							IBlockState blockState = world.getBlockState(new BlockPos(x, y, z));
							if (blockState.getBlock() == Blocks.LOG || blockState.getBlock() == Blocks.LOG2)
							{
								if (!done.contains(n)) queue.add(n);
							}
						}
					}
				}
			}
			done.add(pos);
		}
	}

	public static int reloadTicks = 0;
	public static int ticksExsisted = 0;
	public static int ticksSwing = 0;
	public static int ticks = 0;

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent evt)
	{
		EntityPlayer p = evt.player;
		ItemStack stack = p.getHeldItemMainhand();

		if (stack != null && stack.getItem() instanceof ItemRangedWeapon)
		{
			ItemRangedWeapon w = (ItemRangedWeapon) stack.getItem();
			if (w.isReloading(stack))
			{
				if (++reloadTicks >= w.getReloadDuration())
				{
					if (!p.world.isRemote)
					{
						w.setReloading(stack, p, false);
						reloadTicks = 0;
						w.hasAmmoAndConsume(stack, p);
						w.effectReloadDone(stack, p.world, p);
						ARKCraft.modChannel.sendTo(new ReloadFinished(), (EntityPlayerMP) p);
					}
				}
			}
			/*
			else if(w.fired(stack))
			{
				System.out.println(ticks);
				//p.rotationPitch += 4f;
				if (ticks++ == 20)
				{
					p.rotationPitch += 4f;
				//	if (!p.world.isRemote)

				//	{
						float f = p.isSneaking() ? -0.01F : -0.02F;
						double d = -MathHelper.sin((p.rotationYaw / 180F) * 3.141593F) * MathHelper.cos((0 / 180F)
			 * 3.141593F) * f;
						double d1 = MathHelper.cos((p.rotationYaw / 180F) * 3.141593F) * MathHelper.cos((0 / 180F)
			 * 3.141593F) * f;
					//	p.rotationPitch+=
					//	p.rotationPitch -= 5F;
						p.addVelocity(d, 0, d1);
						w.recoilDown(p, w.getRecoil(), w.getRecoilSneaking(), w.getShouldRecoil());
					//	p.addVelocity(d, 0, d1);
						ticks=0;
						w.setFired(stack, p, false);
					//}
				}
			} */
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.HarvestDropsEvent event)
	{
		if (event.getHarvester() != null && !event.getHarvester().getEntityWorld().isRemote && event.getHarvester()
				.getHeldItemMainhand() == null && ARKPlayer.isARKMode(event.getHarvester()) && event.getState()
				.getBlock() instanceof BlockLog)
		{
			ARKPlayer.get(event.getHarvester()).addXP(0.4);
			event.getDrops().clear();
			if (new Random().nextDouble() < 0.5) event.getDrops().add(new ItemStack(ARKCraftItems.wood, 1));
			int thatch = (int) (new Random().nextDouble() * 5);
			event.getDrops().add(new ItemStack(ARKCraftItems.thatch, thatch));
		}
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event)
	{
		if (!event.getEntityPlayer().world.isRemote)
		{
			ItemStack pickedUp = event.getItem().getEntityItem();
			if (pickedUp != null)
			{
				Item in = pickedUp.getItem();
				if (in instanceof IDecayable)
				{
					EntityPlayer p = event.getEntityPlayer();
					for (int i = 0; i < p.inventory.getSizeInventory(); i++)
					{
						ItemStack inInv = p.inventory.getStackInSlot(i);
						if (inInv != null && inInv.getItem() == in)
						{
							long inDecayStart = IDecayable.getDecayStart(inInv);
							long pickDecayStart = IDecayable.getDecayStart(pickedUp);
							int inSize = inInv.stackSize;
							int pickSize = pickedUp.stackSize;
							int maxSize = inInv.getMaxStackSize();

							if (inSize == maxSize) continue;

							int diff = pickSize - (maxSize - inSize);
							if (diff > 0) pickSize = maxSize - inSize;

							long avg = (inDecayStart * inSize + pickDecayStart * pickSize) / (inSize + pickSize);

							pickedUp.stackSize -= pickSize;
							inInv.stackSize += pickSize;
							IDecayable.setDecayStart(inInv, avg);
							event.getEntityPlayer().world.playSound(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((event.getEntityPlayer().getRNG().nextFloat() - event.getEntityPlayer().getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);

							break;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityKilled(LivingDropsEvent event)
	{
		// TODO remove when actual ark creatures are in place and dropping items
		if (event.getEntityLiving().world.isRemote) return;
		Random r = new Random();
		int x = r.nextInt(3) + 1;
		ItemStack meat = new ItemStack(ARKCraftItems.meat_raw, x);
		event.getDrops().add(new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY,
				event.getEntityLiving().posZ, IDecayable.setDecayStart(meat, ARKCraft.proxy.getWorldTime())));
		if (r.nextDouble() < 0.05) event.getDrops().add(new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX,
				event.getEntityLiving().posY, event.getEntityLiving().posZ, IDecayable.setDecayStart(new ItemStack(
						ARKCraftItems.primemeat_raw), ARKCraft.proxy.getWorldTime())));
		if (event.getEntityLiving() instanceof EntitySpider || event.getEntityLiving() instanceof EntitySilverfish
				|| event.getEntityLiving() instanceof EntityEndermite) event.getDrops().add(new EntityItem(
						event.getEntityLiving().world, event.getEntityLiving().posX, event.getEntityLiving().posY,
						event.getEntityLiving().posZ, new ItemStack(ARKCraftItems.chitin, r.nextInt(3) + 1)));
		else event.getDrops().add(new EntityItem(event.getEntityLiving().world, event.getEntityLiving().posX,
				event.getEntityLiving().posY, event.getEntityLiving().posZ, new ItemStack(ARKCraftItems.hide, r.nextInt(3) + 1)));
	}
}
