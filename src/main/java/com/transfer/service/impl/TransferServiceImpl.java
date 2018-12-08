package com.transfer.service.impl;

import com.transfer.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class TransferServiceImpl implements TransferService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Resource(name = "sourceJdbcTemplate")
    JdbcTemplate sourceJdbcTemplate;

    @Resource(name = "targetJdbcTemplate")
    JdbcTemplate targetJdbcTemplate;
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(30, 30, 5, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    @PostConstruct
    @Override
    public void startTransfer() {
        String queryTableSql = "show tables";
        List<Map<String, Object>> mapList = sourceJdbcTemplate.queryForList(queryTableSql);
        for (Map<String, Object> tableMap : mapList) {
            for (Object value : tableMap.values()) {
                String countSql = "select count(*) from " + value;
                Integer totalCount = sourceJdbcTemplate.queryForObject(countSql, Integer.class);
                int pageSize = 2000;
                int pageCount = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
                for (int t = 0; t < pageCount; t++) {
                    int pageIndex = t;
                    threadPoolExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            String querySql = "select * from " + value + "  limit " + (pageIndex * pageSize) + ", " + pageSize;
                            List<Map<String, Object>> pageData = sourceJdbcTemplate.queryForList(querySql);
                            StringBuilder insertSqlBuilder = new StringBuilder("insert into " + value + "( ");
                            StringBuilder valuesBuilder = new StringBuilder("(");
                            for (String key : pageData.get(0).keySet()) {
                                insertSqlBuilder.append(key).append(",");
                                valuesBuilder.append("?,");
                            }
                            String insertSql = insertSqlBuilder.substring(0, insertSqlBuilder.length() - 1) + ") values " + valuesBuilder.substring(0, valuesBuilder.length() - 1) + ")";
                            List<Object[]> paramsList = new CopyOnWriteArrayList<>();
                            pageData.stream().parallel().forEach(dataMap -> {
                                List<Object> params = new ArrayList<>();
                                for (Map.Entry<String, Object> dataEntry : dataMap.entrySet()) {
                                    params.add(dataEntry.getValue());
                                }
                                paramsList.add(params.toArray());

                            });
                            targetJdbcTemplate.batchUpdate(insertSql, paramsList);
                            logger.info("insert table {},pageIndex={},totalPage={}", value, pageIndex,pageCount);
                        }
                    });
                }
            }
        }

    }
}
