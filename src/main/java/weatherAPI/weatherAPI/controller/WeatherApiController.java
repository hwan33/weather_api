package weatherAPI.weatherAPI.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import weatherAPI.weatherAPI.entity.Weather;
import weatherAPI.weatherAPI.service.WeatherService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherApiController {

    private final WeatherService weatherService;
    private static final List<String> requireCategory = Arrays.asList("POP", "PTY", "PCP",
        "REH", "SNO", "SKY", "TMP", "TMN", "TMX");

    @GetMapping("/weather")
    public void getVillageWeather() throws IOException, ParseException {
        DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);

        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

        // TODO 인터넷에서 할당 받은 인증키를 사용해주세요
        String serviceKey = "인터넷에서 할당 받은 인증키를 사용해주세요";
        String pageNo = "1";
        String numOfRows = "225";
        String dataType = "JSON";
        String base_time = "0500";
        String nx = "60";
        String ny = "120";

        String urlBuilder =
            apiUrl + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey
                + "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows,
                "UTF-8")
                + "&" + URLEncoder.encode("pageNo", "utf-8") + "=" + URLEncoder.encode(pageNo,
                "utf-8")
                + "&" + URLEncoder.encode("base_date", "utf-8") + "=" + URLEncoder.encode(tempDate,
                "utf-8")
                + "&" + URLEncoder.encode("base_time", "utf-8") + "=" + URLEncoder.encode(base_time,
                "utf-8")
                + "&" + URLEncoder.encode("nx", "utf-8") + "=" + URLEncoder.encode(nx, "utf-8")
                + "&" + URLEncoder.encode("ny", "utf-8") + "="
                + URLEncoder.encode(ny, "utf-8")
                + "&" + URLEncoder.encode("dataType", "utf-8") + "=" + URLEncoder.encode(dataType,
                "utf-8");

        String result = getDataFromJson(urlBuilder, "UTF-8", "get", "");
        weatherService.upload(getWeatherList(result));
    }

    public String getDataFromJson(String url, String encoding, String type, String jsonStr)
        throws IOException {
        boolean isPost = false;

        if ("post".equals(type)) {
            isPost = true;
        } else {
            url = "".equals(jsonStr) ? url : url + "?request=" + jsonStr;
        }

        return getStringFromURL(url, encoding, isPost, jsonStr, "application/json");
    }

    public String getStringFromURL(String url, String encoding, boolean isPost, String parameter,
        String contentType) throws IOException {
        URL apiURL = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) apiURL.openConnection();
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
        return sb.toString();
    }

    private List<Weather> getWeatherList(String weathers) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(weathers);
        JSONObject parse_response = (JSONObject) jsonObj.get("response");
        JSONObject parse_body = (JSONObject) parse_response.get("body");
        JSONObject parse_items = (JSONObject) parse_body.get("items");
        JSONArray parse_item = (JSONArray) parse_items.get("item");

        JSONObject obj;

        List<Weather> dataList = new ArrayList<>();
        for (Object o : parse_item) {
            obj = (JSONObject) o;

            String category = (String) obj.get("category");
            if (!requireCategory.contains(category)) {
                continue;
            }
            String baseDate = (String) obj.get("baseDate");
            String baseTime = (String) obj.get("baseTime");
            String fcstDate = (String) obj.get("fcstDate");
            String fcstTime = (String) obj.get("fcstTime");
            String fcstValue = String.valueOf(obj.get("fcstValue"));
            String nx = String.valueOf(obj.get("nx"));
            String ny = String.valueOf(obj.get("ny"));

            dataList.add(Weather.create(baseDate, baseTime, category, fcstDate, fcstTime, fcstValue, nx, ny));
        }
        return dataList;
    }
}
