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

package sh.flg.homeground.utils

import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import java.util.UUID

data class TeleportRequest(
    val playerUUID: UUID,
    val teleportName: String,
    val initialPos: Vec3d,
    val initialWorld: ServerWorld,
    val targetPos: Vec3d,
    val targetWorld: ServerWorld,
    var ticksLeft: Int = 65 // 3 seconds * 20 ticks/second + 5 ticks
)

object TeleportTimerManager {
    private val activeTeleports = mutableMapOf<UUID, TeleportRequest>()

    fun initialize() {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            val iterator = activeTeleports.iterator()

            while (iterator.hasNext()) {
                val request = iterator.next().value
                val player = server.playerManager.getPlayer(request.playerUUID)

                if (player == null || player.isRemoved) {
                    iterator.remove()
                    continue
                }

                if (Permissions.check(player, ModPermissions.IGNORE_TELEPORT_TIMER)) {
                    teleportPlayer(player, request)
                    iterator.remove()
                    continue
                }

                if (hasPlayerMoved(player, request)) {
                    player.sendMessage(Text.literal("Teleportation cancelled! You moved."), true)
                    iterator.remove()
                    continue
                }

                if (request.ticksLeft > 0) {
                    request.ticksLeft--
                    if (request.ticksLeft % 20 == 0 && request.ticksLeft > 0) {
                        val seconds = request.ticksLeft / 20
                        player.sendMessage(Text.literal("Teleporting in $seconds... Don't move!"), true)
                    }
                    continue
                }

                teleportPlayer(player, request)

                iterator.remove()
            }
        }
    }

    fun start(player: ServerPlayerEntity, targetPos: Vec3d, targetWorld: ServerWorld, teleportName: String) {
        if (activeTeleports.containsKey(player.uuid)) {
            player.sendMessage(Text.literal("You are already teleporting!"), false)
            return
        }

        player.sendMessage(Text.literal("Teleporting to $teleportName..."), false)

        val request = TeleportRequest(player.uuid, teleportName, player.pos, player.world, targetPos, targetWorld)
        activeTeleports[player.uuid] = request
    }

    private fun teleportPlayer(player: ServerPlayerEntity, request: TeleportRequest) {
        player.teleport(
            request.targetWorld,
            request.targetPos.x,
            request.targetPos.y,
            request.targetPos.z,
            emptySet<PositionFlag>(),
            player.yaw,
            player.pitch,
            true
        )

        player.sendMessage(Text.literal("Teleported to ${request.teleportName}."), true)
    }

    private fun hasPlayerMoved(player: ServerPlayerEntity, request: TeleportRequest): Boolean {
        if (player.world != request.initialWorld) return true
        return player.pos.distanceTo(request.initialPos) > 0.1
    }
}
