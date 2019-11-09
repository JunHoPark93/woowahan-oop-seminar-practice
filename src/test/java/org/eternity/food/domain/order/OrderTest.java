package org.eternity.food.domain.order;

import org.eternity.food.domain.generic.money.Money;
import org.eternity.food.domain.generic.money.Ratio;
import org.eternity.food.domain.shop.Shop;
import org.junit.Test;

import java.util.Arrays;

import static org.eternity.food.domain.Fixtures.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrderTest {
    @Test
    public void 결제완료() {
        Order order = anOrder().status(Order.OrderStatus.ORDERED).build();

        order.payed();

        assertThat(order.getOrderStatus(), is(Order.OrderStatus.PAYED));
    }


    @Test
    public void 배송완료() {
        Order order = anOrder()
                        .shopId(aShop().build().getId())
                        .status(Order.OrderStatus.PAYED)
                        .items(Arrays.asList(
                            anOrderLineItem()
                                .count(1)
                                .groups(Arrays.asList(
                                    anOrderOptionGroup()
                                        .options(Arrays.asList(anOrderOption().price(Money.wons(10000)).build())).build()))
                                .build()))
                        .build();

        order.delivered();

        assertThat(order.getOrderStatus(), is(Order.OrderStatus.DELIVERED));
    }
}
