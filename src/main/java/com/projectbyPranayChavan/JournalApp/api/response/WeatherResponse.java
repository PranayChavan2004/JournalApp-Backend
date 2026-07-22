package com.projectbyPranayChavan.JournalApp.api.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter public class WeatherResponse {
    private Current current;

    @Getter
    @Setter
    public class Current {
        private int temperature;
        /* we are using JsonProperty because name of this field in json is weather_descriptions
         so now springboot knows what is value of weatherDescriptions*/
        @JsonProperty("weather_descriptions")
        private List<String> weatherDescriptions;
        private int feelslike;
    }


}
