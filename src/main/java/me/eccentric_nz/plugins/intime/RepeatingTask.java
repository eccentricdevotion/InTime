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

public class RepeatingTask implements Runnable {

    InTime plugin;
    //The time of the last updates in milliseconds
    private long lastUpdateCheck;
    private long lastUpdateSigns;

    public RepeatingTask(InTime instance) {
        plugin = instance;
    }

    public void run() {
        //if current time > time of last update + updateinterval (1 second = 1000 milliseconds)
        if (System.currentTimeMillis() > lastUpdateCheck + plugin.config().getUpdateInterval() * 1000) {
            plugin.getTimeHandler().checkTime();
            lastUpdateCheck = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() > lastUpdateSigns + plugin.config().getUpdateInterval() * 1000) {
            plugin.getTimeHandler().updateSigns();
            lastUpdateSigns = System.currentTimeMillis();
        }
    }
}
