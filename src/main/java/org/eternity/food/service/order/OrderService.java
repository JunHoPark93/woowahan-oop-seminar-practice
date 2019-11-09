package org.eternity.food.service.order;

import org.eternity.food.domain.order.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;
    private OrderValidator orderValidator;
    private OrderPayService orderPayService;
    private OrderDeliverService orderDeliverService;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, OrderValidator orderValidator, OrderPayService orderPayService, OrderDeliverService orderDeliverService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderValidator = orderValidator;
        this.orderPayService = orderPayService;
        this.orderDeliverService = orderDeliverService;
    }

    @Transactional
    public void placeOrder(Cart cart) {
        Order order = orderMapper.mapFrom(cart);
        order.place(orderValidator);
        orderRepository.save(order);
    }

    @Transactional
    public void payOrder(Long orderId) {
        orderPayService.payOrder(orderId);
    }

    @Transactional
    public void deliverOrder(Long orderId) {
        orderDeliverService.deliverOrder(orderId);
    }
}
