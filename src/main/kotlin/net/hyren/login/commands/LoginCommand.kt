package net.hyren.login.commands

import net.hyren.core.shared.CoreConstants
import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.commands.argument.Argument
import net.hyren.core.shared.commands.restriction.CommandRestriction
import net.hyren.core.shared.users.data.User
import net.hyren.core.shared.users.passwords.storage.dto.FetchUserPasswordByUserIdDTO
import net.hyren.core.spigot.command.CustomCommand
import net.hyren.login.misc.services.LoginService
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.abs

/**
 * @author Gutyerrez
 */
class LoginCommand : CustomCommand("logar") {

    override fun getDescription() = "Autenticar sua conta."

    override fun getCommandRestriction() = CommandRestriction.GAME

    override fun getAliases() = listOf("login")

    override fun getArguments() = listOf(
        Argument("senha")
    )

    override fun canBeExecuteWithoutLogin() = true

    override fun onCommand(
        commandSender: CommandSender,
        user: User?,
        args: Array<out String>
    ): Boolean {
        if (!LoginService.hasStarted(user)) return false

        commandSender as Player

        println(commandSender.uniqueId)
        println(
            if (user == null) {
                user
            } else {
                "${user.id} ${user.name}"
            }
        )

        if (user == null) {
            commandSender.sendMessage(TextComponent("§cVocê não está registrado."))
            return false
        }

        if (user.isLogged()) {
            return false
        }

        val currentPassword = CoreProvider.Repositories.MariaDB.USERS_PASSWORDS_REPOSITORY.provide().fetchByUserId(
            FetchUserPasswordByUserIdDTO(user.getUniqueId())
        ).stream()
            .filter { it.enabled }
            .findFirst()
            .orElse(null)

        if (currentPassword == null) {
            commandSender.sendMessage(TextComponent("§cVocê não está registrado."))
            return false
        }

        val successfully = user.attemptLogin(args[0])

        if (!successfully && user.loginAttempts.get() >= CoreConstants.MAX_LOGIN_ATTEMPTS) {
            commandSender.kick(
                ComponentBuilder(CoreConstants.Info.ERROR_SERVER_NAME)
                    .append("\n\n")
                    .append("§cVocê excedeu o número limite de ${CoreConstants.MAX_LOGIN_ATTEMPTS} tentativas de login, reconecte e tente novamente.")
                    .create()
            )

            user.loginAttempts.set(0)
            return false
        } else if (!successfully) {
            commandSender.sendMessage(TextComponent("§cSenha incorreta! Você tem mais ${abs(user.loginAttempts.get() - CoreConstants.MAX_LOGIN_ATTEMPTS)} ${if (abs(user.loginAttempts.get() - CoreConstants.MAX_LOGIN_ATTEMPTS) > 1) "tentativas" else "tentativa"}."))
            return false
        }

        LoginService.authenticate(user)
        return true
    }

}