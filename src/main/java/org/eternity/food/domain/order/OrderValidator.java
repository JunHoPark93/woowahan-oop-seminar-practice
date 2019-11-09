package org.eternity.food.domain.order;

import org.eternity.food.domain.shop.Menu;
import org.eternity.food.domain.shop.MenuRepository;
import org.eternity.food.domain.shop.Shop;
import org.eternity.food.domain.shop.ShopRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;

    public OrderValidator(ShopRepository shopRepository, OrderRepository orderRepository, MenuRepository menuRepository) {
        this.shopRepository = shopRepository;
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
    }

    // shop 검증, menu 가 유효한지 검증
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
            Menu menu = menuRepository.findById(orderLineItem.getMenuId()).orElseThrow(RuntimeException::new);
            menu.validateOrder(orderLineItem.getName(), orderLineItem.convertToOptionGroups());
        }
    }
}
