package mikeshafter.token;

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.block.{Block, Sign}
import org.bukkit.block.sign.Side
import org.bukkit.entity.Player
import org.bukkit.event.block.{Action, SignChangeEvent}
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.ItemStack

import java.util.Collections;

class TokenPost extends Listener {
	import Direction.*;

	val tokenSectionMap: Map[String, TokenSection] = Map[String, TokenSection]();
	private val miniMessage: MiniMessage = MiniMessage.miniMessage();
	private val plain: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText();

	private def mm(text: String): Component = Disambiguator.mm(text);

	@EventHandler def onSignCreate(event: SignChangeEvent): Unit = {
		val line0 = plain.serialize(event.line(0));
		if (line0.strip().equalsIgnoreCase("[Token]")) {
			val message = mm("<blue>You have created a Token sign!</blue>");
			event.getPlayer.sendMessage(message);
		}
	}

	@EventHandler def onSignClick(event: PlayerInteractEvent): Unit = {
		val block: Block = event.getClickedBlock;
		if (block == null || event.getAction != Action.RIGHT_CLICK_BLOCK || !block.getState.isInstanceOf[Sign]) return;
		val player: Player = event.getPlayer;
		val item: ItemStack = event.getItem;
		val sign: Sign = block.getState.asInstanceOf[Sign];
		val line0: String = plain.serialize(sign.getSide(Side.FRONT).line(0));
		if (!line0.strip().equalsIgnoreCase("[Token]")) return;

		val sectionId: String = plain.serialize(sign.getSide(Side.FRONT).line(1)).strip();
		val direction: Value = Direction.withName(plain.serialize(sign.getSide(Side.FRONT).line(2)).strip().toUpperCase());
		val maxTrains: Int = Integer.parseInt(plain.serialize(sign.getSide(Side.FRONT).line(3)).strip());

		var section: TokenSection = tokenSectionMap.getOrElse(sectionId, new TokenSection(maxTrains));
		if (section == null || section.maxTrains != maxTrains) {
			section = new TokenSection(maxTrains);
		}

		val statusMsg = mm( s"<blue>Direction: <white>${section.currentDir}</white> Max Trains: <white>${section.maxTrains}</white> Current Trains: <white>${section.currentTrains}</white></blue>");
		player.sendMessage(statusMsg);

		if (item.getItemMeta() != null && item.getItemMeta.lore != null && plain.serialize(item.getItemMeta.lore.get(0)).equals(sectionId) ) {
			// player has token
		}

		if (item == null) {
			if (!section.insertTrain(direction)) {
				player.sendMessage(mm("<red>ERROR: Section full! Token could not be generated.</red>"));
				return;
			}

			val token = new ItemStack(Material.BLAZE_ROD);
			val meta = token.getItemMeta;
			if (meta == null) return;
			meta.displayName(mm("<aqua>Token</aqua>"));
			meta.lore(Collections.singletonList(Component.text(sectionId)));
			token.setItemMeta(meta);
			player.getInventory.addItem(token);

			val successMsg = mm( s"<green>Token generated for: <white>$sectionId</white>\nCurrent trains (incl. you): <white>${section.currentTrains}</white></green>");
			player.sendMessage(successMsg);
		}
		else if (item.getItemMeta != null && item.getItemMeta.lore != null && plain.serialize(item.getItemMeta.lore.get(0)).equals(sectionId)) {
			if (!section.removeTrainOpposite(direction)) {
				player.sendMessage(mm("<red>ERROR: Section empty/direction invalid! Token could not be received.</red>"));
				return;
			}
			item.setAmount(1);
			player.getInventory.remove(item);

			val receivedMsg = mm( s"<green>Token has been received for the section <white>$sectionId</white></green>");
			player.sendMessage(receivedMsg);
		}
	}
}

