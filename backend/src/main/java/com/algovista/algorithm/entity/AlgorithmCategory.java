package com.algovista.algorithm.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("algorithm_category")
public class AlgorithmCategory {
    private Long id;
    private String name;
    private Integer sortOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
