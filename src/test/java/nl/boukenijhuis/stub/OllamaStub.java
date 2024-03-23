package nl.boukenijhuis.stub;


import com.github.tomakehurst.wiremock.WireMockServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class OllamaStub {

    public static void main(String[] args) throws IOException {

        WireMockServer server = new WireMockServer(8080);
        server.start();

        stubFor(post(urlEqualTo("/api/generate")).willReturn(aResponse()
                .withBody(readFile("stub/ollama/stub_with_spring_boot_endpoint.json"))));
    }

    /**
     * Reads the contents of a file and returns it as a string.
     *
     * @param fileName the name of the file to read (give path from resources/)
     * @return the contents of the file as a string
     * @throws IOException if an I/O error occurs
     */
    private static String readFile(String fileName) throws IOException {
        try (var in = OllamaStub.class.getResourceAsStream("/" + fileName)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (NullPointerException e) {
            throw new RuntimeException(String.format("The file '%s' cannot be found in the resources directory.", fileName));
        }
    }

}
