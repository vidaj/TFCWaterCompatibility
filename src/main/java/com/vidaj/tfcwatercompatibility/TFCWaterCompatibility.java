package com.vidaj.tfcwatercompatibility;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCFluids;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
		modid = "tfcwatercompatibility", 
		version = "1.3", 
		dependencies = "required-after:terrafirmacraft;"
				+ "before:Railcraft;"
				+ "before:RotaryCraft;"
				+ "before:ElectriCraft;"
				+ "before:Steamcraft;"
				+ "before:Forestry;"
				+ "before:Thaumcraft")

public class TFCWaterCompatibility {

	public static final Fluid ActualWater = FluidRegistry.WATER;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		replaceWaterInFluidRegistryStaticField();
		replaceWaterInBlocksStaticField();
	}

	private void replaceWaterInFluidRegistryStaticField() {
		try {
			Field field = FluidRegistry.class.getDeclaredField("WATER");
			replaceValueInStaticFinalField(getFreshWater(), field);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot inject custom water", e);
		}
	}

	private void replaceWaterInBlocksStaticField() {
		try {
			Field field = Blocks.class.getDeclaredField("field_150355_j");
			replaceValueInStaticFinalField(TFCBlocks.FreshWater, field);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot replace vanilla water block", e);
		}
	}
	
	private void replaceValueInStaticFinalField(Object itemToReplace,
			Field field) throws Exception, IllegalAccessException {
		field.setAccessible(true);
		
		setFieldFinalState(field, false);
		
		field.set(null, itemToReplace);
		
		setFieldFinalState(field, true);
	}
	
	private void setFieldFinalState(Field field, boolean isFinal) throws Exception
	{
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		int modifiers = modifiersField.getInt(field);
		
		if (isFinal) 
		{
			modifiers &= Modifier.FINAL;
		} else 
		{
			modifiers &= ~Modifier.FINAL;
		}
		
		modifiersField.setInt(field, modifiers);
	}
	
	private Fluid getFreshWater() 
	{
		return TFCFluids.FRESHWATER;
	}
}
