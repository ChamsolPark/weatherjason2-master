package com.example.weather10;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.WorkSource;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class WeatherData{

    private String weather = "", tmperature = "";

    public String lookUpWeather(String baseDate, String time, String nx, String ny) throws IOException, JSONException {

//      String baseDate = date;//"20210531";//date	//조회하고싶은 날짜
        String baseTime = timeChange(time); //"0500";//timeChange(time);	//조회하고싶은 시간
        String type = "json";    //조회하고싶은 시간

        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";
//         홈페이지에서 받은 키
        String serviceKey = "8bBHdSOqe1GzRlJ%2Be6VPGq%2Bp2Q6hcS21Y8dcZwfd6dKrhbVApvbfzgpubXrCI81GqupGRtbuUdK6%2BOy6vt8MiA%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));    /* 타입 */

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());
        //어떻게 넘어가는지 확인하고 싶으면 아래 출력분 주석 해제
        //System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();


        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        Log.i("ITEMS", items);

        // items로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            if (category.equals("SKY")) {

                if (fcstValue.equals("1")) {
                    weather = "맑음";
                } else if (fcstValue.equals("2")) {
                    weather = "비";
                } else if (fcstValue.equals("3")) {
                    weather = "구름";
                } else if (fcstValue.equals("4")) {
                    weather = "흐림";
                }
            }

            if (category.equals("T3H") || category.equals("T1H")) {
                tmperature = fcstValue;
            }

            Log.i("날씨", fcstValue);
            Log.i("카테고리", category);

            Log.i("", weather + tmperature);
        }

        return weather + tmperature;
    }

    public String timeChange(String time)
    {
        // 현재 시간에 따라 데이터 시간 설정(3시간 마다 업데이트) //
        /**
         시간은 3시간 단위로 조회해야 한다. 안그러면 정보가 없다고 뜬다.
         0200, 0500, 0800 ~ 2300까지
         그래서 시간을 입력했을때 switch문으로 조회 가능한 시간대로 변경해주었다.
         **/
        switch(time) {

            case "0200":
            case "0300":
            case "0400":
                time = "0200";
                break;
            case "0500":
            case "0600":
            case "0700":
                time = "0500";
                break;
            case "0800":
            case "0900":
            case "1000":
                time = "0800";
                break;
            case "1100":
            case "1200":
            case "1300":
                time = "1100";
                break;
            case "1400":
            case "1500":
            case "1600":
                time = "1400";
                break;
            case "1700":
            case "1800":
            case "1900":
                time = "1700";
                break;
            case "2000":
            case "2100":
            case "2200":
                time = "2000";
                break;
            default:
                time = "2300";

        }
        return time;
    }

}