package com.azuyamat.maestro.velocity.commands

import com.azuyamat.maestro.common.data.command.CommandData
import com.azuyamat.maestro.velocity.CommandExecutor
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import java.util.Optional

class VelocityCommand(
    private val proxyServer: ProxyServer,
    private val data: CommandData
): SimpleCommand {
    private var executor = CommandExecutor(proxyServer, data)

    override fun execute(invocation: SimpleCommand.Invocation) {
        executor.execute(invocation)
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return data.permission.isNotEmpty() && invocation.source().hasPermission(data.permission)
    }

    fun buildMeta(): CommandMeta = proxyServer.commandManager.metaBuilder(data.name)
        .aliases(*data.aliases)
        .plugin(proxyServer)
        .build()
}