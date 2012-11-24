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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class InTimeBlockListener implements Listener {

    private static InTime plugin;

    public InTimeBlockListener(InTime instance) {
        plugin = instance;
    }

    //This is to check all placed signs. If a InTime-sign is placed, it will be saved
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        //Check whether this is a sign referred to InTime plugin
        if (event.getLine(0).equalsIgnoreCase("[InTime]")) {
            //Check permissions
            if (plugin.getPlayerListener().checkPermissions((CommandSender) player, "createsigns", false)) {
                String playername = event.getLine(1);
                if (plugin.getPlayerListener().playerExist(playername)) {
                    plugin.getFileManager().saveSignValue(block, event.getLine(1));
                    plugin.getTimeHandler().loadSigns();
                    plugin.getTimeHandler().updateSigns();
                    player.sendMessage(plugin.getPluginName() + "Sign succesfully created.");
                } else {
                    player.sendMessage(plugin.getPluginName() + "This player doesn't exist! Sign will be dropped.");
                    dropSign(block.getLocation());
                }
            } else {
                //drop sign
                player.sendMessage(plugin.getPluginName() + "You do not have permissions to do that! Sign will be dropped.");
                dropSign(block.getLocation());
            }
        }
    }

    private void dropSign(Location location) {
        location.getBlock().setType(Material.AIR);
        location.getWorld().dropItemNaturally(location, new ItemStack(Material.SIGN, 1));
    }
}
