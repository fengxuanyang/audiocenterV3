package com.ragentek.homeset.speech.domain;

import com.alibaba.fastjson.JSON;
import static com.ragentek.homeset.speech.domain.SpeechDomainType.*;

public class SpeechDomainUtils {

    public static SpeechBaseDomain parseResult(String result) {
        SpeechBaseDomain baseDomain = JSON.parseObject(result, SpeechBaseDomain.class);
        SpeechDomainType domainType = getDomainType(baseDomain);
        return JSON.parseObject(result, domainType.getJsonClass());
    }

    public static SpeechDomainType getDomainType(SpeechBaseDomain speechDomain) {
        String service = speechDomain.service;
        for (SpeechDomainType domain: SpeechDomainType.values()) {
            if (domain.getType().equals(service)) {
                if (domain == OPENQA) {
                    return getOpenQADomain(speechDomain);
                }
                return domain;
            }
        }

        return NULL;
    }

    private static SpeechDomainType getOpenQADomain(SpeechBaseDomain speechDomain) {
        if (speechDomain instanceof SpeechCommonQADomain) {
            SpeechCommonQADomain commonQADomain = (SpeechCommonQADomain) speechDomain;
            String subType = commonQADomain.answer.text;

            for (SpeechDomainType domain: SpeechDomainType.values()) {
                if (domain.getSubType().equals(subType)) {
                    return domain;
                }
            }
        }

        return OPENQA;
    }
}
