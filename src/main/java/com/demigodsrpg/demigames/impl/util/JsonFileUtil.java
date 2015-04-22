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

package com.demigodsrpg.demigames.impl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Json file related utility methods.
 */
public class JsonFileUtil {
    // -- CONSTRUCTOR -- //

    /**
     * Private constructor to prevent instance calls.
     */
    private JsonFileUtil() {
    }

    // -- UTILITY METHODS -- //

    /**
     * Get the parent JsonSection from a json file.
     *
     * @param path     Path to the json file.
     * @param fileName The filename (incl. file extension).
     * @return The JsonSection for the entire file.
     */
    public static JsonSection getSection(String path, String fileName) {
        File dataFile = new File(path + fileName);
        if (!(dataFile.exists())) createFile(dataFile);
        return loadSection(dataFile);
    }

    /**
     * Load a JsonSection from a json file.
     *
     * @param dataFile The json file.
     * @return The JsonSection for the entire file.
     */
    @SuppressWarnings("unchecked")
    private static JsonSection loadSection(File dataFile) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        try {
            FileInputStream inputStream = new FileInputStream(dataFile);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Map<String, Object> sectionData = gson.fromJson(reader, Map.class);
            reader.close();
            return new JsonSection(sectionData);
        } catch (Exception oops) {
            oops.printStackTrace();
        }
        return null;
    }

    /**
     * Create a file and its directory path.
     *
     * @param dataFile The file object.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createFile(File dataFile) {
        try {
            // Create the directories.
            (dataFile.getParentFile()).mkdirs();

            // Create the new file.
            dataFile.createNewFile();
        } catch (Exception errored) {
            throw new RuntimeException("DGClassic couldn't create a data file!", errored);
        }
    }

    /**
     * Save a JsonSection as a json file.
     *
     * @param path     The path to the json file.
     * @param fileName The filename (incl. file extension).
     * @param section  The JsonSection to be saved.
     * @return Save success or failure.
     */
    public static boolean saveFile(String path, String fileName, JsonSection section) {
        File dataFile = new File(path + fileName);
        if (!(dataFile.exists())) createFile(dataFile);
        return section.save(dataFile);
    }

    /**
     * Save a JsonSection as a json file in a pretty format.
     *
     * @param path     The path to the json file.
     * @param fileName The filename (incl. file extension).
     * @param section  The JsonSection to be saved.
     * @return Save success or failure.
     */
    public static boolean saveFilePretty(String path, String fileName, JsonSection section) {
        File dataFile = new File(path + fileName);
        if (!(dataFile.exists())) createFile(dataFile);
        return section.savePretty(dataFile);
    }
}
