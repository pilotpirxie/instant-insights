package com.instantinsights.api.repositories;

import com.instantinsights.api.entities.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class AdvancedEventRepository implements QueryEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> queryEvents(
        String typeName,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        String appName,
        int limit,
        UUID cursor,
        boolean descending
    ) {

        String sortOrder = descending ? "DESC" : "ASC";
        String cursorOperator = descending ? "<" : ">";
        String jsonbQueryPart = getJsonbQueryPart(params);

        String queryStr = "SELECT e FROM Event e WHERE "
            + "e.eventType.name = :typeName "
            + "AND e.eventTime BETWEEN :start AND :end "
            + "AND e.app.name = :appName "
            + jsonbQueryPart
            + "AND (:cursor IS NULL OR e.id " + cursorOperator + " :cursor) "
            + "ORDER BY e.eventTime " + sortOrder;

        TypedQuery<Event> query = entityManager.createQuery(queryStr, Event.class);
        query.setParameter("typeName", typeName);
        query.setParameter("start", start);
        query.setParameter("end", end);
        query.setParameter("appName", appName);
        query.setParameter("limit", limit);
        query.setParameter("cursor", cursor);

        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long countEvents(
        String eventType,
        LocalDateTime start,
        LocalDateTime end,
        Map<String, String> params,
        String appName
    ) {
        String jsonbQueryPart = getJsonbQueryPart(params);

        String queryStr = "SELECT COUNT(e) FROM Event e WHERE "
            + "e.eventType.name = :typeName "
            + "AND e.eventTime BETWEEN :start AND :end "
            + "AND e.app.name = :appName "
            + jsonbQueryPart;

        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        query.setParameter("typeName", eventType);
        query.setParameter("start", start);
        query.setParameter("end", end);
        query.setParameter("appName", appName);

        return query.getSingleResult();
    }

    private static String getJsonbQueryPart(Map<String, String> params) {
        StringBuilder jsonbQueryPart = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            jsonbQueryPart
                .append("AND (e.params ->> '")
                .append(entry.getKey())
                .append("') = '")
                .append(entry.getValue())
                .append("' ");
        }
        return jsonbQueryPart.toString();
    }
}
