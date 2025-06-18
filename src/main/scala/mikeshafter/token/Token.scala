package mikeshafter.token

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.Plugin

class Token extends JavaPlugin {

override def onEnable(): Unit = {
	getLogger.info("Token is starting up...")
	getServer.getPluginManager.registerEvents(new TokenPost(), this)
	getServer.getPluginManager.registerEvents(new SignalTool(), this)

	val tokenCommands: TokenCommands = new TokenCommands
	
	val manager: LifecycleEventManager[Plugin]  = this.getLifecycleManager;
	manager.registerEventHandler(LifecycleEvents.COMMANDS, commands => {
		commands.registrar.register(tokenCommands.commands)
	})
}

override def onDisable(): Unit = {
	getLogger.info("Token is shutting down...")
}
}
