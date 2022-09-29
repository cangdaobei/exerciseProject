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
 *  这里测试样例的前提是用的jdk动态代理，已在application中开启proxy-target-class: false
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

    /**
     * EnableAsync给容器注入的是AsyncAnnotationBeanPostProcessor，它用于给@Async生成代理，但是它仅仅是个BeanPostProcessor并不属于自动代理创建器，因此exposeProxy = true对它无效。
     * 所以AopContext.setCurrentProxy(proxy);这个set方法肯定就不会执行，so但凡只要业务方法中调用AopContext.currentProxy()方法就铁定抛异常~~
     */
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
    /**
     * AsyncAnnotationBeanPostProcessor在创建代理时有这样一个逻辑：若已经是Advised对象了，那就只需要把@Async的增强器添加进去即可。若不是代理对象才会自己去创建
     * AbstractAdvisingBeanPostProcessor 的postProcessAfterInitialization方法(bean instanceof Advised)
     * 自动代理创建器AbstractAutoProxyCreator它实际也是个BeanPostProcessor，所以它和上面处理器的执行顺序很重要~~~
     * 两者都继承自ProxyProcessorSupport所以都能创建代理，且实现了Ordered接口
     * 1. AsyncAnnotationBeanPostProcessor默认的order值为Ordered.LOWEST_PRECEDENCE。但可以通过@EnableAsync指定order属性来改变此值。 执行代码语句：bpp.setOrder(this.enableAsync.<Integer>getNumber("order"));
     * 2. AbstractAutoProxyCreator默认值也同上。但是在把自动代理创建器添加进容器的时候有这么一句代码：beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE); 自动代理创建器这个处理器是最高优先级
     * 由上可知因为标注有@Transactional，所以自动代理会生效，因此它会先交给AbstractAutoProxyCreator把代理对象生成好了，再交给后面的处理器执行
     * 由于AbstractAutoProxyCreator先执行，所以AsyncAnnotationBeanPostProcessor执行的时候此时Bean已经是代理对象了，由步骤1可知，此时它会沿用这个代理，只需要把切面添加进去即可~
     * 从上面步骤可知，加上了事务注解，最终代理对象是由自动代理创建器创建的，因此exposeProxy = true对它有效，这是解释它能正常work的最为根本的原因。
     */
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
    //同上
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
    //根本原因就是关键节点的执行时机问题。在执行代理对象funTemp方法的时候，绑定动作oldProxy = AopContext.setCurrentProxy(proxy);在前，目标方法执行（包括增强器的执行）invocation.proceed()在后。so其实在执行绑定的还是在主线程里而并非是新的异步线程，所以在你在方法体内（已经属于异步线程了）执行AopContext.currentProxy()那可不就报错了嘛~

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
