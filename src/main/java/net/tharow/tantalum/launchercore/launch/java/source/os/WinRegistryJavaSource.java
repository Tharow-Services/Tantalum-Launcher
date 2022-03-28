

package net.tharow.tantalum.launchercore.launch.java.source.os;


import net.tharow.tantalum.launchercore.launch.java.IVersionSource;
import net.tharow.tantalum.launchercore.launch.java.JavaVersionRepository;
import net.tharow.tantalum.launchercore.launch.java.version.FileBasedJavaVersion;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;

/**
 * Pull the installed java versions from the registry, using REG QUERY command line utility
 *
 */
public class WinRegistryJavaSource implements IVersionSource {
    @Override
    public void enumerateVersions(JavaVersionRepository repository) {
        // Oracle
        enumerateRegistryForBitness(repository, "32", "Software\\JavaSoft", "JavaHome");
        enumerateRegistryForBitness(repository, "64", "Software\\JavaSoft", "JavaHome");

        // AdoptOpenJDK
        enumerateRegistryForBitness(repository, "32", "Software\\AdoptOpenJDK", "Path");
        enumerateRegistryForBitness(repository, "64", "Software\\AdoptOpenJDK", "Path");

        // Microsoft
        enumerateRegistryForBitness(repository, "64", "Software\\Microsoft\\JDK", "Path");

        // Azul Zulu
        enumerateRegistryForBitness(repository, "32", "Software\\Azul Systems\\Zulu", "InstallationPath");
        enumerateRegistryForBitness(repository, "64", "Software\\Azul Systems\\Zulu", "InstallationPath");

        // BellSoft Liberica
        enumerateRegistryForBitness(repository, "32", "Software\\BellSoft\\Liberica", "InstallationPath");
        enumerateRegistryForBitness(repository, "64", "Software\\BellSoft\\Liberica", "InstallationPath");
    }

    private void enumerateRegistryForBitness(JavaVersionRepository repository, String bitness, String keyPath, String keyName) {
        String output = Utils.getProcessOutput("reg", "query", "HKEY_LOCAL_MACHINE\\" + keyPath, "/f", keyName, "/t", "REG_SZ", "/s", "/reg:" + bitness);

        if (output == null || output.isEmpty())
            return;

        //Split reg query response into lines
        for (String line : output.split("\\r?\\n")) {

            //Reg query is a response that's like
            //Key Path
            //Key Name          Type            Value

            //We want to grab all the values that were returned and parse them into java home paths.
            //The type is always REG_SZ because that's what we searched for, so we find the type & expect the value to be afterward

            int typeIndex = line.indexOf("REG_SZ");

            if (typeIndex < 0)
                continue;

            typeIndex += "REG_SZ".length();

            String path = line.substring(typeIndex).trim();
            repository.addVersion(new FileBasedJavaVersion(new File(path + File.separator + "bin" + File.separator + "javaw.exe")));
        }
    }
}
