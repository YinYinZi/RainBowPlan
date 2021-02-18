package org.rainbow.core.log.entity;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志DTO
 *
 * @author K
 * @date 2021/2/9  9:40
 */
public class OptLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestIp;
    private String trace;
    private String type;
    private String userName;
    private String description;
    private String classPath;
    private String actionMethod;
    private String requestUri;
    private String httpMethod;
    private String params;
    private String result;
    private String exDetail;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private Long consumingTime;
    private String ua;
    private Long createBy;

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getActionMethod() {
        return actionMethod;
    }

    public void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getExDetail() {
        return exDetail;
    }

    public void setExDetail(String exDetail) {
        this.exDetail = exDetail;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public Long getConsumingTime() {
        return consumingTime;
    }

    public void setConsumingTime(Long consumingTime) {
        this.consumingTime = consumingTime;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public OptLogDTO() {

    }

    public OptLogDTO(String requestIp, String trace, String type, String userName, String description, String classPath, String actionMethod, String requestUri, String httpMethod, String params, String result, String exDetail, LocalDateTime startTime, LocalDateTime finishTime, Long consumingTime, String ua, Long createBy) {
        this.requestIp = requestIp;
        this.trace = trace;
        this.type = type;
        this.userName = userName;
        this.description = description;
        this.classPath = classPath;
        this.actionMethod = actionMethod;
        this.requestUri = requestUri;
        this.httpMethod = httpMethod;
        this.params = params;
        this.result = result;
        this.exDetail = exDetail;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.consumingTime = consumingTime;
        this.ua = ua;
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return "OptLogDTO{" +
                "requestIp='" + requestIp + '\'' +
                ", trace='" + trace + '\'' +
                ", type='" + type + '\'' +
                ", userName='" + userName + '\'' +
                ", description='" + description + '\'' +
                ", classPath='" + classPath + '\'' +
                ", actionMethod='" + actionMethod + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", params='" + params + '\'' +
                ", result='" + result + '\'' +
                ", exDetail='" + exDetail + '\'' +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", consumingTime=" + consumingTime +
                ", ua='" + ua + '\'' +
                ", createBy=" + createBy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        OptLogDTO optLogDTO = (OptLogDTO) o;
        return Objects.equal(requestIp, optLogDTO.requestIp) &&
                Objects.equal(trace, optLogDTO.trace) &&
                Objects.equal(type, optLogDTO.type) &&
                Objects.equal(userName, optLogDTO.userName) &&
                Objects.equal(description, optLogDTO.description) &&
                Objects.equal(classPath, optLogDTO.classPath) &&
                Objects.equal(actionMethod, optLogDTO.actionMethod) &&
                Objects.equal(requestUri, optLogDTO.requestUri) &&
                Objects.equal(httpMethod, optLogDTO.httpMethod) &&
                Objects.equal(params, optLogDTO.params) &&
                Objects.equal(result, optLogDTO.result) &&
                Objects.equal(exDetail, optLogDTO.exDetail) &&
                Objects.equal(startTime, optLogDTO.startTime) &&
                Objects.equal(finishTime, optLogDTO.finishTime) &&
                Objects.equal(consumingTime, optLogDTO.consumingTime) &&
                Objects.equal(ua, optLogDTO.ua) &&
                Objects.equal(createBy, optLogDTO.createBy);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(requestIp, trace, type, userName, description, classPath, actionMethod, requestUri, httpMethod, params, result, exDetail, startTime, finishTime, consumingTime, ua, createBy);
    }
}
