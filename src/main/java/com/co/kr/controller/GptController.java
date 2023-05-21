package com.co.kr.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.co.kr.service.GptService;
import com.co.kr.vo.ResponseVo;
import com.co.kr.domain.RequestEditTextVo;
import com.co.kr.domain.RequestMakeNameVo;
import com.co.kr.domain.RequestQuestionVo;

@RequestMapping("/gptAPI")
@RestController
public class GptController {
	
    @Autowired
    GptService gptService;

    @RequestMapping(value = "/make/variable/name", method = RequestMethod.GET)
    public ResponseVo makeVariableName(@Valid RequestMakeNameVo requestMakeNameVo){

        ResponseVo responseVo = gptService.getVariableName(requestMakeNameVo);

        return responseVo;
    }

    @RequestMapping(value = "/make/class/name", method = RequestMethod.GET)
    public ResponseVo makeClassName(@Valid RequestMakeNameVo requestMakeNameVo){

        ResponseVo responseVo = gptService.getClassName(requestMakeNameVo);

        return responseVo;
    }

    @RequestMapping(value = "/make/conversation", method = RequestMethod.GET)
    public ResponseVo makeConversation(@Valid RequestQuestionVo requestQuestionVo){
        ResponseVo responseVo = gptService.getConversation(requestQuestionVo);

        return responseVo;
    }

    @RequestMapping(value = "/make/edit", method = RequestMethod.GET)
    public ResponseVo makeEdit(@Valid RequestEditTextVo requestEditTextVo){
        ResponseVo responseVo = gptService.editText(requestEditTextVo);

        return responseVo;
    }

    @RequestMapping(value = "/make/images", method = RequestMethod.GET)
    public ResponseVo makeImages(@Valid RequestQuestionVo requestQuestionVo){
        ResponseVo responseVo = gptService.makeImages(requestQuestionVo);

        return responseVo;
    }

    @GetMapping("gpt/model/list")
    public ResponseVo getModelList(){
        ResponseVo responseVo = gptService.getGPTModels();

        return responseVo;
    }

}