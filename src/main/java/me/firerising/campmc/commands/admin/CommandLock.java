package me.firerising.campmc.commands.admin;

import com.songoda.core.commands.AbstractCommand;
import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.member.ClaimMember;
import me.firerising.campmc.member.ClaimRole;
import me.firerising.campmc.Main;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandLock extends AbstractCommand {

    private final Main plugin;

    public CommandLock(Main plugin) {
        super(true, "admin lock");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = plugin.getClaimManager().getClaim(chunk);

        if (claim == null) {
            plugin.getLocale().getMessage("command.general.notclaimed").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!claim.isLocked()) {
            plugin.getLocale().getMessage("command.lock.lockedother")
                    .sendPrefixedMessage(player);
            for (ClaimMember member : claim.getMembers().stream().filter(m -> m.getRole() == ClaimRole.VISITOR)
                    .collect(Collectors.toList())) {
                member.eject(null);
            }
        } else
            plugin.getLocale().getMessage("command.lock.unlockedother")
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
        return "ultimateclaims.admin.lock";
    }

    @Override
    public String getSyntax() {
        return "admin lock";
    }

    @Override
    public String getDescription() {
        return "Lock or unlock the claim you are standing in.";
    }
}
