package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.ProductInfo;

public interface ProductService{
    ProductInfo queryProductByProductPID(String pid);
}
