package com.redefantasy.login.misc.services

import com.redefantasy.core.shared.CoreConstants
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.echo.packets.ConnectUserToApplicationPacket
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.spigot.misc.player.sendPacket
import com.redefantasy.core.spigot.misc.utils.Title
import com.redefantasy.login.LoginConstants
import com.redefantasy.login.LoginPlugin
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.ChatComponentText
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * @author Gutyerrez
 */
object LoginService {

    val LOGIN_IN = mutableMapOf<UUID, BukkitTask>()
    private val TTL_SECONDS = 30L

    fun start(user: User?) {
        if (user == null) return

        this.start(user.getUniqueId())
    }

    fun start(uuid: UUID) {
        val player = Bukkit.getPlayer(uuid)

        val title = Title(
            "§6§lREDE FANTASY",
            "§fUtilize ${
                if (CoreProvider.Cache.Local.USERS.provide().fetchById(uuid) === null) {
                    "/registrar <senha> <senha>"
                } else "/logar <senha>"
            }",
            0,
            0,
            20 * TTL_SECONDS.toInt()
        )

        LOGIN_IN[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
            LoginPlugin.instance,
            {
                player.kick(
                    ComponentBuilder()
                        .append("§c§lREDE FANTASY")
                        .append("\n\n")
                        .append("§cVocê excedeu o tempo limite para efetuar o login, tente novamente.")
                        .create()
                )
            },
            20 * TTL_SECONDS
        )

        title.sendToPlayer(player)
    }

    fun authenticate(user: User) {
        val player = Bukkit.getPlayer(user.getUniqueId())

        val bukkitTask = LOGIN_IN[player.uniqueId]

        if (bukkitTask !== null) Bukkit.getScheduler().cancelTask(bukkitTask.taskId)

        val bukkitApplication = CoreConstants.fetchLobbyApplication()

        if (bukkitApplication === null) {
            player.kick(TextComponent("§cNão foi possível encontrar um saguão livre."))
            return
        }

        user.setLogged(true)

        val title = Title(
            "§a§lAutenticado!",
            "§fRedirecionando...",
            0,
            0,
            60
        )

        val packet = PacketPlayOutChat(
            ChatComponentText(
                String(LoginConstants.EMPTY_LINES)
            )
        )

        player.sendPacket(packet)

        player.sendMessage(
            ComponentBuilder()
                .append("\n")
                .append("§e§l AVISO: §r§eNão utilize sua senha em outros servidores.")
                .append("\n")
                .create()
        )

        player.playSound(
            player.location,
            Sound.LEVEL_UP,
            1.0F,
            1.0F
        )
        title.sendToPlayer(player)

        Thread {
            Thread.sleep(1000)

            val packet = ConnectUserToApplicationPacket(
                user.id,
                bukkitApplication
            )

            CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)
        }.start()
    }

    fun hasStarted(user: User?) = LOGIN_IN.containsKey(user?.getUniqueId())

}