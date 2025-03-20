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

//this config is for which mob groups invasions spawn
public class InvasionMobConfig {
    private static final File CONFIG = new File("config/invasionslib/invasion_mob_config.json");

    //record for storing data about the mobs an invasion can spawn; builds the 'deck'
    public record InvasionMobData(String name, float chance, List<InvasionMobGroupData> passiveMobs,
                                  List<InvasionMobGroupData> waveMobs) {
    }

    //record for storing data about a mob group in the context of a specific invasion director
    public record InvasionMobGroupData(MobGroupConfig.MobGroupData data, float chance, int cost) {
    }

    public static final HashMap<String, InvasionMobData> invasionMobs = new HashMap<>();


    //called on init
    public static void loadConfig() {
        //make default config if none exists
        if (!CONFIG.exists()) {
            CONFIG.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG)) {
                JsonArray invasionData = new JsonArray();

                JsonObject basicInvasion = new JsonObject();
                basicInvasion.addProperty("name", "basicinvasion");
                basicInvasion.addProperty("weight", 1);
                JsonArray basicInvasionMobGroups = new JsonArray();

                JsonObject basicInvasionBasicGroup = new JsonObject();
                basicInvasionBasicGroup.addProperty("name", "basicGroup");
                //optional weight and cost override
                //basicInvasionZombieGroup.addProperty("weight",100);
                //basicInvasionZombieGroup.addProperty("cost",10);
                //optional conditional spawning, defaults to true on both
                //basicInvasionZombieGroup.addProperty("doPassiveSpawning",false);
                //basicInvasionZombieGroup.addProperty("doWaveSpawning",false);
                basicInvasionMobGroups.add(basicInvasionBasicGroup);

                JsonObject basicInvasionZombieGroup = new JsonObject();
                basicInvasionZombieGroup.addProperty("name", "zombieGroup");
                basicInvasionMobGroups.add(basicInvasionZombieGroup);

                JsonObject basicInvasionSkeletonGroup = new JsonObject();
                basicInvasionSkeletonGroup.addProperty("name", "skeletonGroup");
                basicInvasionMobGroups.add(basicInvasionSkeletonGroup);

                JsonObject basicInvasionRavagerGroup = new JsonObject();
                basicInvasionRavagerGroup.addProperty("name", "ravagerGroup");
                basicInvasionRavagerGroup.addProperty("doPassiveSpawning", false);
                basicInvasionMobGroups.add(basicInvasionRavagerGroup);

                basicInvasion.add("mobGroups", basicInvasionMobGroups);
                invasionData.add(basicInvasion);

                JsonObject defaultConfig = new JsonObject();
                defaultConfig.add("invasions", invasionData);

                new GsonBuilder().setPrettyPrinting().create().toJson(defaultConfig, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileReader reader = new FileReader(CONFIG)) {
            JsonObject config = new Gson().fromJson(reader, JsonObject.class);
            JsonArray invasions = config.getAsJsonArray("invasions");
            int totalWeights = 0;
            for (int i = 0; i < invasions.size(); i++) {
                JsonObject invasion = invasions.get(i).getAsJsonObject();
                totalWeights += invasion.get("weight").getAsInt();
            }
            for (int i = 0; i < invasions.size(); i++) {
                JsonObject invasion = invasions.get(i).getAsJsonObject();
                String name = invasion.get("name").getAsString();
                float chance = ((float) invasion.get("weight").getAsInt()) / totalWeights;
                List<InvasionMobGroupData> passive = new ArrayList<>();
                List<InvasionMobGroupData> wave = new ArrayList<>();
                int passiveWeightTotal = 0;
                int waveWeightTotal = 0;
                JsonArray invasionGroups = invasion.getAsJsonArray("mobGroups");
                //grab weight totals for both passive and wave spawns
                for (int j = 0; j < invasionGroups.size(); j++) {
                    JsonObject mobGroup = invasionGroups.get(j).getAsJsonObject();
                    MobGroupConfig.MobGroupData data = MobGroupConfig.mobGroups.get(mobGroup.get("name").getAsString());
                    if (!mobGroup.has("doPassiveSpawning") || mobGroup.get("doPassiveSpawning").getAsBoolean()) {
                        passiveWeightTotal += data.weight();
                    }
                    if (!mobGroup.has("doWaveSpawning") || mobGroup.get("doWaveSpawning").getAsBoolean()) {
                        waveWeightTotal += data.weight();
                    }
                }
                for (int j = 0; j < invasionGroups.size(); j++) {
                    JsonObject mobGroup = invasionGroups.get(j).getAsJsonObject();
                    MobGroupConfig.MobGroupData data = MobGroupConfig.mobGroups.get(mobGroup.get("name").getAsString());
                    int cost = mobGroup.has("cost") ? mobGroup.get("cost").getAsInt() : data.cost();
                    if (!mobGroup.has("doPassiveSpawning") || mobGroup.get("doPassiveSpawning").getAsBoolean()) {
                        float passiveChance = ((float) (mobGroup.has("weight") ? mobGroup.get("weight").getAsInt() : data.weight())) / passiveWeightTotal;
                        passive.add(new InvasionMobGroupData(data, passiveChance, cost));
                    }
                    if (!mobGroup.has("doWaveSpawning") || mobGroup.get("doWaveSpawning").getAsBoolean()) {
                        float waveChance = ((float) (mobGroup.has("weight") ? mobGroup.get("weight").getAsInt() : data.weight())) / waveWeightTotal;
                        wave.add(new InvasionMobGroupData(data, waveChance, cost));
                    }
                }
                invasionMobs.put(name, new InvasionMobData(name, chance, passive, wave));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
