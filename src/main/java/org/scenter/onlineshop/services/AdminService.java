package org.scenter.onlineshop.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AdminService {

    private StockService stockService;
    private ShopService shopService;
    private UserDetailsServiceImpl userService;

}
