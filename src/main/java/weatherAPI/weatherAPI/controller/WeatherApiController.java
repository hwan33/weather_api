package weatherAPI.weatherAPI.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import lombok.RequiredArgsConstructor;
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
	@Scheduled(cron = "0 0 2/3 * * *", zone = "Asia/Seoul")
	public void getVillageWeather() throws IOException, ParseException {

		String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		// TODO 인터넷에서 발급받은 인증키를 사용해주세요
		String serviceKey = "인터넷에서 발급받은 인증키를 사용해주세요";
		String pageNo = "1";
		String baseDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		String baseTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
		String numOfRows = "1000";
		String dataType = "JSON";
		String nx = "60";
		String ny = "120";

		String url = apiUrl
			+ "?serviceKey=" + UriUtils.encode(serviceKey, StandardCharsets.UTF_8)
			+ "&numOfRows=" + UriUtils.encode(numOfRows, StandardCharsets.UTF_8)
			+ "&pageNo=" + UriUtils.encode(pageNo, StandardCharsets.UTF_8)
			+ "&base_date=" + UriUtils.encode(baseDate, StandardCharsets.UTF_8)
			+ "&base_time=" + UriUtils.encode(baseTime, StandardCharsets.UTF_8)
			+ "&nx=" + UriUtils.encode(nx, StandardCharsets.UTF_8)
			+ "&ny=" + UriUtils.encode(ny, StandardCharsets.UTF_8)
			+ "&dataType=" + UriUtils.encode(dataType, StandardCharsets.UTF_8);

		String result = getStringFromURL(url);
		weatherService.upload(getWeatherList(result));
	}

	public String getStringFromURL(String url) throws IOException {
		URL apiURL = new URL(url);

		HttpURLConnection conn = (HttpURLConnection)apiURL.openConnection();
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
		JSONObject jsonObj = (JSONObject)jsonParser.parse(weathers);
		JSONObject parse_response = (JSONObject)jsonObj.get("response");
		JSONObject parse_body = (JSONObject)parse_response.get("body");
		JSONObject parse_items = (JSONObject)parse_body.get("items");
		JSONArray parse_item = (JSONArray)parse_items.get("item");

		JSONObject obj;

		List<Weather> dataList = new ArrayList<>();
		for (Object o : parse_item) {
			obj = (JSONObject)o;

			String category = (String)obj.get("category");
			if (!requireCategory.contains(category)) {
				continue;
			}
			String baseDate = (String)obj.get("baseDate");
			String baseTime = (String)obj.get("baseTime");
			String fcstDate = (String)obj.get("fcstDate");
			String fcstTime = (String)obj.get("fcstTime");
			String fcstValue = String.valueOf(obj.get("fcstValue"));
			String nx = String.valueOf(obj.get("nx"));
			String ny = String.valueOf(obj.get("ny"));

			dataList.add(
				Weather.create(baseDate, baseTime, category, fcstDate, fcstTime, fcstValue, nx,
					ny));
		}
		return dataList;
	}
}
