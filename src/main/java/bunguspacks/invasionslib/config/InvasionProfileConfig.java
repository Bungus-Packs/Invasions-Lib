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

public class InvasionProfileConfig {

    //config filepath
    private static final File CONFIG = new File("config/invasionslib/profile_config.json");

    //record for storing data about an invasion profile
    public record DirectorProfileData(String name, float chance, float baselineDensity, float waveFraction,
                                      List<DirectorWaveData> waves) {
    }

    //record for storing data about an invasion wave
    public record DirectorWaveData(float progressPoint, float sizeFraction) {
    }

    public static HashMap<String,DirectorProfileData> profiles=new HashMap<>();


    //called on init
    public static void loadConfig() {
        //make default config if none exists
        if (!CONFIG.exists()) {
            try (FileWriter writer = new FileWriter(CONFIG)) {
                JsonArray profileData = new JsonArray();

                JsonObject hellspawnProfile = new JsonObject();
                hellspawnProfile.addProperty("name", "hellspawn");
                hellspawnProfile.addProperty("weight", 1);
                hellspawnProfile.addProperty("baselineDensity", 1.0f);
                hellspawnProfile.addProperty("waveFraction", 1.0f);
                JsonArray hellspawnWaves = new JsonArray();
                JsonObject hellspawnWave1 = new JsonObject();
                hellspawnWave1.addProperty("progressPoint", 0.0f);
                hellspawnWave1.addProperty("size", 1);
                hellspawnWaves.add(hellspawnWave1);

                hellspawnProfile.add("waves", hellspawnWaves);
                profileData.add(hellspawnProfile);

                JsonObject classicProfile = new JsonObject();
                classicProfile.addProperty("name", "classic");
                classicProfile.addProperty("weight", 2);
                classicProfile.addProperty("baselineDensity", 1.0f);
                classicProfile.addProperty("waveFraction", 0.4f);
                JsonArray classicWaves = new JsonArray();
                JsonObject classicWave1 = new JsonObject();
                classicWave1.addProperty("progressPoint", 0.4f);
                classicWave1.addProperty("size", 2);
                classicWaves.add(classicWave1);
                JsonObject classicWave2 = new JsonObject();
                classicWave2.addProperty("progressPoint", 0.7f);
                classicWave2.addProperty("size", 3);
                classicWaves.add(classicWave2);
                JsonObject classicWave3 = new JsonObject();
                classicWave3.addProperty("progressPoint", 1.0f);
                classicWave3.addProperty("size", 4);
                classicWaves.add(classicWave3);

                classicProfile.add("waves", classicWaves);
                profileData.add(classicProfile);

                JsonObject defaultConfig = new JsonObject();
                defaultConfig.add("profiles", profileData);

                new GsonBuilder().setPrettyPrinting().create().toJson(defaultConfig, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileReader reader = new FileReader(CONFIG)) {
            JsonObject config = new Gson().fromJson(reader, JsonObject.class);
            JsonArray configProfiles = config.getAsJsonArray("profiles");
            profiles.clear();

            //profile and wave weight/size stored internally as fractions
            int profileWeightSum = 0;

            //iterate over profiles to determine total profile weighting
            for (int i = 0; i < configProfiles.size(); i++) {
                JsonObject profile = configProfiles.get(i).getAsJsonObject();
                profileWeightSum += profile.get("weight").getAsInt();
            }

            for (int i = 0; i < configProfiles.size(); i++) {
                JsonObject profile = configProfiles.get(i).getAsJsonObject();
                String name = profile.get("name").getAsString();
                float chance = ((float) (profile.get("weight").getAsInt())) / profileWeightSum;
                float baselineDensity = profile.get("baselineDensity").getAsFloat();
                float waveFraction = profile.get("waveFraction").getAsFloat();
                JsonArray waves = profile.getAsJsonArray("waves");
                List<DirectorWaveData> waveData = new ArrayList<>();

                //iterate over waves to determine total size
                int waveSizeSum = 0;
                for (int j = 0; j < waves.size(); j++) {
                    JsonObject wave = waves.get(j).getAsJsonObject();
                    waveSizeSum += wave.get("size").getAsInt();
                }

                for (int j = 0; j < waves.size(); j++) {
                    JsonObject wave = waves.get(j).getAsJsonObject();
                    float progressPoint = wave.get("progressPoint").getAsFloat();
                    float sizeFraction = ((float) wave.get("size").getAsInt()) / waveSizeSum;
                    waveData.add(new DirectorWaveData(progressPoint, sizeFraction));
                }
                profiles.put(name,new DirectorProfileData(name, chance, baselineDensity, waveFraction, waveData));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
