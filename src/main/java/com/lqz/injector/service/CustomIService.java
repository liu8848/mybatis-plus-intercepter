package com.lqz.injector.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

public interface CustomIService<T> extends IService<T> {

    boolean insertOrUpdate(T entity);

    boolean updateEntityById(T entity, Collection<String> fieldNames);

    int insertBatch(Collection<T> entities);

    int insertBatch(Collection<T> entities,int batchSize);

    int insertOrUpdateBatch(Collection<T> entities);

    int insertOrUpdateBatch(Collection<T> entities,int batchSize);
}
