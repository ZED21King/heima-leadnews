import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.Payload;
import com.heima.utils.common.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String publicKeyPath = "D:\\Environment\\rsa\\rsa-key.pub";
    private static final String privateKeyPath = "D:\\Environment\\rsa\\rsa-key";

    /**
     * 生成token
     */
    @Test
    public void testGenerateToken() throws Exception {
        /**
         * 参数一：存在载荷的用户登录信息
         * 参数二：私钥对象
         * 参数三：过期时间
         */
        User user = new User();
        user.setName("admin");
        user.setPassword("123");
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        String token = JwtUtils.generateTokenExpireInMinutes(user, privateKey, 1);
        System.out.println(token);
    }


    /**
     * 解析token
     */
    @Test
    public void testVerifyToken() throws Exception {
        /**
         * 参数一：需要解析的token
         * 参数二：公钥对象
         * 参数三：载荷中用户信息的类型
         */
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wibmFtZVwiOlwiYWRtaW5cIixcInBhc3N3b3JkXCI6XCIxMjNcIn0iLCJqdGkiOiJaREk0Tm1NMk9UUXRORFUzWVMwMFkyRXpMVGd4WWpZdE1qUTJORFF5WVRBMFlqQTMiLCJleHAiOjE2NDcxNjc5NzB9.QXh6dE4qjcVCxnaOQrt3PjmmK7rhBsYuXUo-3fXy3YWwYke9xhE23Onkb4EseVHsHtWBhxVup0W4WlQFlf5HgljW0Lg87MuID_ARYH7s-wpuzDRPqE_sz9_RBmZwrQ12giT4Sx0UHWfF6TiyoYY_LMPlgZ86BbtLUOfVLQENV2qCSDLMN5J_W4kZWS5vIMAnptI8SQfudWw1hWlZW3Qt1jRi8MhqzOBK09xSpyzlZoQdiTPr718kdPaQVDwbKTmCM4G3CDMsPjXOPvdb7LsmnUKjNL5fUl1BiDz8TADBMYI_YRV4aanpjoOLhENpoPxaiZC58V8eUIkPPFzIloQjAw";
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
        Payload<User> payload = JwtUtils.getInfoFromToken(token, publicKey, User.class);
        //获取用户信息
        User user = payload.getInfo();
        System.out.println(user);
    }
}

class User {
    String name;
    String password;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}