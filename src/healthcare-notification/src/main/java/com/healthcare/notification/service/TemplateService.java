package com.healthcare.notification.service;

import com.healthcare.notification.domain.NotificationTemplate;
import com.healthcare.notification.exception.TemplateNotFoundException;
import com.healthcare.notification.repository.NotificationTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateService {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    private final NotificationTemplateRepository templateRepository;

    public TemplateService(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public NotificationTemplate getTemplate(String templateCode) {
        return templateRepository.findActiveByTemplateCode(templateCode)
            .orElseThrow(() -> TemplateNotFoundException.byCode(templateCode));
    }

    public String render(String template, Map<String, String> variables) {
        if (template == null || template.isBlank()) {
            return "";
        }
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            String replacement = variables.getOrDefault(variableName, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public String renderSubject(NotificationTemplate template, Map<String, String> variables) {
        return render(template.getSubjectTemplate(), variables);
    }

    public String renderBody(NotificationTemplate template, Map<String, String> variables) {
        return render(template.getBodyTemplate(), variables);
    }

    public String renderHtml(NotificationTemplate template, Map<String, String> variables) {
        return render(template.getHtmlTemplate(), variables);
    }
}
