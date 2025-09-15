package sh.flg.homeground

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sh.flg.homeground.utils.TeleportTimerManager

class Homeground : ModInitializer {
    companion object {
        val modMetadata: ModMetadata = FabricLoader.getInstance().getModContainer("homeground").get().metadata
        val logger: Logger = LogManager.getLogger(modMetadata.id)
    }

    override fun onInitialize() {
        CommandRegistry.initialize()
        TeleportTimerManager.initialize()

        logger.info("Homeground has been initialized successfully!")
    }
}
