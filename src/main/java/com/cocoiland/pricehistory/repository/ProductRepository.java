package com.cocoiland.pricehistory.repository;


import com.cocoiland.pricehistory.entity.ProductDetails;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends ElasticsearchRepository<ProductDetails, String> {
    Optional<ProductDetails> findById(String id);

    List<ProductDetails> findByName(String name);
}
