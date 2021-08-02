package me.firerising.campmc.claim;

import com.songoda.core.locale.Locale;
import me.firerising.campmc.Main;

import java.util.HashSet;
import java.util.Set;

public class ClaimSettings {

    private final Set<ClaimSetting> settings = new HashSet<>();

    public ClaimSettings setEnabled(ClaimSetting setting, boolean enabled) {
        if (enabled)
            settings.add(setting);
        else
            settings.remove(setting);
        return this;
    }

    public boolean isEnabled(ClaimSetting setting) {
        return settings.contains(setting);
    }

    public String getStatus(ClaimSetting setting) {
        Locale locale = Main.getInstance().getLocale();
        return isEnabled(setting) ? locale.getMessage("general.status.true").getMessage() : locale.getMessage("general.status.false").getMessage();
    }
}
