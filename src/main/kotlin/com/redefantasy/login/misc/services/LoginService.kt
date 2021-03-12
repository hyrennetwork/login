package com.redefantasy.login.misc.services

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.applications.ApplicationType
import com.redefantasy.core.shared.applications.status.ApplicationStatus
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
                val applicationStatus1 =
                    CoreProvider.Cache.Redis.APPLICATIONS_STATUS.provide().fetchApplicationStatusByApplication(
                        application1,
                        ApplicationStatus::class
                    )
                val applicationStatus2 =
                    CoreProvider.Cache.Redis.APPLICATIONS_STATUS.provide().fetchApplicationStatusByApplication(
                        application2,
                        ApplicationStatus::class
                    )

                if (applicationStatus1 === null || applicationStatus2 === null) return@sorted 0

                if (applicationStatus1.onlinePlayers < application1.slots ?: 0 && applicationStatus2.onlinePlayers < application2.slots ?: 0)
                    return@sorted applicationStatus2.onlinePlayers.compareTo(applicationStatus1.onlinePlayers)

                return@sorted 0
            }
            .findFirst()
            .orElse(null)

}