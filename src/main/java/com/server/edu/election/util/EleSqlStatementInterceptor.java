package com.server.edu.election.util;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.server.edu.common.rest.RedisConstant;
import com.server.edu.session.util.SessionUtils;  

@SuppressWarnings("all")
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,Object.class}),
//    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
//    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
	})
@Component
public class EleSqlStatementInterceptor implements Interceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(EleSqlStatementInterceptor.class);

	private Properties properties;
	
	@Autowired
    private RedisTemplate redisTemplate;

    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        Object returnValue = null;
        long start = System.currentTimeMillis();
        returnValue = invocation.proceed();
        long end = System.currentTimeMillis();
        long time = (end - start);
        if (time > 1) {
        	String operator = "SYSTEM";
        	// 获取不到操作账户
//            if (StringUtils.isNotBlank(SessionUtils.getCurrentSession().realName())) {
//            	operator = SessionUtils.getCurrentSession().realName();
//            } else {
//            	operator = "SYSTEM";
//            }
            String sql = getSql(configuration, boundSql, sqlId, operator, end);
            System.err.println(sql);
            logger.info(sql);
            LocalDate nowDate = LocalDate.now();
            int hour = LocalTime.now().getHour();
            // 此拦截器方法会执行两次，插入前先判断是否与redis上一条数据重复
            String prePush = null;
            if (redisTemplate.hasKey(RedisConstant.ELE_SQL_KEY + nowDate + hour)) {
            	prePush = redisTemplate.opsForList().index(RedisConstant.ELE_SQL_KEY + nowDate + hour, -1).toString();
            }
            if (StringUtils.isNotBlank(prePush)) {
            	if (!StringUtils.equals(prePush.split(":")[3].split("\\|")[0], sql.split(":")[3].split("\\|")[0])) {
            		redisTemplate.opsForList().rightPush(RedisConstant.ELE_SQL_KEY + nowDate + hour, sql);
            	}
            } else {
            	redisTemplate.opsForList().rightPush(RedisConstant.ELE_SQL_KEY + nowDate + hour, sql);
            }
        }
        return returnValue;
    }

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId, String operator, long createTime) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        LocalDate now = LocalDate.now();
        LocalTime now2 = LocalTime.now();
        str.append(now);
        str.append(" ");
        str.append(now2);
        str.append(" ");
        str.append(sqlId);
        str.append(":");
        str.append(sql);
//        str.append(":");
//        str.append(time);
//        str.append("ms");
        str.append("|");
        str.append(operator);
        str.append("|");
        str.append(createTime);
        return str.toString();
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else {
                        Map map = (Map)metaObject ;
                         sql = sql.replaceFirst("\\?", getParameterValue(map.get(propertyName)));
            		}  
                }
            }
        }
        return sql;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties0) {
        this.properties = properties0;
    }

}
