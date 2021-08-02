package me.firerising.campmc.commands;

import com.songoda.core.commands.AbstractCommand;
import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.claim.ClaimDeleteReason;
import me.firerising.campmc.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandDissolve extends AbstractCommand {

    private final Main plugin;

    public CommandDissolve(Main plugin) {
        super(true, "dissolve");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);

        claim.destroy(ClaimDeleteReason.PLAYER);
        plugin.getLocale().getMessage("general.claim.dissolve")
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.dissolve";
    }

    @Override
    public String getSyntax() {
        return "dissolve";
    }

    @Override
    public String getDescription() {
        return "Dissolve your claim.";
    }
}
