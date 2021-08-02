package me.firerising.campmc.commands.admin;

import com.songoda.core.commands.AbstractCommand;
import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.Main;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandName extends AbstractCommand {

    private final Main plugin;

    public CommandName(Main plugin) {
        super(true, "admin name");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

        Player player = (Player) sender;

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = plugin.getClaimManager().getClaim(chunk);

        if (claim == null) {
            plugin.getLocale().getMessage("command.general.notclaimed").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        final String name = String.join(" ", args);

        claim.setName(name);

        plugin.getDataManager().updateClaim(claim);

        plugin.getLocale().getMessage("command.name.set")
                .processPlaceholder("name", name)
                .sendPrefixedMessage(sender);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.admin.name";
    }

    @Override
    public String getSyntax() {
        return "admin name <name>";
    }

    @Override
    public String getDescription() {
        return "Set the display name for the claim you are standing in.";
    }
}
