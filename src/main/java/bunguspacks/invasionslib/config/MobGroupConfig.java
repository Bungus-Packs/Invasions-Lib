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
    public record MobUnitData(String mobId, int minCount, int maxCount, int creditWeight) {
    }

    //store mob group data in hashmap keyed by name
    public static final HashMap<String, MobGroupData> mobGroups = new HashMap<>();

    /*
    CONFIG JSON FORMAT:
    {
        //List of mob groups
        "mobGroups": [
            {
                "name": "basicGroup", //Name of the mob group. Must be referenced in invasion_mob_config.json to assign to an invasion
                "weight": 100, //Default spawn weighting of the group; can be overridden
                "cost": 10, //Default cost of the group; can be overridden
                "units": [ //List of the individual types and counts of mobs in the group
                    {
                        "mobid": "minecraft:zombie", //Mob ID of the mob being described
                        "count": 2, //OPTIONAL; one of either 'count' or both 'minCount' and 'maxCount' must be present. Overrides the latter if both are present.
                        "minCount": 1, //OPTIONAL; see above; the minimum count of a mob type to spawn in the group
                        "maxCount": 3 //OPTIONAL; see above; the maximum count of a mob type to spawn in the group
                    },
                    {
                        (another unit)
                    },...
                ]
            },
            {
                (a different mob group)
            },...
        ]
    }
    */

    //called on init
    public static void loadConfig() {


        //make default config if none exists
        if (!CONFIG.exists()) {
            CONFIG.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG)) {

                JsonArray mobGroupData = new JsonArray();


                JsonObject basicMobGroup = new JsonObject();
                basicMobGroup.addProperty("name", "basicGroup");
                basicMobGroup.addProperty("weight", 100);
                basicMobGroup.addProperty("cost", 10);
                JsonArray basicMobUnitData = new JsonArray();

                JsonObject basicZombieUnit = new JsonObject();
                basicZombieUnit.addProperty("mobid", "minecraft:zombie");
                basicZombieUnit.addProperty("minCount", 1);
                basicZombieUnit.addProperty("maxCount", 2);
                basicZombieUnit.addProperty("creditWeight", 1);
                basicMobUnitData.add(basicZombieUnit);

                JsonObject basicSkeletonUnit = new JsonObject();
                basicSkeletonUnit.addProperty("mobid", "minecraft:skeleton");
                basicSkeletonUnit.addProperty("minCount", 0);
                basicSkeletonUnit.addProperty("maxCount", 1);
                basicSkeletonUnit.addProperty("creditWeight", 2);
                basicMobUnitData.add(basicSkeletonUnit);

                basicMobGroup.add("units", basicMobUnitData);
                mobGroupData.add(basicMobGroup);


                JsonObject zombieMobGroup = new JsonObject();
                zombieMobGroup.addProperty("name", "zombieGroup");
                zombieMobGroup.addProperty("weight", 50);
                zombieMobGroup.addProperty("cost", 25);
                JsonArray zombieMobUnitData = new JsonArray();

                JsonObject zombieGroupUnit = new JsonObject();
                zombieGroupUnit.addProperty("mobid", "minecraft:zombie");
                zombieGroupUnit.addProperty("minCount", 4);
                zombieGroupUnit.addProperty("maxCount", 5);
                zombieMobUnitData.add(zombieGroupUnit);

                zombieMobGroup.add("units", zombieMobUnitData);
                mobGroupData.add(zombieMobGroup);


                JsonObject skeletonMobGroup = new JsonObject();
                skeletonMobGroup.addProperty("name", "skeletonGroup");
                skeletonMobGroup.addProperty("weight", 10);
                skeletonMobGroup.addProperty("cost", 25);
                JsonArray skeletonMobUnitData = new JsonArray();

                JsonObject skeletonGroupUnit = new JsonObject();
                skeletonGroupUnit.addProperty("mobid", "minecraft:skeleton");
                skeletonGroupUnit.addProperty("minCount", 4);
                skeletonGroupUnit.addProperty("maxCount", 5);
                skeletonMobUnitData.add(skeletonGroupUnit);

                skeletonMobGroup.add("units", skeletonMobUnitData);
                mobGroupData.add(skeletonMobGroup);


                JsonObject ravagerMobGroup = new JsonObject();
                ravagerMobGroup.addProperty("name", "ravagerGroup");
                ravagerMobGroup.addProperty("weight", 100);
                ravagerMobGroup.addProperty("cost", 100);
                JsonArray ravagerMobUnitData = new JsonArray();

                JsonObject ravagerGroupUnit = new JsonObject();
                ravagerGroupUnit.addProperty("mobid", "minecraft:ravager");
                ravagerGroupUnit.addProperty("count", 1);
                ravagerMobUnitData.add(ravagerGroupUnit);

                ravagerMobGroup.add("units", ravagerMobUnitData);
                mobGroupData.add(ravagerMobGroup);

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
                    String mobId = unit.get("mobid").getAsString();
                    int minCount = unit.get(unit.has("count") ? "count" : "minCount").getAsInt();
                    int maxCount = unit.get(unit.has("count") ? "count" : "maxCount").getAsInt();
                    int creditWeight = unit.has("creditWeight") ? unit.get("creditWeight").getAsInt() : 1;
                    units.add(new MobUnitData(mobId, minCount, maxCount, creditWeight));
                }
                mobGroups.put(name, new MobGroupData(name, weight, cost, units));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
