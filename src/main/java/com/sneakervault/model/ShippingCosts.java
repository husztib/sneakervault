package com.sneakervault.model;

import java.util.Map;

public class ShippingCosts {

    private ShippingCosts() {}

    public static final Map<ShippingMethod, Integer> HUF = Map.of(
            ShippingMethod.MAGYAR_POSTA, 1490,
            ShippingMethod.GLS, 1990,
            ShippingMethod.DPD, 1990,
            ShippingMethod.CSOMAGPONT, 990
    );

    public static final Map<ShippingMethod, Integer> EUR = Map.of(
            ShippingMethod.MAGYAR_POSTA, 4,
            ShippingMethod.GLS, 5,
            ShippingMethod.DPD, 5,
            ShippingMethod.CSOMAGPONT, 3
    );

    public static int getCostHUF(ShippingMethod method) {
        return HUF.getOrDefault(method, 0);
    }

    public static int getCostEUR(ShippingMethod method) {
        return EUR.getOrDefault(method, 0);
    }
}
