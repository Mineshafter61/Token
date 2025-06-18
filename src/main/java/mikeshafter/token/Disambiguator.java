package mikeshafter.token;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Disambiguator {
public static Component mm(String text) { return MiniMessage.miniMessage().deserialize(text); }
}
