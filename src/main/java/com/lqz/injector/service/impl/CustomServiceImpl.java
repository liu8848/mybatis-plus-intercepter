package com.lqz.injector.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lqz.injector.mapper.CustomMapper;
import com.lqz.injector.service.CustomIService;

import java.util.Collection;
import java.util.List;

/**
 * @ClassName CustomServiceImpl
 * @Description TODO
 * @Author liu.qingzong
 * @Date 2024/11/14 10:26
 */
public class CustomServiceImpl<M extends CustomMapper<T>,T> extends ServiceImpl<M,T> implements CustomIService<T> {
    @Override
    public boolean insertOrUpdate(T entity) {
        return baseMapper.insertOrUpdate(entity);
    }

    @Override
    public boolean updateEntityById(T entity, Collection<String> fieldNames) {
        return baseMapper.updateEntityById(entity, fieldNames);
    }

    @Override
    public int insertBatch(Collection<T> entities) {
        return baseMapper.saveBatchsss(entities,1000);
    }

    @Override
    public int insertBatch(Collection<T> entities,int batchSize) {
        return baseMapper.saveBatchsss(entities,batchSize);
    }

    @Override
    public int insertOrUpdateBatch(Collection<T> entities) {
        return baseMapper.saveOrUpdateBatchByUK(entities,1000);
    }

    @Override
    public int insertOrUpdateBatch(Collection<T> entities, int batchSize) {
        return baseMapper.saveOrUpdateBatchByUK(entities,batchSize);
    }
}
