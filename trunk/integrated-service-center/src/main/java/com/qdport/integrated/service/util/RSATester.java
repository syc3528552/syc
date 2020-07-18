package com.qdport.integrated.service.util;

import java.util.Map;

public class RSATester {

    static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCTzlRzId62rauSG8SzLM1WBKKgcn6q+pwnYsjCSdTKRpnfAO4kY+hVmo6+GY4C2cJuNrnCQjdKPPY6PzfVc7q/KYtNLKJTXDcfna5izl7iXGQQ4a43ws/Oq4SwEyaHa+pnIG7R8auS0JN5BnT60odZBUh+rH6NCEy/9nQwB7XcQIDAQAB";
    static String privateKey="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIJPOVHMh3ratq5Ibx" +
    		"LMszVYEoqByfqr6nCdiyMJJ1MpGmd8A7iRj6FWajr4ZjgLZwm42ucJCN0o89jo/N9Vzur8pi00solNcNx+drm" +
    		"LOXuJcZBDhrjfCz86rhLATJodr6mcgbtHxq5LQk3kGdPrSh1kFSH6sfo0ITL/2dDAHtdxAgMBAAECgYAMakfnxz" +
    		"jRN61YBYavn9kjJ/T8RUldHKwFdr0e5Wxdu/jMC0C/gP7SPE16uNfkVxHGF1qj0HSLva8d53TQQnioP99xlsyPCLT" +
    		"3iAyVVTtcbxrnArSq6a3Bfz76HsFfP2gGbsKXGVkdGnTDGRFpxVb8/LgR+GYUCgItafMKQFWxAQJBAMgMBGO3hOeca" +
    		"xtg3qpBvsy9dAXiHsw6BJvCL81CjYlZwLT2Y3TEsfOYCkS/u0Zk9tVmmmNiC9mHnKm18roiM3kCQQCmwclxHjOTv8T7" +
    		"7SCA1jambVc2xK3SLMX6P6IZb3s+7DhUO5Bn6KEtcPcf56x0+gwAc99PerdpRNMCpZS9/o25AkBQwIEZG6C7uemGgMEBa" +
    		"06w6R0nrtKN0DHCCk4vhlkKA8AFjJwzK59F3K7wd5EKZoE5PZS+33o3EkYMP9U+i8XJAkBIsMC25otlJHshtkQs5TP9aDxU" +
    		"S3fZ3lqOs55dqBL5Ys9LKIvSP73CqTOQXNT4SOdiayA07TF2WeIBjVxBVe1BAkB44WUN1e9MTLEa9nRc9galn64G9L/XlJWMNE" +
    		"3mMr+5rhV8eajPBoFqJrAZqJiOJaU8W6TC/jmHFISqRKFZOtkl";

   
    
    public static void main(String[] args) throws Exception {
      
        testHttpSign();
    }

    
    static void testHttpSign() throws Exception {
        String param = "id=1&name=����";
        byte[] encodedData = RSAUtil.encryptByPrivateKey(param.getBytes(), privateKey);
        System.out.println("���ܺ�" + encodedData);
        
        byte[] decodedData = RSAUtil.decryptByPublicKey(encodedData, publicKey);
        System.out.println("���ܺ�" + new String(decodedData));
        
        String sign = RSAUtil.sign(encodedData, privateKey);
        System.err.println("ǩ����" + sign);
        
        boolean status = RSAUtil.verify(encodedData, publicKey, sign);
        System.err.println("ǩ����֤�����" + status);
    }
    
    
}