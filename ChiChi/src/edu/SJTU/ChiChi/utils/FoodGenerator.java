package edu.SJTU.ChiChi.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class FoodGenerator {
    String json_url = "http://wl.ibox.sjtu.edu.cn/w/8207/food.json";
    JSONArray json = null;
    String test =
            "[" +
                    "{" +
                    "'building':'hale'," +
                    "'restaurants':" +
                    "[{" +
                    "'restaurant':'hale'," +
                    "'foods':" +
                    "[{" +
                    "'name':'lamian'," +
                    "'url':'http://fmn.rrimg.com/fmn064/20130824/0005/large_AxBe_056400008651125d.jpg'" +
                    "}]" +
                    "}]" +
                    "}" +
                    "]";

    public FoodGenerator() {
        String json_string = JSONParser.getJsonFromUrl(json_url);
        //String json_string = test;
        try {
            if (json_string == null) return;
            json = new JSONArray(json_string);
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

    public boolean noerror() {
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
    
    public Food[] getFoods(int bid){
    	Food[] res = new Food[3];
    	int bc = getBuildingCount();
    	res[0] = getFood((bid+bc-1)%bc);
    	if(res[0]==null) return null;
    	res[1] = getFood(bid);
    	if(res[1]==null) return null;
    	res[2] = getFood((bid+bc+1)%bc);
    	if(res[2]==null) return null;
    	return res;
    }
}
