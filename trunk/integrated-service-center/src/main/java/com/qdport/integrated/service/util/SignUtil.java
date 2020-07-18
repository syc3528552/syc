package com.qdport.integrated.service.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import it.sauronsoftware.base64.Base64;
/**
 * BASE64编码解码工具包
 * 依赖javabase64-1.3.1.jar
 * @author IceWee
 * @date 2012-5-19
 * @version 1.0
 */

public class SignUtil {




	    /**
	     * 文件读取缓冲区大小
	     */
	    private static final int CACHE_SIZE = 1024;

	    /**
	     * <p>
	     * BASE64字符串解码为二进制数据
	     * </p>
	     *
	     * @param base64
	     * @return
	     * @throws Exception
	     */
	    public static byte[] decode(String base64) throws Exception {
	        return Base64.decode(base64.getBytes());
	    }

	    /**
	     * <p>
	     * 二进制数据编码为BASE64字符串
	     * </p>
	     *
	     * @param bytes
	     * @return
	     * @throws Exception
	     */
	    public static String encode(byte[] bytes) throws Exception {
	        return new String(Base64.encode(bytes));
	    }

	    /**
	     * <p>
	     * 将文件编码为BASE64字符串
	     * </p>
	     * <p>
	     * 大文件慎用，可能会导致内存溢出
	     * </p>
	     *
	     * @param filePath
	     *            文件绝对路径
	     * @return
	     * @throws Exception
	     */
	    public static String encodeFile(String filePath) throws Exception {
	        byte[] bytes = fileToByte(filePath);
	        return encode(bytes);
	    }

	    /**
	     * <p>
	     * BASE64字符串转回文件
	     * </p>
	     *
	     * @param filePath
	     *            文件绝对路径
	     * @param base64
	     *            编码字符串
	     * @throws Exception
	     */
	    public static void decodeToFile(String filePath, String base64) throws Exception {
	        byte[] bytes = decode(base64);
	        byteArrayToFile(bytes, filePath);
	    }

	    /**
	     * <p>
	     * 文件转换为二进制数组
	     * </p>
	     *
	     * @param filePath
	     *            文件路径
	     * @return
	     * @throws Exception
	     */
	    public static byte[] fileToByte(String filePath) throws Exception {
	        byte[] data = new byte[0];
	        File file = new File(filePath);
	        if (file.exists()) {
	            FileInputStream in = new FileInputStream(file);
	            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
	            byte[] cache = new byte[CACHE_SIZE];
	            int nRead = 0;
	            while ((nRead = in.read(cache)) != -1) {
	                out.write(cache, 0, nRead);
	                out.flush();
	            }
	            out.close();
	            in.close();
	            data = out.toByteArray();
	        }
	        return data;
	    }

	    /**
	     * <p>
	     * 二进制数据写文件
	     * </p>
	     *
	     * @param bytes
	     *            二进制数据
	     * @param filePath
	     *            文件生成目录
	     */
	    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
	        InputStream in = new ByteArrayInputStream(bytes);
	        File destFile = new File(filePath);
	        if (!destFile.getParentFile().exists()) {
	            destFile.getParentFile().mkdirs();
	        }
	        destFile.createNewFile();
	        OutputStream out = new FileOutputStream(destFile);
	        byte[] cache = new byte[CACHE_SIZE];
	        int nRead = 0;
	        while ((nRead = in.read(cache)) != -1) {
	            out.write(cache, 0, nRead);
	            out.flush();
	        }
	        out.close();
	        in.close();
	    }

	    /**
		 * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串
		 *
		 * @param paraMap
		 *            要排序的Map对象
		 * @param urlEncode
		 *            是否需要URLENCODE
		 * @param keyToLower
		 *            是否需要将Key转换为全小写 true:key转化成小写，false:不转化
		 * @return
		 */
		public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower) {
			String buff = "";
			Map<String, String> tmpMap = paraMap;
			try {
				List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
				// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
				Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
					@Override
					public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
						return (o1.getKey()).toString().compareTo(o2.getKey());
					}
				});
				// 构造URL 键值对的格式
				StringBuilder buf = new StringBuilder();
				for (Map.Entry<String, String> item : infoIds) {
					if (isNullString(item.getKey())) {
						String key = item.getKey();
						String val = item.getValue();
						if (urlEncode) {
							val = URLEncoder.encode(val, "utf-8");
						}
						if (keyToLower) {
							buf.append(key.toLowerCase() + "=" + val);
						} else {
							buf.append(key + "=" + val);
						}
						buf.append("&");
					}
				}
				buff = buf.toString();
				if (buff.isEmpty() == false) {
					buff = buff.substring(0, buff.length() - 1);
				}
			} catch (Exception e) {
				return null;
			}
			return buff;
		}


		/**
		 * 判断一个String 变量是否为Null;
		 * @param value
		 * @return boolean
		 */
		public static boolean isNullString(String value) {
			if (value == null || "".equals(value.trim())) {
				return true;
			}
			return false;
		}
}
