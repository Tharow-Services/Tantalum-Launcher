/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.launchercore.install;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.utilslib.Utils;

import java.io.IOException;

public record ModpackInstaller<VersionData>(IPlatformApi platformApi,
                                            String clientId) {

    public VersionData installPack(InstallTasksQueue<VersionData> tasksQueue, ModpackModel modpack, String build) throws IOException, InterruptedException {
        modpack.save();
        modpack.initDirectories();

        Version installedVersion = modpack.getInstalledVersion();
        tasksQueue.runAllTasks();

        Version versionFile = new Version(build, false);
        versionFile.save(modpack.getBinDir());

        if (installedVersion == null) {
            platformApi.incrementPackInstalls(modpack.getName());
            Utils.sendTracking("installModpack", modpack.getName(), modpack.getBuild(), clientId);
        }

        return tasksQueue.getMetadata();
    }
}
