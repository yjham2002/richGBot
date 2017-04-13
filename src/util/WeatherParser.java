package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
/**
 * Created by a on 2017-04-13.
 */
public class WeatherParser{

    private static String URL = "http://www.weatheri.co.kr/forecast/forecast01.php?rid=1301040101&k=10&a_name=";
    private static String geocode = "서울";
    private static String IURL = "http://cse.dongguk.edu";
    private static String weatherInfo = "";
    private static final int THRESHOLD = 233;
    private static boolean isFailed = false;

    public static String getImageUrl(){
        return IURL;
    }

    public static String getWeatherInfo(){ return weatherInfo; }

    public WeatherParser(){}

    public static String getWeather(final String geocode){
        switch (geocode){
            default:
                URL = "http://www.weatheri.co.kr/forecast/forecast01.php?rid=0101010000&k=1&a_name=%BC%AD%BF%EF";
                break;
        }
        try {
            Document document = Jsoup.connect(URL).get();
            Element weather = document.select("tr>td").get(THRESHOLD);
            Element imageUrl = document.select("tbody>tr>td[rowspan=4]>img").first();
            IURL = imageUrl.attr("src");
            IURL = "http://www.weatheri.co.kr" + IURL.substring(2, IURL.length());
            weatherInfo = weather.text().toString();
        }catch (IOException e){
            weatherInfo = "날씨 정보를 불러오는 중 오류가 발생했습니다";
            isFailed = true;
        }
        return weatherInfo;
    }

}
