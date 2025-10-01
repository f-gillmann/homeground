// Copyright 2025 Florian Gillmann
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
