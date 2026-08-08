package com.HerWell.example.Controller;

import com.HerWell.example.Service.ProductService;
import com.HerWell.example.Service.ProductService.ProductSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // POST: Fetch dynamic products by Symptoms or Search Query
    @PostMapping(value = "/ai-fetch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAiProducts(@RequestBody ProductSearchRequest request) {
        List<String> symptoms = request.getSymptoms() != null ? request.getSymptoms() : List.of();
        String jsonResult = productService.generateDynamicProducts(symptoms, request.getCustomQuery());
        return ResponseEntity.ok(jsonResult);
    }
}