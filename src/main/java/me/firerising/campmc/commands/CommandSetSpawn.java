package me.firerising.campmc.commands;

import com.songoda.core.commands.AbstractCommand;
import me.firerising.campmc.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSetSpawn extends AbstractCommand {

    private final Main plugin;

    public CommandSetSpawn(Main plugin) {
        super(true, "setspawn");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player)sender;
        plugin.getPluginSettings().setSpawnPoint(player.getLocation());
        plugin.getDataManager().createOrUpdatePluginSettings(plugin.getPluginSettings());
        plugin.getLocale().newMessage("&aSpawn point set!").sendPrefixedMessage(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.admin";
    }

    @Override
    public String getSyntax() {
        return "setspawn";
    }

    @Override
    public String getDescription() {
        return "Set the spawn point for varies use such as ejecting and banning.";
    }
}
