package com.dosola.core.common;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * 利用freemarker构建模板工具类
 * @author june
 *         15/3/27 12:11
 */
public class TemplateBuilderUtil {
    private static String encoding = "utf-8";
    //模板存放目录
    private static String xmlFtlPath = "/ftl";

    public static String build(String ftlName,Map valueMap) {

        try {
            Configuration config = initFreeMarkerConfiguration();
            //获得模板
            Template template = config.getTemplate(ftlName,encoding);
            StringWriter sw = new StringWriter();
            template.process(valueMap, sw);
            return sw.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Configuration initFreeMarkerConfiguration() throws IOException {

        Configuration config = new Configuration();

        config.setDirectoryForTemplateLoading(new File(TemplateBuilderUtil.class.getResource("/").getPath()+xmlFtlPath));

        // 模板缓存更新时间
        config.setTemplateUpdateDelay(300);
        // - Set an error handler that prints errors so they are readable with
        //   a HTML browser.
        // config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // - Use beans wrapper (recommmended for most applications)
        config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        // - Set the default charset of the template files
        config.setDefaultEncoding(encoding);		// config.setDefaultEncoding("ISO-8859-1");
        // - Set the charset of the output. This is actually just a hint, that
        //   templates may require for URL encoding and for generating META element
        //   that uses http-equiv="Content-type".
        config.setOutputEncoding(encoding);			// config.setOutputEncoding("UTF-8");
        // - Set the default locale
        config.setLocale(Locale.getDefault() /* Locale.CHINA */ );		// config.setLocale(Locale.US);
        config.setLocalizedLookup(false);

        // 去掉int型输出时的逗号, 例如: 123,456
        // config.setNumberFormat("#");		// config.setNumberFormat("0"); 也可以
        config.setNumberFormat("#0.#####");
        config.setDateFormat("yyyy/MM/dd");
        config.setTimeFormat("HH:mm:ss");
        config.setDateTimeFormat("yyyy/MM/dd HH:mm:ss");
        return config;
    }
}
