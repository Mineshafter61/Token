package mikeshafter.token;

import net.kyori.adventure.text.Component
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.entity.Player
import org.bukkit.block.Block
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent

class SignalTool extends Listener {
val plugin: Token = JavaPlugin.getPlugin(classOf[Token]);
private val interruptChatMap: mutable.HashMap[Player, Block] = mutable.HashMap[Player, Block]();

private def mm(text: String): Component = Disambiguator.mm(text);

@EventHandler def onBlockClick (event: PlayerInteractEvent): Unit = {
	val block: Block = event.getClickedBlock;
	val player: Player = event.getPlayer;
	val item: ItemStack = event.getItem;

	// Check if item is a signal tool
	val meta = item.getItemMeta;
	if (meta.displayName() != mm("<aqua>Signal Tool</aqua>")) return;

	interruptChatMap.addOne(player, block);

	player.sendMessage("Input signal number in chat:");
}

@EventHandler def onChat (event: AsyncChatEvent): Unit = {
	val player: Player = event.getPlayer;
	if (!interruptChatMap.contains(player)) {return;}

	event.setCancelled(true);
	val message: String = event.message().asInstanceOf[TextComponent].content();
	val block: Block = interruptChatMap.get(player).get;
	
	SignalSql().newSignal(message, "", block.getLocation);
}
}
