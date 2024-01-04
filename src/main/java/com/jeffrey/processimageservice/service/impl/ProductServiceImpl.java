package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.entities.ProductInfo;
import com.jeffrey.processimageservice.mapper.ProductServiceImplMapper;
import com.jeffrey.processimageservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductServiceImplMapper productServiceImplMapper;

    @Autowired
    public ProductServiceImpl(ProductServiceImplMapper productServiceImplMapper) {
        this.productServiceImplMapper = productServiceImplMapper;
    }

    @Override
    public ProductInfo queryProductByProductPID(String pid) {
        return productServiceImplMapper.queryProductByProductPID(pid);
    }
}
