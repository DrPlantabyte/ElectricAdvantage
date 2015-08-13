package cyano.electricadvantage.init;

import net.minecraft.util.DamageSource;
import cyano.poweradvantage.api.ConduitType;

public abstract class Power {

	public static final ConduitType ELECTRIC_POWER = new ConduitType("electricity");
	// 1 unit of electricity is 1 kJ of energy, 1 piece of coal has 50 MJ in it
	public static final float STEAM_TO_ELECTRICITY = 31.25f;
	public static final float ELECTRICITY_TO_STEAM = 1.0f / STEAM_TO_ELECTRICITY;

	// made from lithium
	public static final float LITHIUM_BATTERY_CAPACITY = 8000f;
	// made from manganese and zinc
	public static final float ALKALINE_BATTERY_CAPACITY = 4000f;
	// made from nickel and manganese
	public static final float NICKEL_HYDRIDE_BATTERY_CAPACITY = 2000f;
	// made from lead and sulfur
	public static final float LEAD_ACID_BATTERY_CAPACITY = 1000f;
	
}
