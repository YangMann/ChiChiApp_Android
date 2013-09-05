package edu.SJTU.ChiChi.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class FoodGenerator {
    String json_url = "http://wl.ibox.sjtu.edu.cn/w/8207/food.json";
    JSONArray json = null;

    public FoodGenerator() {
    }

    public void fetchJSON() {
        String json_string = JSONParser.getJsonFromUrl(json_url);
        //String json_string = test;
        try {
            if (json_string == null) return;
            json = new JSONArray(json_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setJSON(String jsonString) {
        try {
//            Log.v("jsonString", jsonString);
            json = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class Food {
        public String name;
        public String building;
        public String restaurant;
        public String url;
        public String genre;
        public String price;
        public String taste;
        public String description;

        public Food(String building, String restaurant, JSONObject fd) throws JSONException {
            this.name = fd.getString("name");
            this.building = building;
            this.restaurant = restaurant;
            this.url = fd.getString("url");
            this.genre = fd.has("genre") ? fd.getString("genre") : "";
            this.price = fd.has("price") ? fd.getString("price") : "";
            this.taste = fd.has("taste") ? fd.getString("taste") : "";
            this.description = fd.has("description") ? fd.getString("description") : "";
        }
    }

    public boolean noError() {
        return json != null;
    }

    public int getBuildingCount() {
        return json.length();
    }

    public Food getFood(int bid) {
        try {
            JSONObject bd = json.getJSONObject(bid);
            String building = bd.getString("building");
            int rc = bd.getJSONArray("restaurants").length();
            Random rand = new Random();
            int rid = Math.abs(rand.nextInt() % rc);
            JSONObject rs = bd.getJSONArray("restaurants").getJSONObject(rid);
            String restaurant = rs.getString("restaurant");
            int fc = rs.getJSONArray("foods").length();
            int fid = Math.abs(rand.nextInt() % fc);
            JSONObject fd = rs.getJSONArray("foods").getJSONObject(fid);
            return new Food(building, restaurant, fd);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Food[] getFoods() {
        int bc = getBuildingCount();
        Food[] res = new Food[bc];
        for (int i = 0; i < bc; ++i) {
            res[i] = getFood(i);
            if (res[i] == null) return null;
        }
        return res;
    }

    public Food randomFood() {
        return getFood(Math.abs(new Random().nextInt()) % getBuildingCount());
    }
}
