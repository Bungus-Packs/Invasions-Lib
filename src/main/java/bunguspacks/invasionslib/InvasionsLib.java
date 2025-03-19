package bunguspacks.invasionslib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvasionsLib implements ModInitializer {
    public static final String MOD_ID="invasionslib";
    public static final Logger LOGGER= LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing "+MOD_ID);
    }
}
