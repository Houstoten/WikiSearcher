package run;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

public class Search {
    public static String input() {
        System.out.println("Enter query below");
        String input = null;
        try {
            input = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input != null;
        if (input.trim().length() == 0) {
            System.out.println("Query cannot be blank");
            return input();
        } else {
            return input;
        }
    }

    public static String ConnectAndGetJsonString(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append('\n');
           // System.out.println(line);
        }
        reader.close();
        connection.disconnect();
        return stringBuilder.toString();
    }

    public static void outputNewPortion(Iterator<? extends JsonElement> jsonIterator, boolean newSearch) throws IOException {
        System.out.println();
        jsonIterator = initPortionIterator(newSearch);
        while (jsonIterator.hasNext()) {
            System.out.println(((JsonObject) jsonIterator.next()).get("title").toString());
        }
        commandPallete(jsonIterator);
    }

    public static void commandPallete(Iterator<? extends JsonElement> jsoniterator) throws IOException {
        System.out.println();
        System.out.println("Enter 'next' to see next 10 results. ");
        System.out.println("Enter 'prev' to see previous 10 results. ");
        System.out.println("Enter 'new' to set new query. ");
        System.out.println("Enter 'exit' to close utility. ");

        String input = null;
        try {
            input = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input != null;
        switch (input.trim()) {
            case "next":
                pagescounter++;
                outputNewPortion(jsoniterator, false);
                break;

            case "prev":
                pagescounter--;
                outputNewPortion(jsoniterator, false);
                break;

            case "new":
                break;

            case "exit":
                System.exit(0);
                break;

            default:
                System.out.println();
                System.out.println("Wrong input");
                commandPallete(jsoniterator);
        }
    }

    public static Iterator<? extends JsonElement> initPortionIterator(boolean newSearch) throws IOException {
        String url = initURL();
        String jsonString = ConnectAndGetJsonString(url);

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        jsonString = null;
        JsonObject jsonObjectQuery = (JsonObject) jsonObject.get("query");
        JsonArray jsonArray = jsonObjectQuery.get("search").getAsJsonArray();
        if (newSearch)
            System.out.println("Founded " + ((JsonObject) jsonObjectQuery.get("searchinfo")).get("totalhits").toString() + " results.");
        return jsonArray.iterator();
    }

    static int pagescounter = 0;
    static String query = "";

    public static String initURL() {
        return "https://ru.wikipedia.org/w/api.php?action=" +
                "query&list=search&utf8=&format=json&sroffset=" + pagescounter * 10 + "&srsearch=\"" + query + "\"";
    }

    public static void main(String[] args) {
        query = input();

        String url = initURL();
        try {
            outputNewPortion(initPortionIterator(false), true);
        } catch (IOException ex) {
            System.out.println("Please check that your query is correct.");
        } finally {
            System.out.println();
            main(new String[]{});
        }
    }
}
