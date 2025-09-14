package mikeshafter.token

import net.kyori.adventure.text.Component
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.entity.Player
import org.bukkit.block.Block
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent

class SignalTool extends Listener {
class ChatBlock(val block: Block, var _page: Int) {
	private var _m: String = ""
	def page: Int = _page
	def page_= (page: Int): Unit = {_page = page}
	def m: String = _m
	def m_= (message: String): Unit = {_m = message}
}
val plugin: Token = JavaPlugin.getPlugin(classOf[Token])
private val interruptChatMap: mutable.HashMap[Player, ChatBlock] = mutable.HashMap[Player, ChatBlock]()

private def mm(text: String): Component = Disambiguator.mm(text)

@EventHandler def onBlockClick (event: PlayerInteractEvent): Unit = {
	val block: Block = event.getClickedBlock
	val player: Player = event.getPlayer
	val item: ItemStack = event.getItem

	// Check if item is a signal tool
	if (item == null || item.getItemMeta.displayName() != mm("<aqua>Signal Tool</aqua>")) return

	interruptChatMap.addOne(player, new ChatBlock(block, 1))

	player.sendMessage("Enter signal type (manual; token; block)")
}

@EventHandler def onChat (event: AsyncChatEvent): Unit = {
	val player: Player = event.getPlayer
	if (!interruptChatMap.contains(player)) {return}

	event.setCancelled(true)
	val block: Block = interruptChatMap(player).block
	val page: Int = interruptChatMap(player).page
	val message: String = event.message().asInstanceOf[TextComponent].content()

	if (page == 1) {
		interruptChatMap(player).page = 2
		interruptChatMap(player).m = message
		player.sendMessage("Enter signal number/section name")
	}
	else if (page == 2) {
		SignalSql().newSignal(message, interruptChatMap(player).m, block.getLocation)
		interruptChatMap.remove(player)
	}
}
}
