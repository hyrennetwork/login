package com.redefantasy.login.misc.services

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.applications.ApplicationType
import com.redefantasy.core.shared.echo.packets.ConnectUserToApplicationPacket
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.spigot.misc.utils.Title
import com.redefantasy.login.listeners.GeneralListeners
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Sound

/**
 * @author Gutyerrez
 */
object LoginService {

    fun authenticate(user: User) {
        val player = Bukkit.getPlayer(user.getUniqueId())

        val bukkitTask = GeneralListeners.LOGIN_IN[player.uniqueId]

        if (bukkitTask !== null) Bukkit.getScheduler().cancelTask(bukkitTask.taskId)

        user.setLogged(true)

        val title = Title(
            "§a§lAutenticado!",
            "§fRedirecionando...",
            0,
            0,
            60
        )

        player.playSound(
            player.location,
            Sound.LEVEL_UP,
            1.0F,
            1.0F
        )
        title.sendToPlayer(player)

        val lobby = this.fetchLobbyApplication()

        if (lobby === null) {
            player.kick(TextComponent("§cNão foi possível encontrar um saguão livre."))
            return
        }

        val packet = ConnectUserToApplicationPacket(
            user.id,
            lobby
        )

        Thread {
            Thread.sleep(1000)

            CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)
        }.start()
    }

    private fun fetchLobbyApplication() = CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByApplicationType(ApplicationType.LOBBY)
            .stream()
            .sorted { application1, application2 ->
                println("${application1.name} -> ${application2.name}")

                val usersByApplication1 = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsersByApplication(application1)
                val usersByApplication2 = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsersByApplication(application2)

                println("${application1.name}:${usersByApplication1.size} || ${application2.name}:${usersByApplication2.size}")

                usersByApplication2.size ushr usersByApplication1.size
            }
            .findFirst()
            .orElse(null)

}