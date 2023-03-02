package com.jeffrey.processimageservice.conf;

import com.jeffrey.processimageservice.entities.*;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.filter.RequestFilter;
import com.jeffrey.processimageservice.security.Decrypt;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.TreeMap;
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
        InitAccountParamProperties.class,
        JavaMailSenderProperties.class
})
public class ProcessImageServiceAutoConfiguration {

    @Bean
    public FilterRegistrationBean<RequestFilter> filterRegistrationConf(RequestFilter requestFilter){
        FilterRegistrationBean<RequestFilter> requestFilterFilterRegistrationBean = new FilterRegistrationBean<>();
        requestFilterFilterRegistrationBean.setFilter(requestFilter);
        requestFilterFilterRegistrationBean.addUrlPatterns("/*");
        requestFilterFilterRegistrationBean.setName("requestFilter");
        requestFilterFilterRegistrationBean.setOrder(1);
        return requestFilterFilterRegistrationBean;
    }

    @Bean(name = "myJavaMailSender")
    public JavaMailSender javaMailSender(JavaMailSenderProperties javaMailSenderProperties){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(javaMailSenderProperties.getHost());
        javaMailSender.setPort(javaMailSenderProperties.getPort());
        javaMailSender.setUsername(javaMailSenderProperties.getDualAuthenticationUsername());
        javaMailSender.setPassword(javaMailSenderProperties.getDualAuthenticationPassword());
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.starttls.enable", String.valueOf(javaMailSenderProperties.getMailSmtpStarttlsEnable()));
        properties.setProperty("mail.smtp.starttls.required", String.valueOf(javaMailSenderProperties.getMailSmtpStarttlsRequired()));
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }

    @Bean(name = "mimeMessageHelper")
    public MimeMessageHelper mimeMessageHelper(JavaMailSender myJavaMailSender,JavaMailSenderProperties javaMailSenderProperties){
        MimeMessage mimeMessage = myJavaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setFrom(javaMailSenderProperties.getFrom());
            String[] cc = javaMailSenderProperties.getCc();
            if (cc != null && cc.length > 0) {

                mimeMessageHelper.setCc(cc);
            }
            return mimeMessageHelper;
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("初始化邮件服务失败");
        }
    }

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
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean(name = "decrypt")
    public Decrypt decrypt(Info info){
        return new Decrypt(info);
    }

    @Bean(name = "cacheTaskMap")
    public TreeMap<Long, File> cacheTaskMap(){
        return new TreeMap<>();
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


//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory strategy = new MultipartConfigFactory();
//        String location = "/Users/jeffrey/IdeaProjects/processImageService/src/main/resources";
//        File tmpFile = new File(location);
//        if (!tmpFile.exists()) {
//            tmpFile.mkdirs();
//        }
//        strategy.setLocation(location);
//        return strategy.createMultipartConfig();
//    }


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
    public LinkedBlockingQueue<ProcessStatus> reportTaskThreadLocal() {
        return new LinkedBlockingQueue<>();
    }
}
