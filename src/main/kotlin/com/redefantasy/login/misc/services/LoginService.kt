package com.redefantasy.login.misc.services

import com.redefantasy.core.shared.CoreConstants
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.echo.packets.ConnectUserToApplicationPacket
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.spigot.misc.utils.Title
import com.redefantasy.login.listeners.GenericListeners
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Sound

/**
 * @author Gutyerrez
 */
object LoginService {

    fun authenticate(user: User) {
        val player = Bukkit.getPlayer(user.getUniqueId())

        val bukkitTask = GenericListeners.LOGIN_IN[player.uniqueId]

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

        val bukkitApplication = CoreConstants.fetchLobbyApplication()

        if (bukkitApplication === null) {
            player.kick(TextComponent("§cNão foi possível encontrar um saguão livre."))
            return
        }

        val packet = ConnectUserToApplicationPacket(
            user.id,
            bukkitApplication
        )

        Thread {
            Thread.sleep(1000)

            CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)
        }.start()
    }

}