import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/13 19:26
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test1() {
        redisTemplate.opsForValue().set("token","123", 30, TimeUnit.MINUTES);
        String token = redisTemplate.opsForValue().get("token");
        System.out.println(token);
    }
}
