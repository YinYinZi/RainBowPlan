### rainbow-websocket-starter 项目解析

#### 程序入口类：ServerEndpointExporter

`ServerEndpointExporter` 继承了 `ApplicationObjectSupport` 实现了 `SmartInitializingSingleton` 和 `BeanFactoryAware`

❗ 继承抽象类 `ApplicationObjectSupport` 会在Spring初始化的时候通过该抽象类的setApplicationContext(ApplicationContext ctx)
将ApplicationContext进行注入 并提供了getApplicationContext()方便获取ApplicationContext

❗ 实现了接口 `SmartInitializingSingleton` 会在所有bean都初始化之后 容器会回调该接口的方法 `afterSingletonInstantiated()`

❗ 实现了接口 `BeanFactoryAware` 会将 `BeanFactory` 对象注入进来

具体执行过程：
* 