package cyano.electricadvantage.init;

import cyano.basemetals.util.VillagerTradeHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public abstract class Villages {
	// TODO: add machinist villager

	private static boolean initDone = false;
	public static void init(){
		if(initDone) return;

		Entities.init();
		try {
			VillagerTradeHelper.insertTrades(1, 1, 2, new EntityVillager.ListItemForEmeralds(
					Items.petrolplastic_ingot, new EntityVillager.PriceInfo(-8, -4)));
			VillagerTradeHelper.insertTrades(1, 1, 1, new EntityVillager.ListItemForEmeralds(
					Item.getItemFromBlock(Blocks.electric_conduit), new EntityVillager.PriceInfo(-6, -3)));
			VillagerTradeHelper.insertTrades(1, 1, 1, new EntityVillager.ListItemForEmeralds(
					Items.solder, new EntityVillager.PriceInfo(-10, -5)));
			VillagerTradeHelper.insertTrades(1, 1, 1, new EntityVillager.ListItemForEmeralds(
					Items.integrated_circuit, new EntityVillager.PriceInfo(-6, -3)));
			VillagerTradeHelper.insertTrades(1, 1, 1, new EntityVillager.ListItemForEmeralds(
					Items.blank_circuit_board, new EntityVillager.PriceInfo(-6, -3)));
			VillagerTradeHelper.insertTrades(1, 1, 2, new EntityVillager.ListItemForEmeralds(
					Items.power_supply_unit, new EntityVillager.PriceInfo(1, 3)));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			FMLLog.log(Level.ERROR, e, "Failed to add trades to villagers");
		}

		
		initDone = true;
	}
}
