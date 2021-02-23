package org.rainbow.notice.process;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.notice.content.ExceptionInfo;
import org.rainbow.notice.properties.MailProperties;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 邮件异常消息通知具体实现
 *
 * @author K
 * @date 2021/2/23  10:38
 */
@Data
@Slf4j
public class MailNoticeProcessor implements INoticeProcessor {
    private final MailProperties mailProperties;

    private final MailSender mailSender;

    public MailNoticeProcessor(MailProperties mailProperties, MailSender mailSender) {
        Assert.hasText(mailProperties.getFrom(), "mail 'from' property must not be null");
        Assert.noNullElements(mailProperties.getTo(), "mail 'to' property must not be null");
        this.mailProperties = mailProperties;
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotice(ExceptionInfo exceptionInfo) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailProperties.getFrom());
        simpleMailMessage.setTo(mailProperties.getTo());
        if (!StringUtils.isEmpty(mailProperties.getCc())) {
            simpleMailMessage.setCc(mailProperties.getCc());
        }
        simpleMailMessage.setText(exceptionInfo.createText());
        simpleMailMessage.setSubject(String.format("来自%s项目的异常通知", exceptionInfo.getProject()));
        mailSender.send(simpleMailMessage);
    }
}
