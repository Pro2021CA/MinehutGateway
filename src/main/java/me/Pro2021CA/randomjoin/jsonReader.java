package me.Pro2021CA.randomjoin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class jsonReader {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JsonObject readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream();
             BufferedReader rd = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String jsonText = readAll(rd);
            return JsonParser.parseString(jsonText).getAsJsonObject();
        }
    }

    public static List<String> getServers(String url) throws IOException {
        JsonObject json = readJsonFromUrl(url);

        List<String> servers = new ArrayList<>();

        JsonArray array = json.getAsJsonArray("servers");

        for (int i = 0; i < array.size(); i++) {
            JsonObject serverObj = array.get(i).getAsJsonObject();
            servers.add(serverObj.get("name").getAsString());
        }

        return servers;
    }
}
