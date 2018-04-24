package m.khokhlov.configuration;

import m.khokhlov.EchechessApplication;
import ca.watier.echesscommon.utils.EcKeystoreGenerator;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.security.KeyStore;

import static ca.watier.echesscommon.utils.EcKeystoreGenerator.PRNG;
import static ca.watier.echesscommon.utils.EcKeystoreGenerator.PROVIDER_NAME;


@Profile("prod")
@Configuration
public class ProdSecurityConfiguration {
    private static final int SECURE_PORT = 8443;
    private static final int WEB_PORT = 8080;

    private static final EcKeystoreGenerator.KeystorePasswordHolder CURRENT_KEYSTORE_HOLDER = EcKeystoreGenerator.createKeystore();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EchechessApplication.class);


    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        JettyServletWebServerFactory jettyServletWebServerFactory = new JettyServletWebServerFactory();
        jettyServletWebServerFactory.addConfigurations(new HttpToHttpsJettyConfiguration());
        jettyServletWebServerFactory.addServerCustomizers(server -> {
            HttpConfiguration httpsConfig = new HttpConfiguration();
            httpsConfig.addCustomizer(new SecureRequestCustomizer());
            httpsConfig.setSecureScheme("https");
            httpsConfig.setSecurePort(SECURE_PORT);

            HttpConfiguration httpConfig = new HttpConfiguration();
            httpConfig.addCustomizer(new SecureRequestCustomizer());
            httpConfig.setSecurePort(SECURE_PORT);

            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStoreProvider(PROVIDER_NAME);
            sslContextFactory.setSecureRandomAlgorithm(PRNG);
            sslContextFactory.setIncludeProtocols("TLSv1.2");

            if (CURRENT_KEYSTORE_HOLDER == null) {
                LOGGER.error("INVALID KEYSTORE HOLDER (NULL)");
                System.exit(1);
            }

            KeyStore keyStore = CURRENT_KEYSTORE_HOLDER.getKeyStore();

            if (keyStore == null) {
                LOGGER.error("INVALID KEYSTORE (NULL)");
                System.exit(1);
            }

            sslContextFactory.setKeyStore(keyStore);
            sslContextFactory.setKeyStorePassword(CURRENT_KEYSTORE_HOLDER.getPassword());

            sslContextFactory.setIncludeCipherSuites(
                    "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                    "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256"
            );

            ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
            http.setPort(WEB_PORT);

            ServerConnector https = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(httpsConfig));
            https.setPort(SECURE_PORT);
            https.setIdleTimeout(500000);

            server.setConnectors(new Connector[]{https, http});
        });

        return jettyServletWebServerFactory;
    }


    private class HttpToHttpsJettyConfiguration extends AbstractConfiguration {
        // http://wiki.eclipse.org/Jetty/Howto/Configure_SSL#Redirecting_http_requests_to_https
        @Override
        public void configure(WebAppContext context) {
            Constraint constraint = new Constraint();
            constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL);

            ConstraintMapping constraintMapping = new ConstraintMapping();
            constraintMapping.setPathSpec("/*");
            constraintMapping.setConstraint(constraint);

            ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
            constraintSecurityHandler.addConstraintMapping(constraintMapping);

            context.setSecurityHandler(constraintSecurityHandler);
        }
    }

}
