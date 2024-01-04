package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.ProductInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductServiceImplMapper {
    ProductInfo queryProductByProductPID(String pid);
}
