package me.firerising.campmc.commands;

import com.songoda.core.commands.AbstractCommand;
import me.firerising.campmc.tasks.VisualizeTask;
import me.firerising.campmc.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandShow extends AbstractCommand {

    private final Main plugin;

    public CommandShow(Main plugin) {
        super(true, "show");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command must be called as a player");
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;

        if (args.length != 0)
            return ReturnType.SYNTAX_ERROR;

        if(VisualizeTask.togglePlayer(player))
            plugin.getLocale().getMessage("command.show.start").sendPrefixedMessage(player);
        else
            plugin.getLocale().getMessage("command.show.stop").sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "campMC.show";
    }

    @Override
    public String getSyntax() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Visualize claims around you";
    }
}
