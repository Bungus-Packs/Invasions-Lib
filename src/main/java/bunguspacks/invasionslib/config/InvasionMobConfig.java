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
import java.util.HashSet;
import java.util.Iterator;
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

    //store sets of invasion mob data in a hashmap keyed by name
    public static final HashMap<String, InvasionMobData> invasionMobs = new HashMap<>();

    /*
    CONFIG JSON FORMAT:
    {
        //List of invasion data
        "invasions": [
            {
                "name": "basicInvasion", //name of the invasion to be internally referenced
                "weight": 1, //weight of the invasion to be randomly selected from among available invasions
                "mobGroups": [ //List of the different mob groups spawned by a specific invasion
                    {
                        "name": "basicGroup", //name of the mob group to be spawned; Must be a name defined in the mob_group_config.json
                        "weight": 100, //OPTIONAL; override mob group defined weight for spawning
                        "cost": 10, //OPTIONAL; override mob group defined cost
                        "doPassiveSpawning": true, //OPTIONAL; whether the mob should be spawned by the passive director; defaults to true
                        "doWaveSpawning": true //OPTIONAL; whether the mob should be spawned by the wave director; defaults to true
                    },
                    {
                        (a different mob group)
                    },...
                ]
            },
            {
                (a different invasion)
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
                JsonArray invasionData = new JsonArray();

                JsonObject basicInvasion = new JsonObject();
                basicInvasion.addProperty("name", "basicInvasion");
                basicInvasion.addProperty("weight", 1);
                JsonArray basicInvasionMobGroups = new JsonArray();

                JsonObject basicInvasionBasicGroup = new JsonObject();
                basicInvasionBasicGroup.addProperty("name", "basicGroup");
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
            //get the sum of weights for invasions so it can be internally converted to probability
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
                //grab weight totals for both passive and wave spawns so they can be internally converted to chance
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
                //read both passive and wave spawn groups into records
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

    public static HashSet<String> getInvasionMobIds(InvasionMobData invasionMobData) {
        HashSet<String> invasionMobIds = new HashSet<>();
        for(InvasionMobGroupData passiveMobGroupData : invasionMobData.passiveMobs()) {
            for (MobGroupConfig.MobUnitData mobUnitData : passiveMobGroupData.data().mobs()) {
                invasionMobIds.add(mobUnitData.mobId());
            }
        }
        for(InvasionMobGroupData waveMobGroupData : invasionMobData.waveMobs()) {
            for (MobGroupConfig.MobUnitData mobUnitData : waveMobGroupData.data().mobs()) {
                invasionMobIds.add(mobUnitData.mobId());
            }
        }
        return invasionMobIds;
    }
}
