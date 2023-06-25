package com.acme.monitor.server;

import com.acme.monitor.config.ApplicationConfigContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/16 13:55
 */

@Component
public class RemoteServerApi {

    private String baseUrl;

    private WebClient webClient;

    @Autowired
    private ApplicationConfigContext applicationConfigContext;

    @PostConstruct
    private void  initServerApi(){
        baseUrl = applicationConfigContext.serverUrl;
        webClient = WebClient.create();
    }

    public List<String> jpsCmdToRemoteServer(){
        Mono<List> listMono = webClient.get().uri(baseUrl + "/server/jpscmd").retrieve().bodyToMono(List.class);
        return listMono.block();

    }

    public void attachPidToRemoteServer(String pid){
        Mono<Object> objectMono = webClient.get().uri(baseUrl + "/server/attach-pid/{1}/{2}", pid, applicationConfigContext.targetPort + "_" + applicationConfigContext.mode).retrieve().bodyToMono(Object.class);
        objectMono.block();

    }



}
