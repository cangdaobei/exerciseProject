package com.zcr.exercise.async.asyncandtransactional;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 讨论使用Async和Transactional过程中AopContext.currentProxy()异常的问题
 */
@Service
public class AServiceImpl implements IAService {

    /**
     *  ------------------场景1  begin-----------------------------
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void funTemp1() {
        // 希望调用本类方法  但是它抛出异常，希望也能够回滚事务
        IAService b = IAService.class.cast(AopContext.currentProxy());
        System.out.println(b);
        b.fun1();
    }

    @Override
    public void fun1() {
        // ... 处理业务属于
        System.out.println(1 / 0);
    }




}
