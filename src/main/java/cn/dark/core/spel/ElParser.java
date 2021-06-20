package cn.dark.core.spel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

/**
 * @author dark
 * @date 2021-01-30
 */
@Slf4j
public class ElParser {

    private static ExpressionParser parser = new SpelExpressionParser();

    /**
     * 解析el表达式，动态获取参数的值作为key
     *
     * @param key el表达式，for example: 非对象参数用"#param"；获取对象参数的属性值用“#student.age”
     * @param paramNames 参数名称
     * @param args 参数值
     * @return java.lang.String
     * @date 2021-01-30
     *
     */
    public static String getKeyByParameter(String key, String[] paramNames, Object[] args) {
        if (paramNames == null
                || paramNames.length == 0
                || args == null
                || args.length == 0) {
            if (StringUtils.isEmpty(key)) {
                throw new IllegalArgumentException("Can't get key, the method don't have arguments");
            }

            return null;
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        Expression expression = parser.parseExpression(key);
        return expression.getValue(context, String.class);
    }

}
