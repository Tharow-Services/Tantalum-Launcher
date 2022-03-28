

package net.tharow.tantalum.autoupdate;

import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.utilslib.OperatingSystem;
import net.tharow.tantalum.utilslib.Utils;

import javax.swing.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Relauncher {

    private String stream;
    private final int currentBuild;
    private final LauncherDirectories directories;
    private boolean didUpdate = false;

    public Relauncher(final int currentBuild, final LauncherDirectories directories, String stream){
        this.stream = stream;
        this.currentBuild = currentBuild;
        this.directories = directories;
    }

    public Relauncher(int currentBuild, LauncherDirectories directories) {
        this(currentBuild, directories, "stable4");
    }

    public int getCurrentBuild() { return currentBuild; }
    public String getStreamName() { return stream; }
    public void setUpdated() { didUpdate = true; }

    protected LauncherDirectories getDirectories() { return directories; }

    public String getRunningPath() throws UnsupportedEncodingException {
        return getRunningPath(getMainClass());
    }

    public static String getRunningPath(Class clazz) throws UnsupportedEncodingException {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.replace("+", URLEncoder.encode("+", String.valueOf(StandardCharsets.UTF_8)));
        return URLDecoder.decode(path, String.valueOf(StandardCharsets.UTF_8));
    }

    protected abstract Class getMainClass();
    public abstract String getUpdateText();
    public abstract boolean isUpdateOnly();
    public abstract boolean isMover();
    public abstract boolean isLauncherOnly();
    public abstract InstallTasksQueue buildMoverTasks();
    public abstract InstallTasksQueue buildUpdaterTasks();
    public abstract String[] getLaunchArgs();
    public abstract void updateComplete();
    public abstract boolean canReboot();

    public boolean runAutoUpdater() throws IOException, InterruptedException {
        if (isLauncherOnly())
            return true;

        boolean needsReboot = false;

        if (canReboot()) {
            if (System.getProperty("awt.useSystemAAFontSettings") == null || !System.getProperty("awt.useSystemAAFontSettings").equals("lcd"))
                needsReboot = true;
            else if (!Boolean.parseBoolean(System.getProperty("java.net.preferIPv4Stack")))
                needsReboot = true;
        }

        InstallTasksQueue updateTasksQueue = null;
        if (isMover()) {
            updateTasksQueue = buildMoverTasks();
        } else if (needsReboot && getCurrentBuild() > 0) {
            relaunch();
            return false;
        } else if (getCurrentBuild() < 1) {
            return true;
        } else {
            updateTasksQueue = buildUpdaterTasks();
        }

        if (updateTasksQueue == null)
            return true;

        updateTasksQueue.runAllTasks();
        updateComplete();

        return !didUpdate && isUpdateOnly();
    }

    public void relaunch() {
        launch(null, getLaunchArgs());
    }

    public File getTempLauncher() {
        File dest;
        String runningPath = null;

        try {
            runningPath = getRunningPath();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }

        if (runningPath.endsWith(".exe"))
            dest = new File(directories.getLauncherDirectory(), "temp.exe");
        else
            dest = new File(directories.getLauncherDirectory(), "temp.jar");
        return dest;
    }

    public void launch(String launchPath, String[] args) {
        if (launchPath == null) {
            try {
                launchPath = getRunningPath();
            } catch (UnsupportedEncodingException ex) {
                return;
            }
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        ArrayList<String> commands = new ArrayList<>();
        if (!launchPath.endsWith(".exe")) {
            commands.add(OperatingSystem.getJavaDir());
            commands.add("-Xmx256m");
            commands.add("-verbose");
            commands.add("-Djava.net.preferIPv4Stack=true");
            commands.add("-Dawt.useSystemAAFontSettings=lcd");
            commands.add("-Dswing.aatext=true");
            commands.add("-cp");
            commands.add(launchPath);
            commands.add(getMainClass().getName());
        } else
            commands.add(launchPath);
        commands.addAll(Arrays.asList(args));

        StringBuilder command = new StringBuilder();

        for (String token : commands) {
            command.append(token).append(" ");
        }

        Utils.getLogger().info("Launching command: '" + command + "'");

        processBuilder.command(commands);

        try {
            processBuilder.start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Your OS has prevented this relaunch from completing.  You may need to add an exception in your security software.", "Relaunch Failed", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public String[] buildMoverArgs() throws UnsupportedEncodingException {
        List<String> outArgs = new ArrayList<>();
        outArgs.add("-movetarget");
        outArgs.add(getRunningPath());
        outArgs.add("-moveronly");
        outArgs.addAll(Arrays.asList(getLaunchArgs()));
        return outArgs.toArray(new String[0]);
    }

    public String[] buildLauncherArgs(boolean isLegacy) {
        List<String> outArgs = new ArrayList<>();
        if (!isLegacy)
            outArgs.add("-launcheronly");
        else
            outArgs.add("-launcher");
        outArgs.addAll(Arrays.asList(getLaunchArgs()));
        outArgs.remove("-moveronly");
        return outArgs.toArray(new String[0]);
    }
}
