package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.FeedbackDTO;
import com.veganfood.veganfoodbackend.dto.CreateFeedbackDTO;
import com.veganfood.veganfoodbackend.model.Feedback;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.model.Product;
import com.veganfood.veganfoodbackend.repository.FeedbackRepository;
import com.veganfood.veganfoodbackend.repository.UserRepository;
import com.veganfood.veganfoodbackend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
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

    // ✅ Tạo feedback mới
    @Transactional
    public FeedbackDTO createFeedback(CreateFeedbackDTO createFeedbackDTO) {
        try {
            // Validate user exists
            User user = userRepository.findById(createFeedbackDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + createFeedbackDTO.getUserId()));

            // Validate product exists
            Product product = productRepository.findById(createFeedbackDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + createFeedbackDTO.getProductId()));

            // Create new feedback
            Feedback feedback = new Feedback();
            feedback.setUser(user);
            feedback.setProduct(product);
            feedback.setComment(createFeedbackDTO.getComment());
            feedback.setCreatedAt(LocalDateTime.now());

            // Save feedback
            Feedback savedFeedback = feedbackRepository.save(feedback);

            // Return DTO
            return new FeedbackDTO(
                    savedFeedback.getComment(),
                    savedFeedback.getUser().getName(),
                    savedFeedback.getCreatedAt()
            );
        } catch (Exception e) {
            System.out.println("Error creating feedback: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create feedback", e);
        }
    }

    // ✅ Lấy tất cả feedback
    public List<FeedbackDTO> getAllFeedback() {
        try {
            List<Feedback> feedbackList = feedbackRepository.findAll();
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
            System.out.println("Error getting all feedback: " + e.getMessage());
            throw new RuntimeException("Failed to get all feedback", e);
        }
    }

    @Transactional
    public void deleteFeedback(Integer feedbackId) {
        try {
            if (!feedbackRepository.existsById(feedbackId)) {
                throw new RuntimeException("Feedback not found with ID: " + feedbackId);
            }
            feedbackRepository.deleteById(feedbackId);
        } catch (Exception e) {
            System.out.println("Error deleting feedback: " + e.getMessage());
            throw new RuntimeException("Failed to delete feedback", e);
        }
    }
}