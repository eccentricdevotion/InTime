/*
 * Copyright 2011 Schwarzer Zylinder. All rights reserved.
 *
 * Development and maintenance taken over by Rob Rate 2012.
 *
 * Licensed under The GNU General Public License v3.0, a copy of the licence is included in the JAR file.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package me.eccentric_nz.plugins.intime;

import java.io.IOException;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class InTime extends JavaPlugin {

    private final InTimePlayerListener playerListener = new InTimePlayerListener(this);
    private final InTimeBlockListener blockListener = new InTimeBlockListener(this);
    private TimeHandler timeHandler = new TimeHandler(this);
    private PluginManager pm;
    private PluginDescriptionFile pdf;
    private Vault vault;
    private Economy economy;
    private Configuration config;
    private FileManager fileManager;
    //plugin name in square brackets, can be set as identifier in front of a message: [DynamicShop] blabla
    public String name;

    @Override
    public void onDisable() {
        //Cancel all tasks
        getServer().getScheduler().cancelTasks(this);
        config.saveConfig();
    }

    @Override
    public void onEnable() {
        pm = this.getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pdf = this.getDescription();
        fileManager = new FileManager(this);
        config = new Configuration(this);
        name = "[" + pdf.getName() + "] ";

        if (!setupVault()) {
            pm.disablePlugin(this);
            return;
        }
        setupEconomy();
        config.loadConfig();
        getTimeHandler().loadSigns();

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

        //Start task to check time
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new RepeatingTask(this), 0, 30);
        printMessage("version " + pdf.getVersion() + " by Schwarzer Zylinder & eccentric_nz is enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return playerListener.onCommand(sender, command, label, args);
    }

    private boolean setupVault() {
        Plugin x = pm.getPlugin("Vault");
        if (x != null && x instanceof Vault) {
            vault = (Vault) x;
            return true;
        } else {
            printWarning("Vault is required for economy, but wasn't found!");
            printWarning("Download it from http://dev.bukkit.org/server-mods/vault/");
            printWarning("Disabling plugin.");
            return false;
        }
    }

    //Loading economy API from Vault
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public void printMessage(String message) {
        System.out.println(name + message);
    }

    public void printWarning(String warning) {
        System.err.println(name + warning);
    }

    public String getPluginName() {
        return ChatColor.AQUA + name;
    }

    public TimeHandler getTimeHandler() {
        return timeHandler;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Configuration config() {
        return config;
    }

    public InTimePlayerListener getPlayerListener() {
        return playerListener;
    }

    public InTimeBlockListener getBlockListener() {
        return blockListener;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PluginDescriptionFile getPdf() {
        return pdf;
    }
}