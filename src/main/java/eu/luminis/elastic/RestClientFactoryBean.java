package eu.luminis.elastic;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.sniff.Sniffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Factory bean for creating the RestClient instance(s)
 */
@Component
public class RestClientFactoryBean extends AbstractFactoryBean<RestClient> {
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String DEFAULT_HEADER_CONTENT_TYPE = "application/json";

    private final LoggingFailureListener loggingFailureListener;

    private String[] hostnames;

    private Sniffer sniffer;

    @Value("${enableSniffer:true}")
    private boolean enableSniffer = true;

    @Autowired
    public RestClientFactoryBean(LoggingFailureListener loggingFailureListener) {
        this.loggingFailureListener = loggingFailureListener;
    }

    @Override
    public Class<?> getObjectType() {
        return RestClient.class;
    }

    @Override
    protected RestClient createInstance() throws Exception {
        HttpHost[] hosts = new HttpHost[hostnames.length];
        for (int i = 0; i < hosts.length; i++) {
            hosts[i] = HttpHost.create(hostnames[i]);
        }

        Header[] defaultHeaders = new Header[]{new BasicHeader(HEADER_CONTENT_TYPE_KEY, DEFAULT_HEADER_CONTENT_TYPE)};

        RestClient restClient = RestClient
                .builder(hosts)
                .setDefaultHeaders(defaultHeaders)
                .setFailureListener(loggingFailureListener)
                .build();

        if (enableSniffer) {
            this.sniffer = Sniffer.builder(restClient).build();
        }
        return restClient;
    }

    @Override
    protected void destroyInstance(RestClient instance) throws Exception {
        try {
            logger.info("Closing the elasticsearch sniffer");
            if (enableSniffer) {
                this.sniffer.close();
            }
            instance.close();
        } catch (IOException e) {
            logger.warn("Failed to close the elasticsearch sniffer");
        }
    }

    @Value("${eu.luminis.elastic.hostnames:#{\"localhost:9200\"}}")
    public void setHostnames(String[] hostnames) {
        this.hostnames = hostnames;
    }
}
