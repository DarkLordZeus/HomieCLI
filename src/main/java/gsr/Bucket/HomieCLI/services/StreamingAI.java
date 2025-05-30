package gsr.Bucket.HomieCLI.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gsr.Bucket.HomieCLI.config.AppConfig;
import okhttp3.*;
import okio.BufferedSource;

import java.io.IOException;

public class StreamingAI {

    private final String OPENAI_API_KEY;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public StreamingAI() {
        this.OPENAI_API_KEY = AppConfig.getApiKey();
    }

    public void askStream(String prompt) throws IOException {
        String jsonPayload = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [{"role": "user", "content": "%s"}],
              "stream": true
            }
            """.formatted(prompt.replace("\"", "\\\""));

        RequestBody body = RequestBody.create(
                jsonPayload,
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(body)
                .build();
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY environment variable is not set.");
            return; // or throw exception
        }

        try (Response response = client.newCall(request).execute()) {


            if (!response.isSuccessful()) {
                System.err.println("❌ Request failed: " + response);
                return;
            }

            BufferedSource source = response.body().source();

            while (!source.exhausted()) {
                String line = source.readUtf8LineStrict();

                if (line.startsWith("data: ")) {
                    String jsonStr = line.substring("data: ".length());

                    if ("[DONE]".equals(jsonStr.trim())) {
                        break;
                    }

                    JsonNode jsonNode = mapper.readTree(jsonStr);
                    JsonNode choices = jsonNode.get("choices");
                    if (choices != null && choices.isArray() && choices.size() > 0) {
                        JsonNode delta = choices.get(0).get("delta");
                        if (delta != null && delta.has("content")) {
                            String content = delta.get("content").asText();
                            System.out.print(content);
                            System.out.flush();
                        }
                    }
                }
            }
            System.out.println(); // Newline after done
        }
    }
}
