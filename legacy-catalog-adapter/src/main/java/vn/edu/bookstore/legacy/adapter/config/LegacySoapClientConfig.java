package vn.edu.bookstore.legacy.adapter.config;

import java.net.URL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogPortType;
import vn.edu.bookstore.legacy.adapter.soap.generated.BookCatalogService;

import jakarta.xml.ws.BindingProvider;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.ws.handler.Handler;
import vn.edu.bookstore.legacy.adapter.soap.SoapLoggingHandler;

@Configuration
public class LegacySoapClientConfig {

    /** Thuộc tính timeout của Metro (Jakarta XML WS RI), đơn vị mili giây. */
    static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";

    @Bean
    BookCatalogPortType bookCatalogPort(LegacySoapProperties props) {
        URL wsdl = LegacySoapClientConfig.class.getResource("/wsdl/BookCatalogService.wsdl");
        BookCatalogPortType port = new BookCatalogService(wsdl).getBookCatalogPort();

        Map<String, Object> ctx =
                ((BindingProvider) port).getRequestContext();

        // Cấu hình địa chỉ endpoint của dịch vụ SOAP
        ctx.put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                props.endpoint()
        );

        // Cấu hình thời gian chờ kết nối, tính bằng mili giây
        ctx.put(
                CONNECT_TIMEOUT,
                (int) props.connectTimeout().toMillis()
        );

        // Cấu hình thời gian chờ phản hồi, tính bằng mili giây
        ctx.put(
                REQUEST_TIMEOUT,
                (int) props.readTimeout().toMillis()
        );

// TH1-5: Gắn handler ghi log thông điệp SOAP vào/ra
if (props.logMessages()) {
    @SuppressWarnings("rawtypes")
    List<Handler> chain = new ArrayList<>(
            ((BindingProvider) port).getBinding().getHandlerChain()
    );

    chain.add(new SoapLoggingHandler());

    ((BindingProvider) port).getBinding().setHandlerChain(chain);
}

return port;
}
}
