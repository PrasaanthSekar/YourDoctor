package com.finalproject.yourdoctor.library;

import android.content.Context;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class UserFunctions {
    private JSONParser jsonParser;
    //URL of the PHP API
    private static String loginURL = "http://yourdoctor.url.ph/Web-Service/";
    private static String registerURL = "http://yourdoctor.url.ph/Web-Service/";
    private static String forpassURL = "http://yourdoctor.url.ph/Web-Service/";
    private static String chgpassURL = "http://yourdoctor.url.ph/Web-Service/";

    private static String sendURL = "http://yourdoctor.url.ph/Web-Service/GetTime/Time.php";

    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String forpass_tag = "forpass";
    private static String chgpass_tag = "chgpass";
    private static String senddata_tag = "send";
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
    /**
     * Function to Login
     **/
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List params = new ArrayList();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public JSONObject getPlaces(){
        List params =  new ArrayList();
        JSONObject json = jsonParser.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=11.0183,76.9725&radius=500&types=clinic&key=AIzaSyBEOKekpNyoCABDJY4tZHtMOCqitm_Xy1o",params);
        System.out.println(""+json);
        return json;
    }
    /**
     * Function to change password
     **/
    @SuppressWarnings("unchecked")
    public JSONObject chgPass(String newpas, String email){
        @SuppressWarnings("rawtypes")
        List params = new ArrayList();
        params.add(new BasicNameValuePair("tag", chgpass_tag));
        params.add(new BasicNameValuePair("newpas", newpas));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
        return json;
    }
    /**
     * Function to reset the password
     **/

    @SuppressWarnings("unchecked")
    public JSONObject forPass(String forgotpassword){
        @SuppressWarnings("rawtypes")
        List params = new ArrayList();
        params.add(new BasicNameValuePair("tag", forpass_tag));
        params.add(new BasicNameValuePair("forgotpassword", forgotpassword));
        JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
        return json;
    }
    /**
     * Function to  Register
     **/
    @SuppressWarnings("unchecked")
    public JSONObject registerUser(String fname, String lname, String email, String uname, String password){
        // Building Parameters
        @SuppressWarnings("rawtypes")
        List params = new ArrayList();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("lname", lname));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("uname", uname));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject sendData(String id){
        // Building Parameters
        @SuppressWarnings("rawtypes")
        List locparams = new ArrayList();
        locparams.add(new BasicNameValuePair("did", id));
        JSONObject json = jsonParser.getJSONFromUrl(sendURL,locparams);
        return json;
    }

    public JSONObject filterDoctors(String loc){
        // Building Parameters
        @SuppressWarnings("rawtypes")
        List docparams = new ArrayList();
        docparams.add(new BasicNameValuePair("location", loc));
        JSONObject json = jsonParser.getJSONFromUrl(sendURL,docparams);
        return json;
    }

    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }
}