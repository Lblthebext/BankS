package com.sanxiang.deposit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sanxiang.deposit.entity.DepositProduct;
import com.sanxiang.deposit.mapper.DepositProductMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final DepositProductMapper productMapper;

    public ProductController(DepositProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @GetMapping
    public ResponseEntity<List<DepositProduct>> list() {
        return ResponseEntity.ok(productMapper.selectList(new LambdaQueryWrapper<DepositProduct>().eq(DepositProduct::getStatus, 1)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepositProduct> detail(@PathVariable Long id) {
        return ResponseEntity.ok(productMapper.selectById(id));
    }
}
