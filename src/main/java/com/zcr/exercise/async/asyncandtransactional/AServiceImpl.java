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
 * 在测试过程中如果1-5方法都放开，那就不会存在AopContext.currentProxy()的问题，
 *  原因是在项目启动初始化的时候，这几个方法中存在能够走到AopContext.setCurrentProxy(proxy)这步的代码，因此对当前类而言exposeProxy = true;
 *  所以在当前类中调用AopContext.currentProxy()都是可行的，但是如果将这些方法放在不同的类中的话，调用AopContext.currentProxy()就会异常了
 *  注:方法5不知何原因一直莫名的中断，待后续再测试
 */
@Service
public class AServiceImpl implements IAService {

    @Resource
    UserMapper userMapper;

//------------------场景1  begin-----------------------------
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void funTemp1() {
//        User user = new User().setUsername("张三").setAge(23);
//        userMapper.insert(user);
//        // 希望调用本类方法  但是它抛出异常，希望也能够回滚事务
//        IAService b = IAService.class.cast(AopContext.currentProxy());
//        System.out.println(b);
//        b.fun1();
//    }
//
//    @Override
//    public void fun1() {
//        // ... 处理业务属于
//        System.out.println(1 / 0);
//    }

//------------------场景2同类内方法调用，希望异步执行被调用的方法（希望@Async生效）  begin-----------------------------
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


//------------------场景3同类内方法调用，希望异步执行被调用的方法，并且在入口方法处使用事务  begin-----------------------------
  /*  @Transactional(rollbackFor = Exception.class)
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

    @Async
    @Override
    public void fun3() {
        System.out.println("fun3线程名称：" + Thread.currentThread().getName());
        System.out.println(1 / 0);
    }

//------------------场景4和示例三的唯一区别是把事务注解@Transactional标注在被调用的方法处（和@Async同方法）：  begin-----------------------------
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
*/
//------------------场景5把@Async标注在入口方法上：  begin-----------------------------
//    @Transactional(rollbackFor = Exception.class)
//    @Async
//    @Override
//    public void funTemp5() {
//        try {
//            User user = new User().setUsername("张三").setAge(23);
//            userMapper.insert(user);
//            // 希望调用本类方法  但是它抛出异常，希望也能够回滚事务
//            IAService b = IAService.class.cast(AopContext.currentProxy());
//            b.fun5();
//            System.out.println("funTemp5线程名称：" + Thread.currentThread().getName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void fun5() {
//        System.out.println("fun5线程名称：" + Thread.currentThread().getName());
//    }
}
