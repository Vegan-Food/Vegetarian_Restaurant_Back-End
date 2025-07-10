package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.OrderDTO;
import com.veganfood.veganfoodbackend.dto.OrderItemDTO;
import com.veganfood.veganfoodbackend.dto.request.CheckoutRequest;
import com.veganfood.veganfoodbackend.model.*;
import com.veganfood.veganfoodbackend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final DiscountRepository discountRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(UserRepository userRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        DiscountRepository discountRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.discountRepository = discountRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // ✅ API thanh toán (checkout)
    public String checkout(CheckoutRequest request, java.security.Principal principal) {
        String email = principal.getName();
        return checkoutByEmail(request, email);
    }

    public String checkoutByEmail(CheckoutRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        Cart cart = cartRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        if (cartItems.isEmpty()) return "Giỏ hàng trống";

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.pending);
        order.setPaymentMethod(Order.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
        order.setPhoneNumber(request.getPhoneNumber());
        order.setAddress(request.getAddress());

        // ✅ Áp dụng mã giảm giá nếu có
        if (request.getDiscountCode() != null && !request.getDiscountCode().isBlank()) {
            Discount discount = discountRepository.findByDiscountCode(request.getDiscountCode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá"));

            if (discount.getStatus() != Discount.Status.Available || discount.getQuantity() <= 0) {
                throw new RuntimeException("Mã giảm giá không còn khả dụng");
            }

            if (discount.getPercentage() > 0) {
                BigDecimal discountAmount = totalAmount
                        .multiply(BigDecimal.valueOf(discount.getPercentage()))
                        .divide(BigDecimal.valueOf(100));
                totalAmount = totalAmount.subtract(discountAmount);
            }

            discount.setQuantity(discount.getQuantity() - 1);
            if (discount.getQuantity() == 0) {
                discount.setStatus(Discount.Status.Unavailable);
            }

            discountRepository.save(discount);
            order.setDiscount(discount);
        }


        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setUser(user);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtTime(BigDecimal.valueOf(item.getProduct().getPrice()));
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAll(cartItems);

        return "✅ Đặt hàng thành công. Mã đơn hàng: " + order.getOrderId();
    }

    public List<Order> getRawOrdersForUserOrAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String role = user.getRole().name();

        if ("customer".equalsIgnoreCase(role)) {
            return orderRepository.findByUser(user);
        } else {
            return orderRepository.findAll();
        }
    }

    public List<OrderDTO> getOrdersForUserOrAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<Order> orders;
        if ("customer".equalsIgnoreCase(user.getRole().name())) {
            orders = orderRepository.findByUser(user);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setUserName(order.getUser().getName());
            dto.setStatus(order.getStatus().name());
            dto.setPaymentMethod(order.getPaymentMethod().name());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPhoneNumber(order.getPhoneNumber());
            dto.setAddress(order.getAddress());
            dto.setCreatedAt(order.getCreatedAt());

            List<OrderItemDTO> items = order.getOrderItems().stream().map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setProductName(item.getProduct().getName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPriceAtTime(item.getPriceAtTime());
                return itemDTO;
            }).collect(Collectors.toList());

            dto.setItems(items);
            return dto;
        }).collect(Collectors.toList());
    }

    public String deleteOrderById(Integer orderId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        boolean isOwner = order.getUser().getUserId().equals(user.getUserId());
        boolean isAdmin = !user.getRole().name().equalsIgnoreCase("customer");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền xóa đơn hàng này");
        }

        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);
        orderItemRepository.deleteAll(items);
        orderRepository.delete(order);

        return "✅ Đã xóa đơn hàng ID: " + orderId;
    }

    public String updateOrderStatus(Integer orderId, Order.OrderStatus status, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        boolean isCustomer = user.getRole().name().equalsIgnoreCase("customer");
        boolean isOwner = order.getUser().getUserId().equals(user.getUserId());

        if (isCustomer && !isOwner) {
            throw new RuntimeException("❌ Bạn không có quyền cập nhật đơn hàng này");
        }

        order.setStatus(status);
        orderRepository.save(order);

        return "✅ Cập nhật trạng thái đơn hàng thành công: " + status.name();
    }

    public OrderDTO getOrderByIdAndEmail(Integer orderId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Order order = orderRepository.findById(orderId)
                .orElse(null);

        if (order == null) return null;

        boolean isOwner = order.getUser().getUserId().equals(user.getUserId());
        boolean isAdmin = !user.getRole().name().equalsIgnoreCase("customer");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền truy cập đơn hàng này");
        }

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserName(order.getUser().getName());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentMethod(order.getPaymentMethod().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setAddress(order.getAddress());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemDTO> items = order.getOrderItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtTime(item.getPriceAtTime());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

}
