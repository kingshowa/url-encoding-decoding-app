package com.example;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;

@Controller
public class UrlEncoderController {

    private final Base32 base32 = new Base32();

    @GetMapping("/")
    public String index() {
        return "index"; // Returns the index.html view
    }

    @PostMapping("/process")
    public String process(@RequestParam String url, @RequestParam String action, Model model) {
        String result;
        if ("encode".equals(action)) {
            result = encodeUrl(url);
        } else {
            result = decodeUrl(url);
        }
        model.addAttribute("original", url);
        model.addAttribute("result", result);
        return "index"; // Show the same form with results
    }

    private String encodeUrl(String url) {
        byte[] encodedBytes = base32.encode(url.getBytes(StandardCharsets.UTF_8));
        return "www." + new String(encodedBytes, StandardCharsets.UTF_8).replace("=", "").toLowerCase() + ".com";
    }

    private String decodeUrl(String secretUrl) {
        String encodedPart = secretUrl.substring(4, secretUrl.length() - 4); // Remove "www." and ".com"
        byte[] decodedBytes = base32.decode(encodedPart.toUpperCase());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
