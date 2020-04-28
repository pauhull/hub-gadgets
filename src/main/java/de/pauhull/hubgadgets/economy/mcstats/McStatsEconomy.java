package de.pauhull.hubgadgets.economy.mcstats;

// Project: hub-gadgets
// Class created on 25.03.2020 by Paul
// Package de.pauhull.hubgadgets.economy.mcstats

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.economy.Economy;
import net.mcstats2.core.MCSCore;
import net.mcstats2.core.api.mcsentity.player.MCSPlayer;
import net.mcstats2.mcmoney.manager.MCMoneyManager;

import java.util.UUID;
import java.util.function.Consumer;

public class McStatsEconomy implements Economy {

    private HubGadgets hubGadgets;

    public McStatsEconomy(HubGadgets hubGadgets) {

        this.hubGadgets = hubGadgets;
    }

    @Override
    public void getCoins(UUID uuid, Consumer<Double> consumer) {

        this.hubGadgets.getExecutorService().execute(() -> {

            MCSPlayer mcsPlayer;

            try {
                mcsPlayer = MCSCore.getInstance().getPlayer(uuid);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            consumer.accept(MCMoneyManager.getInstance().getProfile(mcsPlayer).getCoins());
        });
    }

    @Override
    public void getCredits(UUID uuid, Consumer<Double> consumer) {

        this.hubGadgets.getExecutorService().execute(() -> {

            MCSPlayer mcsPlayer;

            try {
                mcsPlayer = MCSCore.getInstance().getPlayer(uuid);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            consumer.accept(MCMoneyManager.getInstance().getProfile(mcsPlayer).getCredits());
        });
    }

    @Override
    public void setCoins(UUID uuid, double coins) {

        this.hubGadgets.getExecutorService().execute(() -> {

            MCSPlayer mcsPlayer;

            try {
                mcsPlayer = MCSCore.getInstance().getPlayer(uuid);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            MCMoneyManager.getInstance().getProfile(mcsPlayer).setCoins(coins);
        });
    }

    @Override
    public void setCredits(UUID uuid, double credits) {

        this.hubGadgets.getExecutorService().execute(() -> {

            MCSPlayer mcsPlayer;

            try {
                mcsPlayer = MCSCore.getInstance().getPlayer(uuid);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            MCMoneyManager.getInstance().getProfile(mcsPlayer).setCredits(credits);
        });
    }
}
