package cyano.electricadvantage.init;

import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.registry.MachineGUIRegistry;
import cyano.electricadvantage.gui.*;

public abstract class GUI {

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;
		
		Blocks.init();
		Entities.init();

		Blocks.steam_powered_generator.setGuiID(MachineGUIRegistry.addGUI(new PowerGeneratorGUI()), PowerAdvantage.getInstance());
		Blocks.photovoltaic_generator.setGuiID(MachineGUIRegistry.addGUI(new PowerGeneratorGUI()), PowerAdvantage.getInstance());

		Blocks.arc_furnace.setGuiID(MachineGUIRegistry.addGUI(new ArcFurnaceGUI()), PowerAdvantage.getInstance());
		Blocks.battery_array.setGuiID(MachineGUIRegistry.addGUI(new BatteryArrayGUI()), PowerAdvantage.getInstance());
		Blocks.rock_crusher.setGuiID(MachineGUIRegistry.addGUI(new RockCrusherGUI()), PowerAdvantage.getInstance());
		Blocks.laser_turret.setGuiID(MachineGUIRegistry.addGUI(new GenericMachineGUI()), PowerAdvantage.getInstance());
		Blocks.laser_drill.setGuiID(MachineGUIRegistry.addGUI(new ElectricDrillGUI()), PowerAdvantage.getInstance());
		Blocks.fabricator.setGuiID(MachineGUIRegistry.addGUI(new FabricatorGUI()), PowerAdvantage.getInstance());
		Blocks.growth_chamber.setGuiID(MachineGUIRegistry.addGUI(new GrowthChamberGUI()), PowerAdvantage.getInstance());
		Blocks.growth_chamber_controller.setGuiID(MachineGUIRegistry.addGUI(new GrowthChamberControllerGUI()), PowerAdvantage.getInstance());
		Blocks.oven.setGuiID(MachineGUIRegistry.addGUI(new OvenGUI()), PowerAdvantage.getInstance());
		
		Blocks.electric_pump.setGuiID(MachineGUIRegistry.addGUI(new ElectricPumpGUI()), PowerAdvantage.getInstance());
		Blocks.electric_still.setGuiID(MachineGUIRegistry.addGUI(new ElectricStillGUI()), PowerAdvantage.getInstance());
		Blocks.plastic_refinery.setGuiID(MachineGUIRegistry.addGUI(new PlasticRefineryGUI()), PowerAdvantage.getInstance());
		
		initDone = true;
	}
}
