/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.features.Feature;
import tk.wurst_client.navigator.PossibleKeybind;
import tk.wurst_client.settings.Setting;
import tk.wurst_client.utils.ChatUtils;
import tk.wurst_client.utils.EntityUtils;
import tk.wurst_client.utils.EntityUtils.TargetSettings;
import tk.wurst_client.utils.MiscUtils;

public abstract class Cmd extends Feature
{
	private String name = getClass().getAnnotation(Info.class).name();
	private String description =
		getClass().getAnnotation(Info.class).description();
	private String[] syntax = getClass().getAnnotation(Info.class).syntax();
	private String tags = getClass().getAnnotation(Info.class).tags();
	private String help = getClass().getAnnotation(Info.class).help();
	protected ArrayList<Setting> settings =
		new ArrayList<Setting>();
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Info
	{
		String name();
		
		String description();
		
		String[] syntax();
		
		String tags() default "";
		
		String help() default "";
	}
	
	public class SyntaxError extends Error
	{
		public SyntaxError()
		{
			super();
		}
		
		public SyntaxError(String message)
		{
			super(message);
		}
	}
	
	public class Error extends Throwable
	{
		public Error()
		{
			super();
		}
		
		public Error(String message)
		{
			super(message);
		}
	}
	
	public final String getCmdName()
	{
		return name;
	}
	
	public final String[] getSyntax()
	{
		return syntax;
	}
	
	@Override
	public final String getName()
	{
		return "." + name;
	}
	
	@Override
	public final String getType()
	{
		return "Command";
	}
	
	@Override
	public final String getDescription()
	{
		String description = this.description;
		if(syntax.length > 0)
			description += "\n\nSyntax:";
		for(String element : syntax)
			description += "\n  ." + name + " " + element;
		return description;
	}
	
	@Override
	public final boolean isEnabled()
	{
		return false;
	}
	
	@Override
	public final boolean isBlocked()
	{
		return false;
	}
	
	@Override
	public final String getTags()
	{
		return tags;
	}
	
	@Override
	public final ArrayList<Setting> getSettings()
	{
		return settings;
	}
	
	@Override
	public final ArrayList<PossibleKeybind> getPossibleKeybinds()
	{
		return new ArrayList<>();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "";
	}
	
	@Override
	public void doPrimaryAction()
	{
		
	}
	
	@Override
	public final String getHelpPage()
	{
		return help;
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[0];
	}
	
	public final void printHelp()
	{
		for(String line : description.split("\n"))
			ChatUtils.message(line);
	}
	
	public final void printSyntax()
	{
		String output = "�o." + name + "�r";
		if(syntax.length != 0)
		{
			output += " " + syntax[0];
			for(int i = 1; i < syntax.length; i++)
				output += "\n    " + syntax[i];
		}
		for(String line : output.split("\n"))
			ChatUtils.message(line);
	}
	
	protected final int[] argsToPos(TargetSettings targetSettings,
		String... args) throws Cmd.Error
	{
		int[] pos = new int[3];
		if(args.length == 3)
		{
			int[] playerPos =
				new int[]{(int)Minecraft.getMinecraft().player.posX,
					(int)Minecraft.getMinecraft().player.posY,
					(int)Minecraft.getMinecraft().player.posZ};
			for(int i = 0; i < args.length; i++)
				if(MiscUtils.isInteger(args[i]))
					pos[i] = Integer.parseInt(args[i]);
				else if(args[i].startsWith("~"))
					if(args[i].equals("~"))
						pos[i] = playerPos[i];
					else if(MiscUtils.isInteger(args[i].substring(1)))
						pos[i] = playerPos[i]
							+ Integer.parseInt(args[i].substring(1));
					else
						syntaxError("Invalid coordinates.");
				else
					syntaxError("Invalid coordinates.");
		}else if(args.length == 1)
		{
			Entity entity =
				EntityUtils.getEntityWithName(args[0], targetSettings);
			if(entity == null)
				error("Entity \"" + args[0] + "\" could not be found.");
			BlockPos blockPos = new BlockPos(entity);
			pos = new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()};
		}else
			syntaxError("Invalid coordinates.");
		return pos;
	}
	
	protected final void syntaxError() throws SyntaxError
	{
		throw new SyntaxError();
	}
	
	protected final void syntaxError(String message) throws SyntaxError
	{
		throw new SyntaxError(message);
	}
	
	protected final void error(String message) throws Error
	{
		throw new Error(message);
	}
	
	public abstract void execute(String[] args) throws Cmd.Error;
}
