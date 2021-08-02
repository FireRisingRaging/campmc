package me.firerising.campmc.commands;

import com.songoda.core.commands.AbstractCommand;
import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.member.ClaimMember;
import me.firerising.campmc.member.ClaimRole;
import me.firerising.campmc.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandLock extends AbstractCommand {

    private final Main plugin;

    public CommandLock(Main plugin) {
        super(true, "lock");
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

        if (!claim.isLocked()) {
            plugin.getLocale().getMessage("command.lock.locked")
                    .sendPrefixedMessage(player);
            for (ClaimMember member : claim.getMembers().stream().filter(m -> m.getRole() == ClaimRole.VISITOR)
                    .collect(Collectors.toList())) {
                member.eject(null);
            }
        } else
            plugin.getLocale().getMessage("command.lock.unlocked")
                    .sendPrefixedMessage(player);

        claim.setLocked(!claim.isLocked());

        plugin.getDataManager().updateClaim(claim);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.lock";
    }

    @Override
    public String getSyntax() {
        return "lock";
    }

    @Override
    public String getDescription() {
        return "Lock or unlock your claim.";
    }
}
