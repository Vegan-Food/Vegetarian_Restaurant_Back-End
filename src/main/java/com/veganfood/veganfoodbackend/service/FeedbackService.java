package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.FeedbackDTO;
import com.veganfood.veganfoodbackend.model.Feedback;
import com.veganfood.veganfoodbackend.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public List<FeedbackDTO> getFeedbackByProductId(Integer productId) {
        try {
            System.out.println("Fetching feedback for product ID: " + productId);
            List<Feedback> feedbackList = feedbackRepository.findByProductId(productId);
            System.out.println("Found " + feedbackList.size() + " feedback records");

            return feedbackList.stream()
                    .map(f -> {
                        String userName = "Anonymous";
                        try {
                            if (f.getUser() != null && f.getUser().getName() != null) {
                                userName = f.getUser().getName();
                            }
                        } catch (Exception e) {
                            System.out.println("Error getting user name: " + e.getMessage());
                        }

                        return new FeedbackDTO(
                                f.getComment(),
                                userName,
                                f.getCreatedAt()
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error in getFeedbackByProductId: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get feedback for product " + productId, e);
        }
    }
}