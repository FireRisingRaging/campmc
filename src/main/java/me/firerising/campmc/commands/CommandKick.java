package me.firerising.campmc.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.utils.PlayerUtils;
import me.firerising.campmc.events.ClaimPlayerKickEvent;
import me.firerising.campmc.claim.Claim;
import me.firerising.campmc.member.ClaimMember;
import me.firerising.campmc.member.ClaimRole;
import me.firerising.campmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandKick extends AbstractCommand {

    private final Main plugin;

    public CommandKick(Main plugin) {
        super(true, "kick");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);
        ClaimMember target = claim.getMember(args[0]);
        OfflinePlayer toKick;

        if(target != null) {
            toKick = target.getPlayer();
        } else {
            // unknown player: double-check
            toKick = Bukkit.getOfflinePlayer(args[0]);

            if (toKick == null || !(toKick.hasPlayedBefore() || toKick.isOnline())) {
                plugin.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            } else if (player.getUniqueId().equals(toKick.getUniqueId())) {
                plugin.getLocale().getMessage("command.kick.notself").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            // all good!
            target = claim.getMember(toKick.getUniqueId());
        }

        if (target == null || target.getRole() != ClaimRole.MEMBER) {
            plugin.getLocale().getMessage("command.general.notinclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        ClaimPlayerKickEvent event = new ClaimPlayerKickEvent(claim, toKick);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ReturnType.FAILURE;
        }

        if (toKick.isOnline())
            plugin.getLocale().getMessage("command.kick.kicked")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(toKick.getPlayer());

        plugin.getLocale().getMessage("command.kick.kick")
                .processPlaceholder("name", toKick.getName())
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        // and YEET!
        target.setRole(ClaimRole.VISITOR);
        plugin.getDataManager().deleteMember(target);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            // todo: list out members in this player's owned claim
            return PlayerUtils.getVisiblePlayerNames(sender, args[0]);
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.kick";
    }

    @Override
    public String getSyntax() {
        return "kick <member>";
    }

    @Override
    public String getDescription() {
        return "Kick a member from your claim.";
    }
}
