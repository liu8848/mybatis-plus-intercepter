package com.lqz.injector.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 自定义Mapper，添加批量插入接口
 */
public interface CustomMapper<T> extends BaseMapper<T> {

    /**
     * <p>
     *     批量插入，方法的名字是固定的不能随意设置
     *     命名来源InsertBatchSomeColumn.getMethod()
     * </p>
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);

    /**
     * <p>
     *     解决大数据插入InsertBatchSomeColumn报错问题。
     * </p>
     * @param entityList
     * @param batchSize
     */
    @Transactional(rollbackFor = {Exception.class})
    default void saveBatchsss(Collection<T> entityList, int batchSize) {
        int size = entityList.size();
        int idxLimit = Math.min(batchSize, size);
        int i = 1;
        //保存单批提交的数据集合
        List<T> oneBatchList = new ArrayList<>();
        for (Iterator<T> var7 = entityList.iterator(); var7.hasNext(); ++i) {
            T element = var7.next();
            oneBatchList.add(element);
            if (i == idxLimit) {
                insertBatchSomeColumn(oneBatchList);
                //每次提交后需要清空集合数据
                oneBatchList.clear();
                idxLimit = Math.min(idxLimit + batchSize, size);
            }
        }
    }

}
