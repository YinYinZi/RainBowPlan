package org.rainbow.core.markdown;

import cn.hutool.core.convert.Convert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author K
 * @date 2021/2/18  14:33
 */
public class MarkdownBuilder {
    public static final String TITLE_PREFIX = "#";
    public static final String QUOTE_PREFIX = "> ";
    public static final String BOLD_PREFIX = "**";
    public static final String ITALIC_PREFIX = "*";
    public static final String UNORDERED_LIST_PREFIX = "- ";
    public static final String ORDER_LIST_PREFIX = ". ";
    private final List<String> content = new ArrayList();
    private StringBuilder lineTextBuilder = new StringBuilder();

    public MarkdownBuilder() {
    }

    public MarkdownBuilder append(String content) {
        this.lineTextBuilder.append(content);
        return this;
    }

    public MarkdownBuilder orderList(String content) {
        String tmp = "";
        if (this.content.size() != 0) {
            tmp = (String)this.content.get(this.content.size() - 1);
        }

        int index = 1;
        String isOrderListPattern = "^\\d\\. .*";
        if (Pattern.matches(isOrderListPattern, tmp)) {
            index = Convert.toInt(tmp.substring(0, tmp.indexOf(". ") - 1));
        }

        return this.orderList(index, content);
    }

    public MarkdownBuilder orderList(int index, String content) {
        this.lineBreak();
        this.lineTextBuilder.append(index).append(". ").append(content);
        return this;
    }

    public MarkdownBuilder unorderedList(String content) {
        this.lineBreak();
        this.lineTextBuilder.append("- ").append(content);
        return this;
    }

    public MarkdownBuilder pic(String url) {
        return this.pic("", url);
    }

    public MarkdownBuilder pic(String title, String url) {
        this.lineTextBuilder.append("![").append(title).append("](").append(url).append(")");
        return this;
    }

    public MarkdownBuilder link(String title, String url) {
        this.lineTextBuilder.append("[").append(title).append("](").append(url).append(")");
        return this;
    }

    public MarkdownBuilder italic(String content) {
        this.lineTextBuilder.append("*").append(content).append("*");
        return this;
    }

    public MarkdownBuilder bold(String content) {
        this.lineTextBuilder.append("**").append(content).append("**");
        return this;
    }

    public MarkdownBuilder quote(String content) {
        this.lineBreak();
        this.content.add("> " + content);
        return this;
    }

    public MarkdownBuilder quoteLineBreak(String content) {
        this.quote(content);
        return this.forceLineBreak();
    }

    public MarkdownBuilder forceLineBreak() {
        this.content.add(this.lineTextBuilder.toString());
        this.lineTextBuilder = new StringBuilder();
        return this;
    }

    public MarkdownBuilder lineBreak() {
        return this.lineTextBuilder.length() != 0 ? this.forceLineBreak() : this;
    }

    private MarkdownBuilder title(int i, String content) {
        this.lineBreak();

        for(int j = 0; j < i; ++j) {
            this.lineTextBuilder.append("#");
        }

        this.content.add(this.lineTextBuilder.append(" ").append(content).toString());
        this.lineTextBuilder = new StringBuilder();
        return this;
    }

    public MarkdownBuilder title1(String text) {
        return this.title(1, text);
    }

    public MarkdownBuilder title2(String text) {
        return this.title(2, text);
    }

    public MarkdownBuilder title3(String text) {
        return this.title(3, text);
    }

    public MarkdownBuilder title4(String text) {
        return this.title(4, text);
    }

    public MarkdownBuilder title5(String text) {
        return this.title(5, text);
    }

    @Override
    public String toString() {
        return this.build();
    }

    public String build() {
        this.lineBreak();
        StringBuilder res = new StringBuilder();
        this.content.forEach((content) -> {
            res.append(content).append(" \n");
        });
        return res.toString();
    }
}
