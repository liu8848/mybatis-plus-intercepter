package com.lqz.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.lqz.injector.interceptor.InsertBatchSomeColumn;
import com.lqz.injector.interceptor.InsertOrUpdateBatchByUK;
import com.lqz.injector.interceptor.UpdateSomeColumnById;

import java.util.List;

//将批量插入的方法添加method
public class CustomSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        // 获取父类SQL注入方法列表
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        // 将批量插入方法添加进去，这个方法在下方会重写
        methodList.add(new InsertBatchSomeColumn());
        methodList.add(new InsertOrUpdateBatchByUK());
        methodList.add(new UpdateSomeColumnById());
        return methodList;
    }
}