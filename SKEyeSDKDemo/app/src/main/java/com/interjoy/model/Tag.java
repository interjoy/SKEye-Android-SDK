package com.interjoy.model;

/**
 * Tag实体类
 *
 * @author wangcan  Interjoy
 */
public class Tag {
    private String tag;// 标签名
    private int confidence;// 置信度

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "Tag [tag=" + tag + ", confidence=" + confidence + "]";
    }

}
