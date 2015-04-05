package com.vidaj.tfcwatercompatibility;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.bioxx.tfc.api.TFCFluids;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.Fluid;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
		modid = "tfcwatercompatibility", 
		version = "1.1", 
		dependencies = "required-after:terrafirmacraft;"
				+ "before:Railcraft;"
				+ "before:RotaryCraft;"
				+ "before:ElectriCraft;"
				+ "before:Steamcraft;"
				+ "before:Forestry;"
				+ "before:Thaumcraft")
public class TFCWaterCompatibility {

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		replaceWaterInFluidRegistryMap();
		replaceWaterInFluidRegistryStaticField();
	}

	private void replaceWaterInFluidRegistryMap() {
		try {
			Field fluidsField = FluidRegistry.class.getDeclaredField("fluids");
			fluidsField.setAccessible(true);
			
			HashMap<String, Fluid> fluids = (HashMap<String, Fluid>) fluidsField.get(null);
			fluids.put("water", getFreshWater());
		} catch (Exception e) {
			throw new IllegalStateException("Cannot find fluids");
		}
	}

	private void replaceWaterInFluidRegistryStaticField() {
		try {
			Field field = FluidRegistry.class.getDeclaredField("WATER");
			field.setAccessible(true);
			
			setFieldFinalState(field, false);
			
			field.set(null, getFreshWater());
			
			setFieldFinalState(field, true);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot inject custom water", e);
		}
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
