/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.events.listeners.LeftClickListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.features.Feature;

@Mod.Info(
	description = "Automatically uses the best tool in your hotbar to\n"
		+ "mine blocks. Tip: This works with Nuker.",
	name = "AutoTool",
	tags = "auto tool",
	help = "Mods/AutoTool")
@Mod.Bypasses
public class AutoToolMod extends Mod
	implements LeftClickListener, UpdateListener
{
	private boolean isActive = false;
	private int oldSlot;
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSwordMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		isActive = false;
		mc.player.inventory.currentItem = oldSlot;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate()
	{
		if(!mc.gameSettings.keyBindAttack.pressed && isActive)
		{
			isActive = false;
			mc.player.inventory.currentItem = oldSlot;
		}else if(isActive && mc.objectMouseOver != null
			&& mc.objectMouseOver.getBlockPos() != null
			&& mc.world.getBlockState(mc.objectMouseOver.getBlockPos())
				.getBlock().getMaterial(null) != Material.AIR)
			setSlot(mc.objectMouseOver.getBlockPos());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onLeftClick()
	{
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		if(mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()
			.getMaterial(null) != Material.AIR)
		{
			isActive = true;
			oldSlot = mc.player.inventory.currentItem;
			setSlot(mc.objectMouseOver.getBlockPos());
		}
	}
	
	public static void setSlot(BlockPos blockPos)
	{
		float bestSpeed = 1F;
		int bestSlot = -1;
		IBlockState blockState = mc.world.getBlockState(blockPos);
		for(int i = 0; i < 9; i++)
		{
			ItemStack item = mc.player.inventory.getStackInSlot(i);
			if(item == null)
				continue;
			float speed = item.getStrVsBlock(blockState);
			if(speed > bestSpeed)
			{
				bestSpeed = speed;
				bestSlot = i;
			}
		}
		if(bestSlot != -1)
			mc.player.inventory.currentItem = bestSlot;
	}
}
