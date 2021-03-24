import service.ShopService;
import service.impl.VinylShopService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ShopService shopService = new VinylShopService();
        shopService.getDataProduct();
        System.out.println();
    }
}

