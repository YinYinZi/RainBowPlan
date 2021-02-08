package org.rainbow.database.datasource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.rainbow.database.properties.DatabaseProperties;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 数据库 & 事务 & MyBatis & MyBatis Plus配置
 *
 * @author K
 * @date 2021/1/26  15:35
 */
@Slf4j
public abstract class
BaseDatabaseConfiguration implements InitializingBean {

    protected static final String[] DEV_PROFILES = new String[]{"dev"};

    @Value("spring.profiles.active:dev")
    protected String profiles;

    private static final List<Class<? extends Annotation>> AOP_POINTCUT_ANNOTATIONS = new ArrayList<>(2);

    static {
        // 事务在Controller层开启
        AOP_POINTCUT_ANNOTATIONS.add(RestController.class);
        AOP_POINTCUT_ANNOTATIONS.add(Controller.class);
    }

    protected final MybatisPlusProperties properties;
    protected final DatabaseProperties databaseProperties;
    private final Interceptor[] interceptors;
    private final TypeHandler[] typeHandlers;
    private final LanguageDriver[] languageDrivers;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;
    private final List<MybatisPlusPropertiesCustomizer> mybatisPlusPropertiesCustomizers;
    private final ApplicationContext applicationContext;

    public BaseDatabaseConfiguration(MybatisPlusProperties properties,
                                     DatabaseProperties databaseProperties,
                                     ObjectProvider<Interceptor[]> interceptorsProvider,
                                     ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                     ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                     ResourceLoader resourceLoader,
                                     ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                     ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                     ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider,
                                     ApplicationContext applicationContext) {
        this.properties = properties;
        this.databaseProperties = databaseProperties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();
        this.languageDrivers = languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.mybatisPlusPropertiesCustomizers = mybatisPlusPropertiesCustomizerProvider.getIfAvailable();
        this.applicationContext = applicationContext;
    }


    protected TransactionAttributeSource transactionAttributeSource() {
        // 当前存在事务就使用当前事务, 当前不存在事务就创建一个新的事务
        RuleBasedTransactionAttribute requireTx = new RuleBasedTransactionAttribute();
        requireTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Throwable.class)));
        requireTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requireTx.setTimeout(this.databaseProperties.getTxTimeout());
        HashMap<String, TransactionAttribute> txMap = new HashMap<>(this.databaseProperties.getTransactionAttributeList().size() + 5);
        this.databaseProperties.getTransactionAttributeList().forEach(key -> txMap.put(key, requireTx));

        RuleBasedTransactionAttribute readonlyTx = new RuleBasedTransactionAttribute();
        readonlyTx.setReadOnly(true);
        readonlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        // 除了上面的事务之外  其他的都走只读事务
        txMap.put("*", readonlyTx);
        NameMatchTransactionAttributeSource txTransactionAttributeSource = new NameMatchTransactionAttributeSource();
        txTransactionAttributeSource.setNameMap(txMap);
        return txTransactionAttributeSource;
    }

    protected Advisor txAdviceAdvisor(TransactionInterceptor ti) {
        return new DefaultPointcutAdvisor(new Pointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return (clazz) -> {
                    // 判断调用类是否以"org.rainbow"开头
                    if (!clazz.getName().startsWith(BaseDatabaseConfiguration.this.databaseProperties.getTransactionScanPackage())) {
                        return false;
                    }
                    for (Class<? extends Annotation> aop : AOP_POINTCUT_ANNOTATIONS) {
                        if (clazz.getAnnotation(aop) == null) {
                            continue;
                        }
                        log.debug("允许带事务的类为：{}", clazz);
                        return true;
                    }
                    return false;
                };
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return MethodMatcher.TRUE;
            }
        }, ti);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtil.isNotEmpty(mybatisPlusPropertiesCustomizers)) {
            this.mybatisPlusPropertiesCustomizers.forEach(e -> e.customize(this.properties));
        }
        checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(),
                    "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }
    }

    protected SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (ObjectUtil.isNotEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (Objects.nonNull(this.databaseIdProvider)) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (Objects.nonNull(this.properties.getTypeAliasesSuperType())) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (ObjectUtil.isNotEmpty(this.typeHandlers)) {
            factory.setTypeHandlers(this.typeHandlers);
        }
        if (ObjectUtil.isNotEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }
        Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
        if (ObjectUtil.isNotEmpty(this.languageDrivers)) {
            factory.setScriptingLanguageDrivers(languageDrivers);
        }
        Optional.ofNullable(defaultLanguageDriver).ifPresent(factory::setDefaultScriptingLanguageDriver);
        // 自定义枚举包
        if (StringUtils.hasText(this.properties.getTypeEnumsPackage())) {
            factory.setTypeEnumsPackage(this.properties.getTypeEnumsPackage());
        }

        GlobalConfig globalConfig = this.properties.getGlobalConfig();
        // 注入填充器
        if (this.applicationContext.getBeanNamesForType(MetaObjectHandler.class, false, false).length > 0) {
            MetaObjectHandler metaObjectHandler = this.applicationContext.getBean(MetaObjectHandler.class);
            globalConfig.setMetaObjectHandler(metaObjectHandler);
        }
        // 注入主键生成器
        if (this.applicationContext.getBeanNamesForType(IKeyGenerator.class, false, false).length > 0) {
            IKeyGenerator keyGenerator = this.applicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }
        // 注入Sql注入器
        if (this.applicationContext.getBeanNamesForType(ISqlInjector.class, false, false).length > 0) {
            ISqlInjector sqlInjector = this.applicationContext.getBean(ISqlInjector.class);
            globalConfig.setSqlInjector(sqlInjector);
        }
        // 设置globalConfig到 MybatisSqlSessionFactoryBean
        factory.setGlobalConfig(globalConfig);
        return factory.getObject();
    }

    private void applyConfiguration(MybatisSqlSessionFactoryBean factory) {
        MybatisConfiguration newConfiguration = this.properties.getConfiguration();
        MybatisConfiguration configuration = new MybatisConfiguration();
        BeanUtil.copyProperties(newConfiguration, configuration);

        if (!CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }
}
