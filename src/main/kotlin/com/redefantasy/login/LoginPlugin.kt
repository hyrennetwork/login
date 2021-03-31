package com.redefantasy.login

import com.redefantasy.core.spigot.command.registry.CommandRegistry
import com.redefantasy.core.spigot.misc.frame.data.Frame
import com.redefantasy.core.spigot.misc.plugin.CustomPlugin
import com.redefantasy.login.commands.LoginCommand
import com.redefantasy.login.commands.RegisterCommand
import com.redefantasy.login.listeners.GenericListeners
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import java.net.URL

/**
 * @author Gutyerrez
 */
class LoginPlugin : CustomPlugin() {

    companion object {

        lateinit var instance: LoginPlugin

    }

    init {
        instance = this
    }

    override fun onEnable() {
        super.onEnable()

        val pluginManager = Bukkit.getServer().pluginManager

        pluginManager.registerEvents(GenericListeners(), this)

        CommandRegistry.registerCommand(LoginCommand())
        CommandRegistry.registerCommand(RegisterCommand())

        /**
         * World settings
         */
        Bukkit.getServer().worlds.forEach {
            it.setStorm(false)

            it.isThundering = false
            it.weatherDuration = 0

            it.ambientSpawnLimit = 0
            it.animalSpawnLimit = 0
            it.monsterSpawnLimit = 0

            it.setTicksPerAnimalSpawns(99999)
            it.setTicksPerMonsterSpawns(99999)

            it.setGameRuleValue("randomTickSpeed", "-999")
            it.setGameRuleValue("mobGriefing", "false")
            it.setGameRuleValue("doMobSpawning", "false")
            it.setGameRuleValue("doMobLoot", "false")
            it.setGameRuleValue("doFireTick", "false")
            it.setGameRuleValue("doDaylightCycle", "false")

            it.time = 1200
        }

        /**
         * Frames
         */

        /**
         * Frames
         */
        val frame = Frame(URL("https://i.imgur.com/YzXizib.png"))

        frame.place(
            Location(
                Bukkit.getWorld("world"),
                -4.0,
                89.0,
                -39.0
            ),
            BlockFace.SOUTH
        )

        frame.place(
            Location(
                Bukkit.getWorld("world"),
                4.0,
                89.0,
                -39.0
            ),
            BlockFace.NORTH
        )

    }

}