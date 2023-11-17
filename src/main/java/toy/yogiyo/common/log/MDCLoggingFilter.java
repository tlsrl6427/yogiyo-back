package toy.yogiyo.common.log;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UUID uuid = UUID.randomUUID();
        MDC.put("traceId", uuid.toString());
        chain.doFilter(request, response);
        MDC.clear();    // 쓰레드 풀을 통해 해당 스레드를 재사용하기 때문에 clear 해줘야함
    }
}
