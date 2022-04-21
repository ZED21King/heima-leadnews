package com.heima.utils.common;

import java.util.UUID;

/**
 * 激活码随机生成工具
 * 
 * @author zed
 * @version v1.0
 */
public class UUIDUtils {
	
	/** UUID随机生成方法 */
	public static String generateUuid() {
		// 把-替换为空
		return UUID.randomUUID().toString().replace("-", "");
	}
}