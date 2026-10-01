package io.github.scordio.springframework.security.oauth2.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.TestPropertySource;
import org.wiremock.spring.EnableWireMock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableWireMock
@ExtendWith(AutoConfigureEmbeddedJwtSigner.EmbeddedJwtSignerExtension.class)
@TestPropertySource(properties = { //
		"spring.security.oauth2.resourceserver.jwt.issuer-uri=${wiremock.server.baseUrl}",
		"spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${wiremock.server.baseUrl}/jwks" //
})
public @interface AutoConfigureEmbeddedJwtSigner {

	class EmbeddedJwtSignerExtension implements BeforeEachCallback, AfterEachCallback {

		private static final String JWKS_RESPONSE;

		static {
			try {
				JWKS_RESPONSE = new ObjectMapper().writeValueAsString(EmbeddedJwtSigner.INSTANCE.getJWKSet());
			}
			catch (JsonProcessingException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void beforeEach(ExtensionContext context) {
			givenThat(get("/jwks").willReturn(okJson(JWKS_RESPONSE)));
		}

		@Override
		public void afterEach(ExtensionContext context) {
			reset();
		}

	}

}
