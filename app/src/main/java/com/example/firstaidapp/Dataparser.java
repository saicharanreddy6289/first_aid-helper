package com.example.firstaidapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dataparser {
// single hospital data is parsed here
    private HashMap<String,String> gethospital(JSONObject googlePlaceJson)  {
        HashMap<String,String> googleplacemap=new HashMap<>();
        String Nameofplace="";
        String latitude="";
        String longitude="";
            try {
                Nameofplace=googlePlaceJson.getString("name");
                latitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude=googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        googleplacemap.put("place_name",Nameofplace);

        googleplacemap.put("lat",latitude);
        googleplacemap.put("lng",longitude);


        return googleplacemap;
    }

    // List of all the hospital objects is parsed here

    private List<HashMap<String,String>> getallnearbyhospitals(JSONArray jsonArray) throws JSONException {
        int count=jsonArray.length();
        List<HashMap<String,String>> nearbyhospital_list=new ArrayList<>();
        HashMap<String,String> nearbymap=null;
        int i=0;
        while(i<count){
            nearbymap=gethospital((JSONObject)jsonArray.get(i));
            nearbyhospital_list.add(nearbymap);
            i++;
        }
        return nearbyhospital_list;
    }


    // the jsondata of google places api is parsed here
    public List<HashMap<String,String>> parse(String jsondata) throws JSONException {
        JSONArray jsonArray=null;
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(jsondata);
            jsonArray=jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getallnearbyhospitals(jsonArray);

    }

}
