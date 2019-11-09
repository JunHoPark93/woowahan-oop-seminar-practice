package org.eternity.food.service.order;

import org.eternity.food.domain.delivery.Delivery;
import org.eternity.food.domain.delivery.DeliveryRepository;
import org.eternity.food.domain.order.Order;
import org.eternity.food.domain.order.OrderRepository;
import org.eternity.food.domain.order.OrderValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private DeliveryRepository deliveryRepository;
    private OrderMapper orderMapper;
    private OrderValidator orderValidator;

    public OrderService(OrderRepository orderRepository, DeliveryRepository deliveryRepository, OrderMapper orderMapper, OrderValidator orderValidator) {
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
        this.orderMapper = orderMapper;
        this.orderValidator = orderValidator;
    }

    @Transactional
    public void placeOrder(Cart cart) {
        Order order = orderMapper.mapFrom(cart);
        order.place(orderValidator);
        orderRepository.save(order);
    }

    @Transactional
    public void payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        order.payed();

        Delivery delivery = Delivery.started(order.getId());
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        order.delivered();

        Delivery delivery = deliveryRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        delivery.complete();
    }
}
