package lomtev.dev.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateProperties {
    private final String showSql;
    private final String formatSql;
    private final String highlightSql;
    private final String hbm2ddlAuto;

    public HibernateProperties(
            @Value("${hibernate.show_sql}") String showSql,
            @Value("${hibernate.format_sql}") String formatSql,
            @Value("${hibernate.highlight_sql}") String highlightSql,
            @Value("${hibernate.hbm2ddl.auto}") String hbm2ddlAuto
    ) {
        this.showSql = showSql;
        this.formatSql = formatSql;
        this.highlightSql = highlightSql;
        this.hbm2ddlAuto = hbm2ddlAuto;
    }

    public String getShowSql() {
        return showSql;
    }

    public String getFormatSql() {
        return formatSql;
    }

    public String getHighlightSql() {
        return highlightSql;
    }

    public String getHbm2ddlAuto() {
        return hbm2ddlAuto;
    }
}
