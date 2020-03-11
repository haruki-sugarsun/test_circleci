package net.sickhack.test_circleci;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

/**
 * An example of a configuration which provides beans for customizing the server
 * and client.
 */
@Configuration
public class HelloConfiguration {

	@Bean
	Object init(Environment environment) {
		System.err.println("haruki DEBUG: " + environment);

		// ref:
		// https://stackoverflow.com/questions/23506471/spring-access-all-environment-properties-as-a-map-or-properties-object
		Map<String, Object> map = new HashMap<String, Object>();
		for (Iterator<PropertySource<?>> it = ((AbstractEnvironment) environment).getPropertySources().iterator(); it
				.hasNext();) {
			PropertySource<?> propertySource = (PropertySource<?>) it.next();
			if (propertySource instanceof MapPropertySource) {
				map.putAll(((MapPropertySource) propertySource).getSource());
			}
		}

		System.err.println("haruki DEBUG: " + map);
		System.exit(1);
		return null;
	}

	/**
	 * A user can configure a {@link Server} by providing an
	 * {@link ArmeriaServerConfigurator} bean.
	 */
	@Bean
	public ArmeriaServerConfigurator armeriaServerConfigurator(HelloAnnotatedService service) {
		// Customize the server using the given ServerBuilder. For example:
		return builder -> {
			// Add DocService that enables you to send Thrift and gRPC requests
			// from web browser.
			builder.serviceUnder("/docs", new DocService());

			// Log every message which the server receives and responds.
			builder.decorator(LoggingService.newDecorator());

			// Write access log after completing a request.
			builder.accessLogWriter(AccessLogWriter.combined(), false);

			// Add an Armeria annotated HTTP service.
			builder.annotatedService(service);

			// You can also bind asynchronous RPC services such as Thrift and
			// gRPC:
			// builder.service(THttpService.of(...));
			// builder.service(GrpcService.builder()...build());
		};
	}
}
