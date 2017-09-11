/**
 * Project Name:WithinJetty
 * File Name:PropertiesUtil.java
 * Package Name:com
 * Date:2017年6月28日下午5:45:27
 * Copyright (c) 2017, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.example.demo.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * ClassName:PropertiesUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2017年6月28日 下午5:45:27 <br/>
 * @author   Yang Jx
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
public class PropertiesUtil {

	private static Map<String, String> propMap;
	
	static{
		try {
			propMap = getPropertyMap("config.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * getPropertyMap:(返回配置项目键值对Map). <br/>
	 *
	 * @author 杨建雄
	 * @param path  配置文件路径
	 * @return
	 * @throws Exception
	 * @since JDK 1.8
	 */
	public static Map<String, String> getPropertyMap(String path) throws Exception{
		
		Map<String,String> propertyMap = new HashMap<String,String>();
		try {
			Properties property = new Properties();
			InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
			property.load(inputStream);
			Set<Object> keySet = property.keySet();
			Iterator<Object> it = keySet.iterator();
			while(it.hasNext()){
				String key = (String) it.next();
				String value = property.getProperty(key);
				value = new String(value.getBytes("iso-8859-1"), "UTF-8");
				propertyMap.put(key, value);
			}
			return propertyMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	public static Map<String, String> getPropMap() {
		return propMap;
	}
	public static void setPropMap(Map<String, String> propMap) {
		PropertiesUtil.propMap = propMap;
	}
	
}

