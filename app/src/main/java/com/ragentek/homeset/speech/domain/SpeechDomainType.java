package com.ragentek.homeset.speech.domain;

public enum SpeechDomainType {

    NULL(0, "noDomain", "noSubDomain", "空的场景类型", SpeechBaseDomain.class),
    MUSIC(1, "music", NULL.getSubType(), "我要听音乐; 我要听歌", SpeechMusicDomain.class),
    MUSIC_PLAYER(2, "musicPlayer_smartHome", NULL.getSubType(), "上一首; 下一首; 播放; 暂停", SpeechMusicPlayerDomain.class),
    TELEPHONE(3, "telephone", NULL.getSubType(), "我要打电话", SpeechTelephoneDomain.class),
    WEATHER(4, "weather", NULL.getSubType(), "今天的天气", SpeechWeatherDomain.class),

    /** Common questions and answers */
    COMMONQA(100, "commonQA", NULL.getSubType(), "通用问答库", SpeechCommonQADomain.class),
    CALC(COMMONQA.getCode() + 1, "calc", NULL.getSubType(), "五加五等于几", SpeechCommonQADomain.class),
    DATETIME(COMMONQA.getCode() + 2, "datetime", NULL.getSubType(), "今天几号了", SpeechCommonQADomain.class),
    BAIKE(COMMONQA.getCode() + 3, "baike", NULL.getSubType(), "中秋节", SpeechCommonQADomain.class),
    CHAT(COMMONQA.getCode() + 4, "chat", NULL.getSubType(), "你叫什么名字", SpeechCommonQADomain.class),

    /** OpenQA type, which has subtype */
    OPENQA(200, "openQA", NULL.getSubType(), "私有问答库", SpeechCommonQADomain.class),
    HOMESET_FAVORITE(OPENQA.getCode() + 1, "openQA", "homeset_favorite", "我的最爱; 我喜欢的; 播放我的最爱; 播放我喜欢的", SpeechHomesetDomain.class),
    HOMESET_CROSSTALK(OPENQA.getCode() + 2, "openQA", "homeset_crosstalk", "我要听相声", SpeechHomesetDomain.class),
    HOMESET_OPERA(OPENQA.getCode() + 3, "openQA", "homeset_opera", "我要听曲艺; 我要听戏曲", SpeechHomesetDomain.class),
    HOMESET_STROY(OPENQA.getCode() + 4, "openQA", "homeset_story", "我要听书; 我要听故事", SpeechHomesetDomain.class),
    HOMESET_HEALTH(OPENQA.getCode() + 5, "openQA", "homeset_health", "我要听健康", SpeechHomesetDomain.class),
    HOMESET_FINACE(OPENQA.getCode() + 6, "openQA", "homeset_finance", "我要听财经", SpeechHomesetDomain.class),
    HOMESET_HISTORY(OPENQA.getCode() + 7, "openQA", "homeset_history", "我要听历史", SpeechHomesetDomain.class),
    HOMESET_RADIO(OPENQA.getCode() + 8, "openQA", "homeset_radio", "我要听广播; 我要听FM; 我要听收音机", SpeechHomesetDomain.class);

    private int code;
    private final String type;
    private final String subType;
    private final String description;
    private final Class<SpeechBaseDomain> jsonClass;

    SpeechDomainType(int code, String type, String subType, String description, Class jsonClass) {
        this.code = code;
        this.type = type;
        this.subType = subType;
        this.description = description;
        this.jsonClass = jsonClass;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getAllType() {
        return type + subType;
    }

    public Class<SpeechBaseDomain> getJsonClass() {
        return jsonClass;
    }
}
