package com.zcr.exercise.async.asyncandtransactional;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(exposeProxy = true) // 暴露当前代理对象到当前线程绑定
public class RootConfig {
}
