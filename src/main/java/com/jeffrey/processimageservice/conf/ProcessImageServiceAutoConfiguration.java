package com.jeffrey.processimageservice.conf;

import com.jeffrey.processimageservice.entities.AsyncPendingTasksItem;
import com.jeffrey.processimageservice.entities.ProcessStatus;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Configuration
@EnableConfigurationProperties({
        TranslateProperties.class,
        MyThreadPoolTaskExecutorProperties.class,
        InitAccountParamProperties.class
})
public class ProcessImageServiceAutoConfiguration {

//    @Value("${spring.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.redis.port}")
//    private int redisPort;
//    @Value("${spring.redis.password}")
//    private String password;
//
//    @Value("${spring.redis.database}")
//    private int database;

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
//        configuration.setHostName(redisHost);
//        configuration.setPort(redisPort);
//        configuration.setPassword(password);
//        configuration.setDatabase(database);
//        return new LettuceConnectionFactory(configuration);
//    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        return redisTemplate;
    }

    @Bean(name = "encoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean(name = "myTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(MyThreadPoolTaskExecutorProperties threadPoolTaskExecutorProperties) {

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(threadPoolTaskExecutorProperties.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolTaskExecutorProperties.getMaxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(threadPoolTaskExecutorProperties.getQueueCapacity());
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolTaskExecutorProperties.getKeepAliveSeconds());
        threadPoolTaskExecutor.setThreadNamePrefix(threadPoolTaskExecutorProperties.getThreadNamePrefix());
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(threadPoolTaskExecutorProperties.getWaitForTasksToCompleteOnShutdown());
        threadPoolTaskExecutor.setAwaitTerminationSeconds(threadPoolTaskExecutorProperties.getAwaitTerminationSeconds());
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }

    @Bean(name = "requestParamsWrapperThreadLocal")
    public ThreadLocal<RequestParamsWrapper> requestParamsWrapperLocal() {
        return new ThreadLocal<>();
    }

    @Bean(name = "encryptedInfoThreadLocal")
    public ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal() {
        return new ThreadLocal<>();
    }


    @Bean(value = "restTemplate")
    public RestTemplate restTemplate() {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        httpRequestFactory.setConnectionRequestTimeout(8000);
        httpRequestFactory.setConnectTimeout(8000);
        httpRequestFactory.setReadTimeout(8000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
                // 使RestTemplate能够对响应的错误消息不进行处理
                // 如：当响应码为400、500等错误时，能够不进行处理，以便正常获取响应数据
                return false;
            }

            @Override
            public void handleError(@NotNull ClientHttpResponse response) throws IOException {
                // ignore
                System.out.println(response.getStatusCode());
                System.out.println(response.getBody());
            }

        });

        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }

    /**
     * 往 IOC 中添加一个待处理任务队列，使用 LinkedHashSet 确保任务的有序性。AsyncPendingTasks 对象为挂起的任务
     *
     * @return 待处理任务队列
     */
    @Bean
    public LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue() {
        return new LinkedBlockingQueue<>();
    }


    @Bean(name = "reportTaskQueue")
    public LinkedBlockingQueue<ProcessStatus> reportTaskThreadLocal(){
        return new LinkedBlockingQueue<>();
    }
}
