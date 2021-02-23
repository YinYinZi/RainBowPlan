package org.rainbow.notice.content;

import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 异常信息数据model
 *
 * @author K
 * @date 2021/2/22  18:42
 */
@Data
public class ExceptionInfo {

    /**
     * 工程名
     */
    private String project;

    /**
     * 异常标记码
     */
    private String uid;

    /**
     * 请求地址
     */
    private String reqAddress;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数信息
     */
    private Object params;

    /**
     * 类路径
     */
    private String classPath;

    /**
     * 异常信息
     */
    private String exceptionMessage;

    /**
     * 异常追踪信息
     */
    private List<String> traceInfo = new ArrayList<>();

    /**
     * 最后一次出现时间
     */
    private LocalDateTime latestShowTime = LocalDateTime.now();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExceptionInfo(Throwable ex, String methodName, String filterTrace, Object args, String reqAddress) {
        this.exceptionMessage = gainExceptionMessage(ex);
        this.reqAddress = reqAddress;
        this.params = args;
        List<StackTraceElement> list = Arrays.stream(ex.getStackTrace())
                .filter(x -> filterTrace == null || x.getClassName().startsWith(filterTrace))
                .filter(x -> !"<generated>".equals(x.getFileName())).collect(Collectors.toList());
        if (list.size() > 0) {
            this.traceInfo = list.stream().map(StackTraceElement::toString).collect(Collectors.toList());
            this.methodName = null == methodName ? list.get(0).getMethodName() : methodName;
            this.classPath = list.get(0).getClassName();
        }
        this.uid = calUid();
    }
    
    /**
     * 获取异常信息
     *
     * @param exception 异常
     * @return 异常信息
     */
    private String gainExceptionMessage(Throwable exception) {
        String em = exception.toString();
        if (exception.getCause() != null) {
            em = String.format("%s\r\n\tcaused by : %s", em, gainExceptionMessage(exception.getCause()));
        }
        return em;
    }

    /**
     * 生成异常id
     *
     * @return 异常id
     */
    private String calUid() {
        return DigestUtils.md5DigestAsHex(String.format("%s-%s", exceptionMessage, traceInfo.size() > 0 ? traceInfo.get(0) : "").getBytes());
    }

    @SneakyThrows
    public String createText() {
        StringBuilder sb = new StringBuilder();
        sb.append("项目名称：").append(project).append("\n");
        sb.append("类路径：").append(classPath).append("\n");
        sb.append("请求路径：").append(reqAddress).append("\n");
        sb.append("方法名：").append(methodName).append("\n");
        if (params != null) {
            sb.append("方法参数").append(objectMapper.writeValueAsString(params)).append("\n");
        }
        sb.append("异常信息：").append("\n").append(exceptionMessage).append("\n");
        sb.append("异常追踪：").append("\n").append(String.join("\n", traceInfo)).append("\n");
        sb.append("最后一次出现的时间：").append(latestShowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return sb.toString();
    }

    @SneakyThrows
    public String createWeChatMarkDown() {
        StringBuilder sb = new StringBuilder();
        sb.append(">项目名称：<font color=\"info\">").append(project).append("</font>").append("\n");
        sb.append(">类路径：<font color=\"info\">").append(project).append("</font>").append("\n");
        sb.append(">请求地址：<font color=\"info\">").append(project).append("</font>").append("\n");
        sb.append(">方法名：<font color=\"info\">").append(project).append("</font>").append("\n");
        if (params != null) {
            sb.append(">方法参数：<font color=\"info\">").append(objectMapper.writeValueAsString(params)).append("</font>").append("\n");
        }
        sb.append(">异常信息：<font color=\"red\">").append("\n").append(exceptionMessage).append("</font>").append("\n");
        sb.append(">异常追踪：<font color=\"info\">").append("\n").append(String.join("\n", traceInfo)).append("</font>").append("\n");
        sb.append(">最后一次出现时间：<font color=\"info\">")
                .append(latestShowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</font>");
        return sb.toString();
    }

    @SneakyThrows
    public String createDingTalkMarkDown() {
        StringBuilder sb = new StringBuilder();
        sb.append("#### 项目名称：").append("\n").append("> ").append(project).append("\n");
        sb.append("#### 类路径：").append("\n").append("> ").append(classPath).append("\n");
        sb.append("#### 请求地址：").append("\n").append("> ").append(reqAddress).append("\n");
        sb.append("#### 方法名：").append("\n").append("> ").append(methodName).append("\n");
        if (params != null) {
            sb.append("#### 方法参数：").append(objectMapper.writeValueAsString(params)).append("\n");
        }
        sb.append("#### 异常信息：").append("\n").append("> ").append(exceptionMessage).append("\n");
        sb.append("#### 异常追踪：").append("\n").append("> ").append(String.join("\n", traceInfo)).append("\n");
        sb.append("#### 最后一次出现时间：")
                .append(latestShowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return sb.toString();
    }
}
