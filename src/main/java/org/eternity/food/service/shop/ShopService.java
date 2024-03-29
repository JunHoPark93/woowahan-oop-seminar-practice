package org.eternity.food.service.shop;

import org.eternity.food.domain.shop.Menu;
import org.eternity.food.domain.shop.MenuRepository;
import org.eternity.food.domain.shop.Shop;
import org.eternity.food.domain.shop.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShopService {
    private ShopRepository shopRepository;
    private MenuRepository menuRepository;

    public ShopService(ShopRepository shopRepository, MenuRepository menuRepository) {
        this.shopRepository = shopRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public MenuBoard getMenuBoard(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(IllegalArgumentException::new);
        List<Menu> menus = menuRepository.findByShopId(shop.getId());
        return new MenuBoard(shop, menus);
    }
}
