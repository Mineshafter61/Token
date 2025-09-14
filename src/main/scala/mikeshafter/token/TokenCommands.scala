package mikeshafter.token;

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.{IntegerArgumentType, LongArgumentType, StringArgumentType}
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.{CommandSourceStack, Commands}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.BlockCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.{Location, Material}


class TokenCommands {
private val miniMessage: MiniMessage = MiniMessage.miniMessage();
private val plain: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText();
private def mm(text: String): Component = Disambiguator.mm(text);

private val clearTokenSectionCmd = Commands.literal("cleartokensection")
    .`then`(Commands.argument("section", StringArgumentType.word())
        .`then`(Commands.argument("max_trains", IntegerArgumentType.integer())
            .executes(ctx => {
                val tokenPost: TokenPost = new TokenPost();
                val section: String = ctx.getArgument("section", classOf[String]);
                val maxTrains: Int = ctx.getArgument("max_trains", classOf[Int]);
                val tokenSection = tokenPost.tokenSectionMap.updated(section, new TokenSection(maxTrains, section));
                Command.SINGLE_SUCCESS;
            })
        )
    );

private val getSignalTool = Commands.literal("getsignaltool")
    .executes(ctx => {
        val sender = ctx.getSource.getSender;
        sender match
            case player: Player =>
                val token = new ItemStack(Material.STICK);
                val meta = token.getItemMeta;
                if (meta == null) Command.SINGLE_SUCCESS;
                meta.displayName(mm("<aqua>Signal Tool</aqua>"));
                meta.setEnchantmentGlintOverride(true);
                token.setItemMeta(meta);
                player.getInventory.addItem(token);
                Command.SINGLE_SUCCESS;
            case _ => Command.SINGLE_SUCCESS;
    });

private val newSignalCmd = Commands.literal("newsignal")
    .`then`(Commands.argument("x", LongArgumentType.longArg())
        .`then`(Commands.argument("y", LongArgumentType.longArg())
            .`then`(Commands.argument("z", LongArgumentType.longArg())
				.`then`(Commands.argument("name", StringArgumentType.string())
                .executes(ctx => {
                    val x = ctx.getArgument("x", classOf[Int]);
                    val y = ctx.getArgument("y", classOf[Int]);
                    val z = ctx.getArgument("z", classOf[Int]);
                    val w = ctx.getSource.getSender match
                        case player: Player => player.getWorld
                        case block: BlockCommandSender => block.getBlock.getWorld
                    val loc: Location = new Location(w,x,y,z)
                    val name: String = ctx.getArgument("name", classOf[String]);
                    SignalSql().newSignal(name, "man", loc);
                    Command.SINGLE_SUCCESS;
                }))
            )
        )
    );

private val removeSignalCmd = Commands.literal("removesignal")
	.`then`(Commands.argument("name", StringArgumentType.string())
		.executes(ctx => {
			val name: String = ctx.getArgument("name", classOf[String]);
			SignalSql().removeSignal(name);
			Command.SINGLE_SUCCESS;
		})
	);

private val setSignalCmd = Commands.literal("setsignal")
	.`then`(Commands.argument("name", StringArgumentType.string())
		.`then`(Commands.literal("proceed").executes(ctx => {
			SignalSql().setSignal(ctx.getArgument("name", classOf[String]), Aspect.PROCEED)
			Command.SINGLE_SUCCESS
		}))
		.`then`(Commands.literal("caution").executes(ctx => {
			SignalSql().setSignal(ctx.getArgument("name", classOf[String]), Aspect.CAUTION)
			Command.SINGLE_SUCCESS
		}))
		.`then`(Commands.literal("stop").executes(ctx => {
			SignalSql().setSignal(ctx.getArgument("name", classOf[String]), Aspect.STOP)
			Command.SINGLE_SUCCESS
		}))
	);

val commands: LiteralCommandNode[CommandSourceStack] = Commands.literal("token")
    .`then`(clearTokenSectionCmd)
    .`then`(getSignalTool)
    .`then`(removeSignalCmd)
    .`then`(setSignalCmd)
    .build();
};
