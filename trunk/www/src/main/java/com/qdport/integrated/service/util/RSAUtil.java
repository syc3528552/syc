package com.qdport.integrated.service.util;

	import java.io.ByteArrayOutputStream;
	import java.security.Key;
	import java.security.KeyFactory;
	import java.security.PrivateKey;
	import java.security.PublicKey;
	import java.security.Signature;
	import java.security.spec.PKCS8EncodedKeySpec;
	import java.security.spec.X509EncodedKeySpec;
	import java.util.Map;

	import javax.crypto.Cipher;

	/**
	 * RSA公钥/私钥/签名工具包
	 *
	 * @author IceWee
	 * @date 2012-4-26
	 * @version 1.0
	 */
	public class RSAUtil {

	    /**
	     * 加密算法RSA
	     */
	    public static final String KEY_ALGORITHM = "RSA";

	    /**
	     * 签名算法
	     */
	    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	    /**
	     * 获取公钥的key
	     */
	    private static final String PUBLIC_KEY = "RSAPublicKey";

	    /**
	     * 获取私钥的key
	     */
	    private static final String PRIVATE_KEY = "RSAPrivateKey";

	    /**
	     * RSA最大加密明文大小
	     */
	    private static final int MAX_ENCRYPT_BLOCK = 117;

	    /**
	     * RSA最大解密密文大小
	     */
	    private static final int MAX_DECRYPT_BLOCK = 128;


	    /**
	     * <p>
	     * 用私钥对信息生成数字签名
	     * </p>
	     *
	     * @param data 已加密数据
	     * @param privateKey 私钥(BASE64编码)
	     *
	     * @return
	     * @throws Exception
	     */
	    public static String sign(byte[] data, String privateKey) throws Exception {
	        byte[] keyBytes = SignUtil.decode(privateKey);
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	        signature.initSign(privateK);
	        signature.update(data);
	        return SignUtil.encode(signature.sign());
	    }

	    /**
	     * <p>
	     * 校验数字签名
	     * </p>
	     *
	     * @param data 已加密数据
	     * @param publicKey 公钥(BASE64编码)
	     * @param sign 数字签名
	     *
	     * @return
	     * @throws Exception
	     *
	     */
	    public static boolean verify(byte[] data, String publicKey, String sign)
	            throws Exception {
	        byte[] keyBytes = SignUtil.decode(publicKey);
	        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        PublicKey publicK = keyFactory.generatePublic(keySpec);
	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	        signature.initVerify(publicK);
	        signature.update(data);
	        return signature.verify(SignUtil.decode(sign));
	    }

	    /**
	     * <P>
	     * 私钥解密
	     * </p>
	     *
	     * @param encryptedData 已加密数据
	     * @param privateKey 私钥(BASE64编码)
	     * @return
	     * @throws Exception
	     */
	    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
	            throws Exception {
	        byte[] keyBytes = SignUtil.decode(privateKey);
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, privateK);
	        int inputLen = encryptedData.length;
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        int offSet = 0;
	        byte[] cache;
	        int i = 0;
	        // 对数据分段解密
	        while (inputLen - offSet > 0) {
	            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
	                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
	            } else {
	                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
	            }
	            out.write(cache, 0, cache.length);
	            i++;
	            offSet = i * MAX_DECRYPT_BLOCK;
	        }
	        byte[] decryptedData = out.toByteArray();
	        out.close();
	        return decryptedData;
	    }

	    /**
	     * <p>
	     * 公钥解密
	     * </p>
	     *
	     * @param encryptedData 已加密数据
	     * @param publicKey 公钥(BASE64编码)
	     * @return
	     * @throws Exception
	     */
	    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
	            throws Exception {
	        byte[] keyBytes = SignUtil.decode(publicKey);
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        Key publicK = keyFactory.generatePublic(x509KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, publicK);
	        int inputLen = encryptedData.length;
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        int offSet = 0;
	        byte[] cache;
	        int i = 0;
	        // 对数据分段解密
	        while (inputLen - offSet > 0) {
	            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
	                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
	            } else {
	                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
	            }
	            out.write(cache, 0, cache.length);
	            i++;
	            offSet = i * MAX_DECRYPT_BLOCK;
	        }
	        byte[] decryptedData = out.toByteArray();
	        out.close();
	        return decryptedData;
	    }

	    /**
	     * <p>
	     * 公钥加密
	     * </p>
	     *
	     * @param data 源数据
	     * @param publicKey 公钥(BASE64编码)
	     * @return
	     * @throws Exception
	     */
	    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
	            throws Exception {
	        byte[] keyBytes = SignUtil.decode(publicKey);
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        Key publicK = keyFactory.generatePublic(x509KeySpec);
	        // 对数据加密
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, publicK);
	        int inputLen = data.length;
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        int offSet = 0;
	        byte[] cache;
	        int i = 0;
	        // 对数据分段加密
	        while (inputLen - offSet > 0) {
	            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
	                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
	            } else {
	                cache = cipher.doFinal(data, offSet, inputLen - offSet);
	            }
	            out.write(cache, 0, cache.length);
	            i++;
	            offSet = i * MAX_ENCRYPT_BLOCK;
	        }
	        byte[] encryptedData = out.toByteArray();
	        out.close();
	        return encryptedData;
	    }

	    /**
	     * <p>
	     * 私钥加密
	     * </p>
	     *
	     * @param data 源数据
	     * @param privateKey 私钥(BASE64编码)
	     * @return
	     * @throws Exception
	     */
	    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
	            throws Exception {
	        byte[] keyBytes = SignUtil.decode(privateKey);
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, privateK);
	        int inputLen = data.length;
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        int offSet = 0;
	        byte[] cache;
	        int i = 0;
	        // 对数据分段加密
	        while (inputLen - offSet > 0) {
	            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
	                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
	            } else {
	                cache = cipher.doFinal(data, offSet, inputLen - offSet);
	            }
	            out.write(cache, 0, cache.length);
	            i++;
	            offSet = i * MAX_ENCRYPT_BLOCK;
	        }
	        byte[] encryptedData = out.toByteArray();
	        out.close();
	        return encryptedData;
	    }

	    /**
	     * <p>
	     * 获取私钥
	     * </p>
	     *
	     * @param keyMap 密钥对
	     * @return
	     * @throws Exception
	     */
	    public static String getPrivateKey(Map<String, Object> keyMap)
	            throws Exception {
	        Key key = (Key) keyMap.get(PRIVATE_KEY);
	        return SignUtil.encode(key.getEncoded());
	    }

	    /**
	     * <p>
	     * 获取公钥
	     * </p>
	     *
	     * @param keyMap 密钥对
	     * @return
	     * @throws Exception
	     */
	    public static String getPublicKey(Map<String, Object> keyMap)
	            throws Exception {
	        Key key = (Key) keyMap.get(PUBLIC_KEY);
	        return SignUtil.encode(key.getEncoded());
	    }

	}
