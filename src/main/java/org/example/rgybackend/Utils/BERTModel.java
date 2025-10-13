package org.example.rgybackend.Utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BERTModel {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${bert.url}")
    private String BASEURL;

    public ModelResponse checkEmotion(String text) {
        String url = BASEURL + "/api/check/emotion";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", text);
        ModelResponse response = restTemplate.postForObject(url, requestBody, ModelResponse.class);
        if(response == null) {
            throw new RuntimeException("调用API失败");
        }
        return response;
    }

    public ModelResponse checkCrisis(String text) {
        String url = BASEURL + "/api/check/crisis";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", text);
        ModelResponse response = restTemplate.postForObject(url, requestBody, ModelResponse.class);
        if(response == null) {
            throw new RuntimeException("调用API失败");
        }
        return response;
    }

    public Long justify(String text) {
        ModelResponse emotionResponse = this.checkEmotion(text);
        ModelResponse crisisResponse = this.checkCrisis(text);

        if(crisisResponse.getPredicted_class() == 1 && emotionResponse.getPredicted_class() == 2) {
            return 1L;
        }

        if(crisisResponse.getPredicted_class() == 2 && (crisisResponse.getConfidence() > 0.6 && crisisResponse.getConfidence() < 0.99)) {
            return 2L;
        }

        if(crisisResponse.getPredicted_class() == 2 && (crisisResponse.getConfidence() >= 0.99 && crisisResponse.getConfidence() < 0.999)) {
            return 3L;
        }

        if(crisisResponse.getPredicted_class() == 2 && crisisResponse.getConfidence() >= 0.999) {
            return 4L;
        }

        return 0L;
    }

}
