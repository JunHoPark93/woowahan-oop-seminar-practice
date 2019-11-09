package org.eternity.food.domain.delivery;

import org.eternity.food.domain.order.Order;
import org.eternity.food.domain.order.OrderDeliverService;
import org.eternity.food.domain.order.OrderPayService;
import org.eternity.food.domain.order.OrderRepository;
import org.eternity.food.domain.shop.Shop;
import org.eternity.food.domain.shop.ShopRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderPayServiceImpl implements OrderPayService, OrderDeliverService {
    private OrderRepository orderRepository;
    private DeliveryRepository deliveryRepository;
    private ShopRepository shopRepository;

    public OrderPayServiceImpl(OrderRepository orderRepository, DeliveryRepository deliveryRepository, ShopRepository shopRepository) {
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public void payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        order.payed();

        Delivery delivery = Delivery.started(order.getId());
        deliveryRepository.save(delivery);
    }

    @Override
    public void deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        order.delivered();

        Shop shop = shopRepository.findById(order.getShopId()).orElseThrow(IllegalArgumentException::new);
        shop.billCommissionFee(order.calculateTotalPrice());

        Delivery delivery = deliveryRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        delivery.complete();
    }
}