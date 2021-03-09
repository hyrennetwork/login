package com.redefantasy.login.misc.services

import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.spigot.misc.utils.Title
import org.bukkit.Bukkit
import org.bukkit.Sound

/**
 * @author Gutyerrez
 */
object LoginService {

    fun authenticate(user: User) {
        val player = Bukkit.getPlayer(user.getUniqueId())


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
    }

}