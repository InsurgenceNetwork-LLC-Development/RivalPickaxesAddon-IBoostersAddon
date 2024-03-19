package org.insurgence.addons.rivalpickaxes.listeners;

import com.google.common.util.concurrent.AtomicDouble;
import me.rivaldev.pickaxes.api.events.PickaxeEssenceReceivePreEnchantEvent;
import me.rivaldev.pickaxes.api.events.PickaxeMoneyReceiveEnchant;
import me.rivaldev.pickaxes.api.events.PickaxeXPGainEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.insurgencedev.insurgenceboosters.api.IBoosterAPI;
import org.insurgencedev.insurgenceboosters.data.BoosterFindResult;
import org.insurgencedev.insurgenceboosters.data.PermanentBoosterData;

import java.util.Optional;

public final class RivalPickaxeEventListener implements Listener {

    private final String namespace = "RIVAL_PICKAXES";

    @EventHandler
    private void onReceive(PickaxeEssenceReceivePreEnchantEvent event) {
        double multi = getMulti(event.getPlayer(), "Essence");

        if (multi > 0) {
            event.setEssence(calculateAmount(event.getEssence(), multi));
        }
    }

    @EventHandler
    private void onReceive(PickaxeMoneyReceiveEnchant event) {
        double multi = getMulti(event.getPlayer(), "Money");

        if (multi > 0) {
            event.setMoney(calculateAmount(event.getMoney(), multi));
        }
    }

    @EventHandler
    private void onGain(PickaxeXPGainEvent event) {
        double multi = getMulti(event.getPlayer(), "XP");

        if (multi > 0) {
            event.setXP(calculateAmount(event.getXP(), multi));
        }
    }

    private double getMulti(Player player, String type) {
        AtomicDouble totalMulti = new AtomicDouble(getPersonalPermMulti(player, type) + getGlobalPermMulti(type));

        BoosterFindResult pResult = IBoosterAPI.INSTANCE.getCache(player).getBoosterDataManager().findActiveBooster(type, namespace);
        if (pResult instanceof BoosterFindResult.Success boosterResult) {
            totalMulti.getAndAdd(boosterResult.getBoosterData().getMultiplier());
        }

        IBoosterAPI.INSTANCE.getGlobalBoosterManager().findGlobalBooster(type, namespace, globalBooster -> {
            totalMulti.getAndAdd(globalBooster.getMultiplier());
            return null;
        }, () -> null);

        return totalMulti.get();
    }

    private double getPersonalPermMulti(Player uuid, String type) {
        Optional<PermanentBoosterData> foundMulti = Optional.ofNullable(IBoosterAPI.INSTANCE.getCache(uuid).getPermanentBoosts().getPermanentBooster(type, namespace));
        return foundMulti.map(PermanentBoosterData::getMulti).orElse(0d);
    }

    private double getGlobalPermMulti(String type) {
        AtomicDouble multi = new AtomicDouble(0d);

        IBoosterAPI.INSTANCE.getGlobalBoosterManager().findPermanentBooster(type, namespace, data -> {
            multi.set(data.getMulti());
            return null;
        }, () -> null);

        return multi.get();
    }

    private double calculateAmount(double amount, double multi) {
        return amount * (multi < 1 ? 1 + multi : multi);
    }
}
