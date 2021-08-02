package me.firerising.campmc.member;

import com.songoda.core.locale.Locale;
import me.firerising.campmc.Main;

import java.util.HashSet;
import java.util.Set;

public class ClaimPermissions {

    private final Set<ClaimPerm> permissions = new HashSet<>();

    public ClaimPermissions setAllowed(ClaimPerm perm, boolean allowed) {
        if (allowed)
            permissions.add(perm);
        else
            permissions.remove(perm);
        return this;
    }

    public boolean hasPermission(ClaimPerm perm) {
        return permissions.contains(perm);
    }

    public String getStatus(ClaimPerm perm) {
        Locale locale = Main.getInstance().getLocale();
        return hasPermission(perm) ? locale.getMessage("general.status.true").getMessage() : locale.getMessage("general.status.false").getMessage();
    }
}
