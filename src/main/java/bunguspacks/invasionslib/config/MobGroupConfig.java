package bunguspacks.invasionslib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//this config is for defining mob groups
public class MobGroupConfig {
    //config filepath
    private static final File CONFIG = new File("config/invasionslib/mob_group_config.json");

    //record for storing data about a mob group
    public record MobGroupData(String name, int weight, int cost, List<MobUnitData> mobs) {
    }

    //record for storing data about a mob unit (a subgroup consisting of a single type of mob)
    public record MobUnitData(String mobid, int minCount, int maxCount, int creditWeight) {
    }

    public static final HashMap<String, MobGroupData> mobGroups = new HashMap<>();
    //public static final List<MobGroupData> mobGroups = new ArrayList<>();

    //called on init
    public static void loadConfig() {
        //make default config if none exists
        if (!CONFIG.exists()) {
            CONFIG.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG)) {

                JsonArray mobGroupData = new JsonArray();

                JsonObject basicMobGroup = new JsonObject();
                basicMobGroup.addProperty("name", "zombieGroup");
                basicMobGroup.addProperty("weight", 100);
                basicMobGroup.addProperty("cost", 10);

                JsonArray basicMobUnitData = new JsonArray();

                JsonObject zombieUnit = new JsonObject();
                zombieUnit.addProperty("mobid", "minecraft:zombie");
                zombieUnit.addProperty("minCount", 2);
                zombieUnit.addProperty("maxCount", 4);
                zombieUnit.addProperty("creditWeight", 1);
                basicMobUnitData.add(zombieUnit);

                JsonObject skeletonUnit = new JsonObject();
                skeletonUnit.addProperty("mobid", "minecraft:skeleton");
                skeletonUnit.addProperty("minCount", 5);
                skeletonUnit.addProperty("maxCount", 9);
                skeletonUnit.addProperty("creditWeight", 2);
                basicMobUnitData.add(skeletonUnit);

                basicMobGroup.add("units", basicMobUnitData);

                mobGroupData.add(basicMobGroup);

                JsonObject defaultConfig = new JsonObject();
                defaultConfig.add("mobGroups", mobGroupData);

                new GsonBuilder().setPrettyPrinting().create().toJson(defaultConfig, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileReader reader = new FileReader(CONFIG)) {
            JsonObject config = new Gson().fromJson(reader, JsonObject.class);
            JsonArray groups = config.getAsJsonArray("mobGroups");
            mobGroups.clear();
            for (int i = 0; i < groups.size(); i++) {
                JsonObject group = groups.get(i).getAsJsonObject();
                String name = group.get("name").getAsString();
                int weight = group.get("weight").getAsInt();
                int cost = group.get("cost").getAsInt();
                JsonArray mobUnits = group.get("units").getAsJsonArray();
                List<MobUnitData> units = new ArrayList<>();
                for (int j = 0; j < mobUnits.size(); j++) {
                    JsonObject unit = mobUnits.get(j).getAsJsonObject();
                    String mobid = unit.get("mobid").getAsString();
                    int minCount = unit.get("minCount").getAsInt();
                    int maxCount = unit.get("maxCount").getAsInt();
                    int creditWeight = unit.get("creditWeight").getAsInt();
                    units.add(new MobUnitData(mobid, minCount, maxCount, creditWeight));
                }
                mobGroups.put(name, new MobGroupData(name, weight, cost, units));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
