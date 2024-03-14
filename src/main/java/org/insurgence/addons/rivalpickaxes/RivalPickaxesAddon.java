package org.insurgence.addons.rivalpickaxes;

import org.insurgence.addons.rivalpickaxes.listeners.RivalPickaxeEventListener;
import org.insurgencedev.insurgenceboosters.api.addon.IBoostersAddon;
import org.insurgencedev.insurgenceboosters.api.addon.InsurgenceBoostersAddon;
import org.insurgencedev.insurgenceboosters.libs.fo.Common;

@IBoostersAddon(name = "RivalPickaxesAddon", version = "1.0.1", author = "InsurgenceDev", description = "RivalPickaxes Support")
public class RivalPickaxesAddon extends InsurgenceBoostersAddon {

    @Override
    public void onAddonReloadAblesStart() {
        if (Common.doesPluginExist("RivalPickaxes")) {
            registerEvent(new RivalPickaxeEventListener());
        }
    }
}
