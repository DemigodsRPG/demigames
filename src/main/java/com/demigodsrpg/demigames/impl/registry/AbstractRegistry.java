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

package com.demigodsrpg.demigames.impl.registry;

import com.demigodsrpg.demigames.impl.Demigames;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class AbstractRegistry<K extends Serializable, V extends Serializable> {
    protected final Cache<K, V> REGISTERED_DATA;
    private final File FOLDER;
    private final Class<V> V_TYPE;

    public AbstractRegistry(String folder, Class<V> vType) {
        FOLDER = new File(Demigames.getInstance().getDataFolder().getPath() + "/" + folder + "/");
        V_TYPE = vType;
        REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(3, TimeUnit.MINUTES).build();
    }

    public Optional<V> fromKey(K key) {
        if (!REGISTERED_DATA.asMap().containsKey(key)) {
            loadFromFile(key);
        }
        return Optional.ofNullable(REGISTERED_DATA.asMap().getOrDefault(key, null));
    }

    public V put(K key, V value) {
        REGISTERED_DATA.put(key, value);
        saveToFile(key);
        return value;
    }

    public void removeIfPresent(K key) {
        if (REGISTERED_DATA.asMap().containsKey(key)) {
            REGISTERED_DATA.asMap().remove(key);
            removeFile(key);
        }
    }

    private void createFile(File file) {
        try {
            FOLDER.mkdirs();
            file.createNewFile();
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public void removeFile(K key) {
        File file = new File(FOLDER.getPath() + "/" + key.toString() + ".json");
        if (file.exists()) {
            file.delete();
        }
    }

    public void saveToFile(K key) {
        if (REGISTERED_DATA.asMap().containsKey(key)) {
            File file = new File(FOLDER.getPath() + "/" + key.toString() + ".json");
            if (!(file.exists())) {
                createFile(file);
            }
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(REGISTERED_DATA.asMap().get(key));
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.print(json);
                writer.close();
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }
    }

    public void loadFromFile(K key) {
        Gson gson = new GsonBuilder().create();
        try {
            File file = new File(FOLDER.getPath() + "/" + key.toString() + ".json");
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream);
                V value = gson.fromJson(reader, V_TYPE);
                REGISTERED_DATA.put(key, value);
                reader.close();
            }
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }
}
