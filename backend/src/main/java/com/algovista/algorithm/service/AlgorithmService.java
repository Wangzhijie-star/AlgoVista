package com.algovista.algorithm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.algovista.algorithm.entity.Algorithm;
import com.algovista.algorithm.entity.AlgorithmCategory;
import com.algovista.algorithm.mapper.AlgorithmCategoryMapper;
import com.algovista.algorithm.mapper.AlgorithmMapper;
import com.algovista.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlgorithmService {
    private final AlgorithmCategoryMapper categoryMapper;
    private final AlgorithmMapper algorithmMapper;

    public AlgorithmService(AlgorithmCategoryMapper categoryMapper, AlgorithmMapper algorithmMapper) {
        this.categoryMapper = categoryMapper;
        this.algorithmMapper = algorithmMapper;
    }

    public List<AlgorithmCategory> listCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<AlgorithmCategory>()
                .orderByAsc(AlgorithmCategory::getSortOrder));
    }

    public List<Algorithm> listAlgorithms(Long categoryId) {
        LambdaQueryWrapper<Algorithm> wrapper = new LambdaQueryWrapper<Algorithm>()
                .eq(Algorithm::getEnabled, true)
                .orderByAsc(Algorithm::getCategoryId)
                .orderByAsc(Algorithm::getId);
        if (categoryId != null) {
            wrapper.eq(Algorithm::getCategoryId, categoryId);
        }
        return algorithmMapper.selectList(wrapper);
    }

    public Algorithm getAlgorithm(Long id) {
        Algorithm algorithm = algorithmMapper.selectById(id);
        if (algorithm == null || !Boolean.TRUE.equals(algorithm.getEnabled())) {
            throw new BusinessException(404, "算法不存在");
        }
        return algorithm;
    }
}
