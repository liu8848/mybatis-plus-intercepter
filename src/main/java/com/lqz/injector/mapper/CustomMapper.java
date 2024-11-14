package com.lqz.injector.mapper;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义Mapper，添加批量插入接口
 */
public interface CustomMapper<T> extends BaseMapper<T> {

    Integer insertBatchSomeColumn(Collection<T> entityList);

    Integer insertOrUpdateBatchByUK(Collection<T> entityList);

    Integer updateSomeColumnById(@Param("et") T entity);


    /**
    * @Description: 根据唯一约束插入或更新一条数据
    * @author: liu.qingzong
    * @Date: 2024/11/14 09:34
    */
    @Transactional(rollbackFor = Exception.class)
    default boolean insertOrUpdate(T entity) {
        if(entity == null) {
            return false;
        }
        Integer row = insertOrUpdateBatchByUK(Collections.singletonList(entity));
        return row != null && row > 0;
    }

    /**
    * @Description: 根据id更新数据部分字段内容
     * @param entity 更新的数据
     * @param fieldNames 更新的字段列表
    * @return: 更新结果
    * @author: liu.qingzong
    * @Date: 2024/11/14 10:13
    */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateEntityById(T entity, Collection<String> fieldNames) {
        if(CollectionUtils.isEmpty(fieldNames)) {
            throw new RuntimeException("更新的属性列表为空");
        }
        Class<?> aClass = entity.getClass();

        //筛选需要置空的字段
        List<Field> setNullFields = Arrays.stream(aClass.getDeclaredFields())
                .filter(f -> f.getAnnotation(TableId.class) != null || !(fieldNames.contains(f.getName())))     //筛选出主键字段与本次不需要更新的字段
                .collect(Collectors.toList());

        try{
            for(Field field : setNullFields) {
                field.setAccessible(true);
                field.set(entity,null);
            }
        }catch (IllegalAccessException |IllegalArgumentException e) {
            throw new RuntimeException("非法字段或私有字段无权更改",e);
        }

        return updateSomeColumnById(entity)>0;
    }


    @Transactional(rollbackFor = {Exception.class})
    default int saveBatchsss(Collection<T> entityList, int batchSize) {
        int row=0;
        int size = entityList.size();
        int idxLimit = Math.min(batchSize, size);
        int i = 1;
        //保存单批提交的数据集合
        List<T> oneBatchList = new ArrayList<>();
        for (Iterator<T> var7 = entityList.iterator(); var7.hasNext(); ++i) {
            T element = var7.next();
            oneBatchList.add(element);
            if (i == idxLimit) {
                row+=insertBatchSomeColumn(oneBatchList);
                //每次提交后需要清空集合数据
                oneBatchList.clear();
                idxLimit = Math.min(idxLimit + batchSize, size);
            }
        }
        return row;
    }

    @Transactional(rollbackFor = {Exception.class})
    default int saveOrUpdateBatchByUK(Collection<T> entityList, int batchSize) {
        int row=0;
        int size = entityList.size();
        int idxLimit = Math.min(batchSize, size);
        int i = 1;
        List<T> oneBatchList = new ArrayList<>();
        for (Iterator<T> var7 = entityList.iterator(); var7.hasNext(); ++i) {
            T element = var7.next();
            oneBatchList.add(element);
            if (i == idxLimit) {
                row+=insertOrUpdateBatchByUK(oneBatchList);
                oneBatchList.clear();
                idxLimit = Math.min(idxLimit + batchSize, size);
            }
        }
        return row;
    }
}
