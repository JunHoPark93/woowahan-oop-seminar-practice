package org.eternity.food.domain.order;

import org.eternity.food.domain.shop.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public void validate(Order order) {
        validate(order, getShop(order), getMenus(order));
    }

    private Map<Long, Menu> getMenus(Order order) {
        return menuRepository.findAllById(order.getMenuIds())
                .stream().collect(Collectors.toMap(Menu::getId, Function.identity()));
    }

    private Shop getShop(Order order) {
        return shopRepository.findById(order.getShopId()).orElseThrow(RuntimeException::new);
    }

    private void validate(Order order, Shop shop, Map<Long, Menu> menus) {
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
            validateOrderLineItem(orderLineItem, menus.get(orderLineItem.getMenuId()));
        }
    }

    private void validateOrderLineItem(OrderLineItem orderLineItem, Menu menu) {
        if (!orderLineItem.getName().equals(menu.getName())) {
            throw new IllegalArgumentException("기본 상품이 변경되었습니다");
        }

        for (OrderOptionGroup group : orderLineItem.getGroups()) {
            validateOrderOptionGroup(group, menu);
        }
    }

    private void validateOrderOptionGroup(OrderOptionGroup group, Menu menu) {
        for (OptionGroupSpecification optionGroupSpec : menu.getOptionGroupSpecs()) {
            if (optionGroupSpec.isSatisfiedBy(group.convertToOptionGroup())) {
                return;
            }
        }
        throw new IllegalArgumentException("메뉴가 변경되었습니다");
    }
}
