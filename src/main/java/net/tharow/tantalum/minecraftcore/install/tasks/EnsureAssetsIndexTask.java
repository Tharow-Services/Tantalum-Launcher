/*
 * This file is part of Technic Minecraft Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Minecraft Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Minecraft Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Minecraft Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.minecraftcore.install.tasks;

import net.tharow.tantalum.launchercore.install.ITasksQueue;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.DownloadFileTask;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.install.verifiers.IFileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.SHA1FileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.ValidJsonFileVerifier;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.minecraftcore.mojang.version.MojangVersion;
import net.tharow.tantalum.minecraftcore.mojang.version.io.AssetIndex;

import java.io.File;
import java.io.IOException;

public record EnsureAssetsIndexTask(File assetsDirectory,
                                    ModpackModel modpack,
                                    ITasksQueue downloadIndexQueue,
                                    ITasksQueue examineIndexQueue,
                                    ITasksQueue checkAssetsQueue,
                                    ITasksQueue downloadAssetsQueue,
                                    ITasksQueue installAssetsQueue) implements IInstallTask {

    @Override
    public String getTaskDescription() {
        return "Retrieving assets index";
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException {
        @SuppressWarnings("unchecked") MojangVersion version = ((InstallTasksQueue<MojangVersion>) queue).getMetadata();

        String assetKey = version.getAssetsKey();
        if (assetKey == null || assetKey.isEmpty()) {
            assetKey = "legacy";
        }

        String assetsUrl;
        AssetIndex assetIndex = version.getAssetIndex();
        if (assetIndex != null) {
            assetsUrl = assetIndex.getUrl();
        } else {
            assetsUrl = MojangUtils.getAssetsIndex(assetKey);
        }

        File output = new File(assetsDirectory + File.separator + "indexes", assetKey + ".json");

        (new File(output.getParent())).mkdirs();

        IFileVerifier fileVerifier;

        if (assetIndex != null && assetIndex.getSha1() != null)
            fileVerifier = new SHA1FileVerifier(assetIndex.getSha1());
        else
            fileVerifier = new ValidJsonFileVerifier(MojangUtils.getGson());

        if (!output.exists() || !fileVerifier.isFileValid(output)) {
            downloadIndexQueue.addTask(new DownloadFileTask(assetsUrl, output, fileVerifier));
        }

        examineIndexQueue.addTask(new InstallMinecraftAssetsTask(modpack, assetsDirectory.getAbsolutePath(), output, checkAssetsQueue, downloadAssetsQueue, installAssetsQueue));
    }

}
