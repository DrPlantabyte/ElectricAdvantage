package cyano.electricadvantage.init;

import cyano.electricadvantage.ElectricAdvantage;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class TreasureChests {

	private static boolean initDone = false;
	public static void init(Path configFolder){
		if(initDone)return;

		Path chestFolder = configFolder.resolve(Paths.get("additional-loot-tables", ElectricAdvantage.MODID,"chests"));
		writeLootFile(chestFolder.resolve("abandoned_mineshaft.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("simple_dungeon.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("village_blacksmith.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("stronghold_corridor.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("stronghold_crossing.json"), LOOT_POOL);
		writeLootFile(chestFolder.resolve("end_city_treasure.json"), LOOT_POOL);

		initDone = true;
	}

	private static void writeLootFile(Path file, String content){
		try {
			Files.createDirectories(file.getParent());
			Files.write(file, Arrays.asList(content), Charset.forName("UTF-8"));
		} catch (IOException e) {
			FMLLog.log(Level.ERROR,e,"Error writing additional-loot-table files");
		}
	}


	private static final String LOOT_POOL = "{\n" +
			"    \"pools\": [\n" +
			"        {\n" +
			"            \"__comment\":\"25% chance of a Electric Advantage item\",\n" +
			"            \"rolls\": 1,\n" +
			"            \"entries\": [\n" +
			"                {\n" +
			"                    \"type\": \"empty\",\n" +
			"                    \"weight\": 144\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:psu\",\n" +
			"                    \"weight\": 6,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 1,\n" +
			"                                \"max\": 4\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:electric_conduit\",\n" +
			"                    \"weight\": 12,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 3,\n" +
			"                                \"max\": 10\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:led_bar\",\n" +
			"                    \"weight\": 6,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 2,\n" +
			"                                \"max\": 8\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:solder\",\n" +
			"                    \"weight\": 6,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 3,\n" +
			"                                \"max\": 12\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:integrated_circuit\",\n" +
			"                    \"weight\": 3,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 5,\n" +
			"                                \"max\": 22\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:lead_acid_battery\",\n" +
			"                    \"weight\": 8,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 1,\n" +
			"                                \"max\": 2\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:nickel_hydride_battery\",\n" +
			"                    \"weight\": 4,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 1,\n" +
			"                                \"max\": 2\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:alkaline_battery\",\n" +
			"                    \"weight\": 2,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 1,\n" +
			"                                \"max\": 2\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                },\n" +
			"                {\n" +
			"                    \"type\": \"item\",\n" +
			"                    \"name\": \"electricadvantage:lithium_battery\",\n" +
			"                    \"weight\": 1,\n" +
			"                    \"functions\": [\n" +
			"                        {\n" +
			"                            \"function\": \"set_count\",\n" +
			"                            \"count\": {\n" +
			"                                \"min\": 1,\n" +
			"                                \"max\": 2\n" +
			"                            }\n" +
			"                        }\n" +
			"                    ]\n" +
			"                }\n" +
			"            ]\n" +
			"        }\n" +
			"    ]\n" +
			"}\n";
}