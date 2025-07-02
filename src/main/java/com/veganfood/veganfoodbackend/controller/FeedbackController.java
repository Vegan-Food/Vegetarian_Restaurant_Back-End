package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.FeedbackDTO;
import com.veganfood.veganfoodbackend.service.FeedbackService; // ✅ Thêm import này
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity; // ✅ Nếu dùng ResponseEntity
import org.springframework.http.HttpStatus; // ✅ Nếu dùng HttpStatus
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getFeedbackByProduct(@PathVariable Integer productId) {
        try {
            System.out.println("Getting feedback for product ID: " + productId);
            List<FeedbackDTO> feedbacks = feedbackService.getFeedbackByProductId(productId);
            System.out.println("Found " + feedbacks.size() + " feedbacks");
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}