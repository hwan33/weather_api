package weatherAPI.weatherAPI.dto;

import lombok.Data;

@Data
public class WeatherDto {
	private String baseDate;
	private String baseTime;
	private String category;
	private String fcstDate;
	private String fcstTime;
	private String fcstValue;
	private String nx;
	private String ny;
}
