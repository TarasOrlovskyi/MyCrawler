package service;

import entity.Vinyl;

import java.io.IOException;
import java.util.List;

public interface ShopService {
    List<Vinyl> getDataProduct() throws IOException;
}
