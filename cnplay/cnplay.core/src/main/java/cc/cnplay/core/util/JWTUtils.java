package cc.cnplay.core.util;

import java.util.HashMap;
import java.util.Map;

import cc.cnplay.core.vo.Item;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

public class JWTUtils {
	private static String secret = "CC.CNPLAY.XERE";

	public static String sign(Object value, long exp) {
		JWTSigner signer = new JWTSigner(secret);
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("token", value);
		claims.put("exp", exp);
		String token = signer.sign(claims);
		return token;
	}

	@SuppressWarnings("unchecked")
	public static <T> T verify(String token, Class<T> tokenType)
			throws JWTVerifyException {
		JWTVerifier verifier = new JWTVerifier(secret);
		Map<String, Object> claims;
		T bean = null;
		try {
			claims = verifier.verify(token);
			Map<String, Object> value = (Map<String, Object>) claims.get("token");
			bean = tokenType.newInstance();
			BeanUtils.copyProperties(bean, value);
		} catch (JWTVerifyException e) {
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	public static void main(String[] args) throws Exception {
		long exp = System.currentTimeMillis() / 1000L + (7 * 24 * 60 * 60);
		Item item = new Item();
		item.setId("userId");
		item.setText("token text");

		String token = JWTUtils.sign(item, exp);
		Item it = JWTUtils.verify(token, Item.class);
		System.out.println(token);
		System.out.println(it);
	}
}
