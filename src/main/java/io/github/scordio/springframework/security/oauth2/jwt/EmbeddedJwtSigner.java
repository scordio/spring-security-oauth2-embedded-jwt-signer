package io.github.scordio.springframework.security.oauth2.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

public enum EmbeddedJwtSigner {

	INSTANCE;

	private final JWSAlgorithm algorithm = JWSAlgorithm.RS512;

	private final RSAKey key;

	EmbeddedJwtSigner() {
		try {
			this.key = new RSAKeyGenerator(2048) //
				.keyID("embedded-jwt-signer")
				.algorithm(algorithm)
				.keyUse(KeyUse.SIGNATURE)
				.generate();
		}
		catch (JOSEException e) {
			throw new IllegalStateException(e);
		}
	}

	public JWSAlgorithm getAlgorithm() {
		return algorithm;
	}

	public JWKSet getJWKSet() {
		return new JWKSet(key.toPublicJWK());
	}

}
