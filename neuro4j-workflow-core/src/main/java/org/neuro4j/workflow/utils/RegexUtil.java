package org.neuro4j.workflow.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {

    private final static Logger logger = LoggerFactory.getLogger(RegexUtil.class);

    private RegexUtil() {

    }

    private static final String NOT_MATCHER_DATA = "regex not data found.";


    /**
     * 获得可匹配对象
     * @param input
     * @param regex
     * @return
     */
    private static Matcher getMatcher(String input, String regex) {
        Pattern defaultPattern = getPattern(regex);
        Matcher defaultMatcher = defaultPattern.matcher(input);
        return defaultMatcher;
    }

    /**
     * 获得模式对象
     * @param regex
     * @return
     */
    private static Pattern getPattern(String regex) {
        Pattern defaultPattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        return defaultPattern;
    }

    /**
     * 返回多行结果集
     * @param input
     * @param regex
     * @return
     */
    public static List<String> getStringList(String input, String regex,Integer group) {
        if (group == null) {
            group = 0;
        }
        List<String> resultList = new ArrayList<String>();
        Matcher defaultMatcher = getMatcher(input, regex);
        while (defaultMatcher.find()) {
            if (defaultMatcher.groupCount() >= group) {
                resultList.add(defaultMatcher.group(group).trim());
            }
        }
        if (resultList.size() < 1) {
            logger.error(NOT_MATCHER_DATA);
        }
        return resultList;
    }



    /**
     * 是否匹配表达式
     * @param input
     * @param regex
     * @return
     */
    public static boolean isMatch(String input,String regex){
        if (input == null){
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    /**
     * 字符串替换
     * @param input
     * @param regex
     * @param replacement
     * @return
     */
    public static String replaceAll(String input, String regex, String replacement) {
        if (input == null || replacement == null || regex == null) {
            throw new RuntimeException("input ,replacement must not null.");
        }
        return input.replaceAll(regex, replacement);
    }

    /**
     * 实现替换最后一个
     * @param input
     * @param regex
     * @param replacement
     * @return
     */
    @Deprecated
    public static String replaceLast(String input, String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            return input;
        }
        int lastMatchStart=0;
        do {
            lastMatchStart=matcher.start();
        } while (matcher.find());
        matcher.find(lastMatchStart);
        StringBuffer sb = new StringBuffer(input.length());
        matcher.appendReplacement(sb, replacement);
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 过滤文本中所有Html标签
     * @param inputString
     * @return
     */
    public static String html2Text(String inputString) {
        String htmlStr = inputString;
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>

            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>

            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }

    /**
     * 过滤出纯数字
     * @param s
     * @return
     */
    public static Long filterNumber(String s) {
        if (s == null) {
            return null;
        }
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(s);
        String result = m.replaceAll("").trim();
        if (result==null||"".equalsIgnoreCase(result.trim())) {
            return null;
        } else {
            return Long.valueOf(result);
        }
    }




    public static void main(String[] args) {
        /*
        String input = "<table><tr><td>1</td><td>九月十三</td></tr><tr><td>2</td><td>九月十四</td></tr></table>";
        input += "\\r\\n";
        input += "<table><tr><td>3</td><td>十月十三</td></tr><tr><td>4</td><td>十月十四</td></tr></table>";

        List<String> results = RegexUtil.getStringList(input, "<table>.*?</table>");

        Boolean match = RegexUtil.isMatch(input, "<table>.*?</table>");
        System.out.println(match);

        System.out.println(RegexUtil.replaceAll(input, "<table>.*?</table>", "{table}"));

        System.out.println(RegexUtil.replaceAll(input, "<table>(.*?)</table>", "$1"));
*/
        String html = "<tr height=\"35\" style=\"line-height:25px\">\n" +
                "\t\t\t\t\t    <td align=\"center\" width=\"620\">\n" +
                "\t\t\t\t\t      <div class=\"high\" align=\"left\"><b><font color=\"#0066cc\"><span class=\"high\">5.截至2015年09月30日，</span></font></b></div>\n" +
                "\t\t\t\t\t    \n" +
                "\t\t\t\t\t    </td>\n" +
                "\t\t\t\t\t  </tr>";

        html += "<tr><td>我是测试的中间文本，输出来就表示正确了</td></tr>";
        html += "<tr height=\"35\" style=\"line-height:25px\">\n" +
                "\t\t\t\t\t    <td align=\"center\" width=\"620\">\n" +
                "\t\t\t\t\t      <div class=\"high\" align=\"left\"><b><font color=\"#0066cc\"><span class=\"high\">6.截至2015年10月05日，账户状态为“结清”。\n" +
                "\t\t\t\t\t        </span></font></b></div>\n" +
                "\t\t\t\t\t        \n" +
                "\t\t\t\t\t    </td>\n" +
                "\t\t\t\t\t  </tr>";

        //html = "\t\n<tr><td>开始</td></tr><tr><td>\t\n牛逼</td></tr><tr><td>起来</td></tr>";
        String reg1 = "^<tr.*?>.*?5.截至2015年09月30日.*?</tr>([\\s\\S]*)<tr.*?>.*?6.截至2015年10月05日，.*?</tr>$";
        String reg3 = "^<tr.*?>.*?{column(i)}.*?</tr>([\\s\\S]*)<tr.*?>.*?{column(i+1)}.*?</tr>$";
        String reg2 = "<tr.*?>.*?开始.*?</tr>([\\s\\S]*)<tr.*?>.*?起来.*?</tr>";

        List<String> testRet = RegexUtil.getStringList(html, reg1,1);

        System.out.println(testRet.get(0));
    }
}