package com.ticket_pipeline.simple_exchange.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticket_pipeline.simple_utils.clean.Cleanable;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QWork implements Cleanable {
    private BigInteger id;//bigint(20)
    private Integer siteId;//  int(11)
    private Integer queueId;//  int(11)
    private BigInteger getId;//bigint(20)
    private String url;//  varchar(255)
    private Boolean allowReassign;//  tinyint
    private Integer procSpeed;//  int(11)
    private LocalDateTime procStartTime; // timestamp
    private Integer procType;//  tinyint
    private String user;//  varchar(64)
    private String pwd;// 	varchar(64)
    private Integer getQType;//  tinyint

    public BigInteger getId() {
        return id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public BigInteger getGetId() {
        return getId;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getAllowReassign() {
        return allowReassign;
    }

    public Integer getProcSpeed() {
        return procSpeed;
    }

    public LocalDateTime getProcStartTime() {
        return procStartTime;
    }

    public Integer getProcType() {
        return procType;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public Integer getGetQType() {
        return getQType;
    }

    public QWork setId(BigInteger id) {
        this.id = id;
        return this;
    }

    public QWork setSiteId(Integer siteId) {
        this.siteId = siteId;
        return this;
    }

    public QWork setQueueId(Integer queueId) {
        this.queueId = queueId;
        return this;
    }

    public QWork setGetId(BigInteger getId) {
        this.getId = getId;
        return this;
    }

    public QWork setUrl(String url) {
        this.url = url;
        return this;
    }

    public QWork setAllowReassign(Boolean allowReassign) {
        this.allowReassign = allowReassign;
        return this;
    }

    public QWork setProcSpeed(Integer procSpeed) {
        this.procSpeed = procSpeed;
        return this;
    }

    public QWork setProcStartTime(LocalDateTime procStartTime) {
        this.procStartTime = procStartTime;
        return this;
    }

    public QWork setProcType(Integer procType) {
        this.procType = procType;
        return this;
    }

    public QWork setUser(String user) {
        this.user = user;
        return this;
    }

    public QWork setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public QWork setGetQType(Integer getQType) {
        this.getQType = getQType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QWork qWork = (QWork) o;
        return Objects.equals(id, qWork.id) &&
                Objects.equals(siteId, qWork.siteId) &&
                Objects.equals(queueId, qWork.queueId) &&
                Objects.equals(getId, qWork.getId) &&
                Objects.equals(url, qWork.url) &&
                Objects.equals(allowReassign, qWork.allowReassign) &&
                Objects.equals(procSpeed, qWork.procSpeed) &&
                Objects.equals(procStartTime, qWork.procStartTime) &&
                Objects.equals(procType, qWork.procType) &&
                Objects.equals(user, qWork.user) &&
                Objects.equals(pwd, qWork.pwd) &&
                Objects.equals(getQType, qWork.getQType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, siteId, queueId, getId, url, allowReassign, procSpeed, procStartTime, procType, user, pwd, getQType);
    }

    @Override
    public void clean() {
        id = null;
        siteId = null;
        queueId = null;
        getId = null;
        url = null;
        allowReassign = null;
        procSpeed = null;
        procStartTime = null;
        procType = null;
        user = null;
        pwd = null;
        getQType = null;
    }

    @Override
    public String toString() {
        return "QWork{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", queueId=" + queueId +
                ", getId=" + getId +
                ", url='" + url + '\'' +
                ", allowReassign=" + allowReassign +
                ", procSpeed=" + procSpeed +
                ", procStartTime=" + procStartTime +
                ", procType=" + procType +
                ", user='" + user + '\'' +
                ", pwd='" + pwd + '\'' +
                ", getQType=" + getQType +
                '}';
    }
}