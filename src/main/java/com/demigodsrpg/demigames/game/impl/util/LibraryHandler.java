/*
 * Copyright (c) 2015 Demigods RPG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.demigodsrpg.demigames.game.impl.util;

import org.bukkit.plugin.Plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryHandler {
    // -- IMPORTANT FIELDS -- //

    public static final String MAVEN_CENTRAL = "http://central.maven.org/maven2/";
    private static final int BYTE_SIZE = 1024;

    private final List<String> FILE_NAMES;
    private final Plugin PLUGIN;
    private final File LIB_DIRECTORY;

    // -- CONSTRUCTOR -- //

    public LibraryHandler(Plugin plugin) {
        this.PLUGIN = plugin;
        FILE_NAMES = new ArrayList<>();
        LIB_DIRECTORY = new File(PLUGIN.getDataFolder().getPath() + "/lib");
        checkDirectory();
    }

    // -- HELPER METHODS -- //

    public void addMavenLibrary(String repo, String groupId, String artifactId, String version) {
        try {
            String fileName = artifactId + "-" + version + ".jar";
            loadLibrary(fileName, new URI(repo + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" +
                    fileName).toURL());
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public void checkDirectory() {
        // If it exists and isn't a directory, throw an error
        if (LIB_DIRECTORY.exists() && !LIB_DIRECTORY.isDirectory()) {
            PLUGIN.getLogger().severe("The library directory isn't a directory!");
            return;
        }
        // Otherwise, make the directory
        else if (!LIB_DIRECTORY.exists()) {
            LIB_DIRECTORY.mkdirs();
        }

        // Check if all libraries exist

        File[] filesArray = LIB_DIRECTORY.listFiles();
        List<File> files = Arrays.asList(filesArray != null ? filesArray : new File[]{});

        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                FILE_NAMES.add(file.getName());
            }
        }
    }

    public void loadLibrary(String fileName, URL url) {
        // Check if the files are found or not
        File libraryFile = null;
        if (FILE_NAMES.contains(fileName)) {
            libraryFile = new File(LIB_DIRECTORY + "/" + fileName);
        }

        // If they aren't found, download them
        if (libraryFile == null) {
            PLUGIN.getLogger().warning(fileName + " is missing, downloading now.");
            libraryFile = downloadLibrary(fileName, url);
        }

        // Add the library to the classpath
        addToClasspath(libraryFile);
    }

    public void addToClasspath(File file) {
        try {
            ClassPathHack.addFile(file, (URLClassLoader) PLUGIN.getClass().getClassLoader());
        } catch (Exception oops) {
            PLUGIN.getLogger().severe("Couldn't load " + (file != null ? file.getName() : "a required library") +
                    ", this may cause problems.");
            oops.printStackTrace();
        }
    }

    public File downloadLibrary(String libraryFileName, URL libraryUrl) {
        // Get the file
        File libraryFile = new File(LIB_DIRECTORY.getPath() + "/" + libraryFileName);

        // Create the streams
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try {
            // Setup the streams
            in = new BufferedInputStream(libraryUrl.openStream());
            fout = new FileOutputStream(libraryFile);

            // Create variables for loop
            final byte[] data = new byte[BYTE_SIZE];
            int count;

            // Write the data to the file
            while ((count = in.read(data, 0, BYTE_SIZE)) != -1) {
                fout.write(data, 0, count);
            }

            PLUGIN.getLogger().info("Download complete.");

            // Return the file
            return libraryFile;
        } catch (final Exception oops) {
            // Couldn't download the file
            PLUGIN.getLogger().severe("Download could not complete");
        } finally {
            // Close the streams
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (final Exception ignored) {
            }
        }

        return null;
    }
}
