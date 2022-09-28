package com.zcr.exercise.async.asyncandtransactional;

import com.zcr.exercise.entity.User;
import com.zcr.exercise.mapper.UserMapper;
import lombok.SneakyThrows;
import org.springframework.aop.framework.AopContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 讨论使用Async和Transactional过程中AopContext.currentProxy()异常的问题
 */
@Service
public class AServiceImpl implements IAService {

    @Resource
    UserMapper userMapper;

//------------------场景1  begin-----------------------------
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void funTemp1() {
        User user = new User().setUsername("张三").setAge(23);
        userMapper.insert(user);
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

//------------------场景2  begin-----------------------------
    @Override
    public void funTemp2() {
        User user = new User().setUsername("张三").setAge(23);
        userMapper.insert(user);
        // 希望调用本类方法  但是它抛出异常，希望也能够回滚事务
        IAService b = IAService.class.cast(AopContext.currentProxy());
        System.out.println(b);
        b.fun2();
        System.out.println("funTemp2线程名称：" + Thread.currentThread().getName());
    }

    @Async
    @Override
    public void fun2() {
        System.out.println("fun2线程名称：" + Thread.currentThread().getName());
    }


//------------------场景3  begin-----------------------------
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void funTemp3() {

        User user = new User().setUsername("张三").setAge(23);
        userMapper.insert(user);
        // 希望调用本类方法  但是它抛出异常，希望也能够回滚事务
        IAService b = IAService.class.cast(AopContext.currentProxy());
        System.out.println(b);
        b.fun3();
        System.out.println("funTemp3线程名称：" + Thread.currentThread().getName());
    }

    @SneakyThrows
    @Async
    @Override
    public void fun3() {
        System.out.println("fun3线程名称：" + Thread.currentThread().getName());
    }

//------------------场景4  begin-----------------------------
    @Override
    public void funTemp4() {
        User user = new User().setUsername("张三").setAge(23);
        userMapper.insert(user);
        // 希望调用本类方法  但是它抛出异常，希望也能够回滚事务
        IAService b = IAService.class.cast(AopContext.currentProxy());
        System.out.println(b);
        b.fun4();
        System.out.println("funTemp4线程名称：" + Thread.currentThread().getName());
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    @Async
    @Override
    public void fun4() {
        System.out.println("fun4(线程名称：" + Thread.currentThread().getName());
    }

//------------------场景5  begin-----------------------------
    @Transactional(rollbackFor = Exception.class)
    @Async
    @Override
    public void funTemp5() {
        try {
            User user = new User().setUsername("张三").setAge(23);
            userMapper.insert(user);
            // 希望调用本类方法  但是它抛出异常，希望也能够回滚事务
            IAService b = IAService.class.cast(AopContext.currentProxy());
            b.fun5();
            System.out.println("funTemp5线程名称：" + Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void fun5() {
        System.out.println("fun5线程名称：" + Thread.currentThread().getName());
    }
}
