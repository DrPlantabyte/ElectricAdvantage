package cyano.electricadvantage.init;

import net.minecraft.util.DamageSource;
import cyano.poweradvantage.api.ConduitType;

public abstract class Power {

	public static final ConduitType ELECTRIC_POWER = new ConduitType("electricity");
	// 1 unit of electricity is 1 kJ of energy, 1 piece of coal has 50 MJ in it
	public static float STEAM_TO_ELECTRICITY = 31.25f;
	public static float ELECTRICITY_TO_STEAM = 1.0f / STEAM_TO_ELECTRICITY;
	
}
