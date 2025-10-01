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

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

class CommandManager {
    companion object {
        data class CommandData(
            val name: String,
            val description: String,
            val usage: String,
            val permission: String,
            val execute: (CommandContext<ServerCommandSource>) -> Int
        )

        open class ModCommand {
            var commandData: CommandData? = null

            open fun createCommand(
                name: String,
                description: String,
                usage: String,
                permission: String = "homeground.command.$name",
                execute: (CommandContext<ServerCommandSource>) -> Int
            ) {
                commandData = CommandData(name, description, usage, permission, execute)
            }
        }
    }
}
