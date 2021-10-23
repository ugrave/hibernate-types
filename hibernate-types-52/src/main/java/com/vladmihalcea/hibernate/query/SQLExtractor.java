package com.vladmihalcea.hibernate.query;

import org.hibernate.query.internal.AbstractProducedQuery;

import javax.persistence.Query;
import java.util.Collections;

/**
 * The {@link SQLExtractor} allows you to extract the
 * underlying SQL query generated by a JPQL or JPA Criteria API query.
 * <p>
 * For more details about how to use it, check out <a href="https://vladmihalcea.com/get-sql-from-jpql-or-criteria/">this article</a> on <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
 *
 * @author Vlad Mihalcea
 * @since 2.9.11
 */
public class SQLExtractor {

    protected SQLExtractor() {
    }

    /**
     * Get the underlying SQL generated by the provided JPA query.
     *
     * @param query JPA query
     * @return the underlying SQL generated by the provided JPA query
     */
    public static String from(Query query) {
        AbstractProducedQuery abstractProducedQuery = query.unwrap(AbstractProducedQuery.class);
        String[] sqls = abstractProducedQuery
            .getProducer()
            .getFactory()
            .getQueryPlanCache()
            .getHQLQueryPlan(abstractProducedQuery.getQueryString(), false, Collections.emptyMap())
            .getSqlStrings();

        return sqls.length > 0 ? sqls[0] : null;
    }
}
