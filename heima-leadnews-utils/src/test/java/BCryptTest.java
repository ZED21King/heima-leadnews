
import com.heima.utils.common.BCrypt;
import org.junit.Test;

public class BCryptTest {

    /**
     * 加密
     */
    @Test
    public void testEncode(){
        String password = "admin";
//        String password = "123";

        //产生随机盐
        String gensalt = BCrypt.gensalt();

        //加密
        String pwd = BCrypt.hashpw(password, gensalt);

        System.out.println(gensalt);
        System.out.println(pwd);
    }

    /**
     * 密码匹配
     */
    @Test
    public void testMatch(){
        String password = "admin";
        
        boolean flag = BCrypt.checkpw(password,
                "$2a$10$Ku5gSwV9kG.nFNV48BvBU.8w55UWKx9Nc5D0Z.8HbE5/cBonjRWkG");

        System.out.println(flag);
    }
}