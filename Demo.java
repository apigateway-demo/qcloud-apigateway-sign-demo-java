package apigatewayDemo;
public class Demo {
	public static void main(String[] args) {
		String url = "http://service-km944but-1253970226.ap-beijing.apigateway.myqcloud.com/release/kke";
		String secretId = "AKIDi6qE41WgJ9w8h4h9zq68Vq24d1beIuN0qIwU";
		String secretKey = "hGZqy11wj52wEFe3H7lGdU12sm41758no9f7093m";
		SignAndSend signAndSendInstance = new SignAndSend();
		String result = SignAndSend.sendGet(url, secretId, secretKey);
        System.out.println(result);
    }
}