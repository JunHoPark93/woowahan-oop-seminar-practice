package org.eternity.food.domain.order;

import org.eternity.food.domain.shop.Shop;
import org.eternity.food.domain.shop.ShopRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;

    public OrderValidator(ShopRepository shopRepository, OrderRepository orderRepository) {
        this.shopRepository = shopRepository;
        this.orderRepository = orderRepository;
    }

    public void validateOrder(Order order) {
        Shop shop = shopRepository.findById(order.getShop().getId()).orElseThrow(RuntimeException::new);

        if (order.getOrderLineItems().isEmpty()) {
            throw new IllegalStateException("주문 항목이 비어 있습니다.");
        }

        if (!shop.isOpen()) {
            throw new IllegalArgumentException("가게가 영업중이 아닙니다.");
        }

        if (!shop.isValidOrderAmount(order.calculateTotalPrice())) {
            throw new IllegalStateException(String.format("최소 주문 금액 %s 이상을 주문해주세요.", shop.getMinOrderAmount()));
        }

        for (OrderLineItem orderLineItem : order.getOrderLineItems()) {
            orderLineItem.validate();
        }
    }
}
