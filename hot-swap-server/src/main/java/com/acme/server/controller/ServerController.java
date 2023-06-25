package com.acme.server.controller;

import com.acme.common.utils.ProcessUtils;
import com.acme.common.utils.VMUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/16 15:11
 */


@RestController
@RequestMapping("/server")
public class ServerController {

    @RequestMapping("jpscmd")
    public List<String> jpsCmd(){

        return ProcessUtils.jpsCmd();
    }

    @RequestMapping("attach-pid/{pid}/{targetPort}")
    public void attachVM4pid(@PathVariable String pid,@PathVariable String targetPort){
       VMUtils.attachVM(pid,targetPort);
    }


}
