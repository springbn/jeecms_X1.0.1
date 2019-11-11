package com.jeecms.backup.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jeecms.backup.domain.DatabaseBackupRecord;
import com.jeecms.backup.domain.dto.CompleteDto;
import com.jeecms.backup.service.DatabaseBackupRecordService;
import com.jeecms.backup.service.DatabaseBackupService;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.exception.IllegalParamExceptionInfo;
import com.jeecms.common.exception.NotFundExceptionInfo;
import com.jeecms.common.exception.SystemExceptionInfo;
import com.jeecms.common.response.ResponseInfo;
import com.jeecms.common.util.TransportUtil;
import com.jeecms.common.web.util.HttpClientUtil;
import com.jeecms.system.domain.CmsSite;
import com.jeecms.system.domain.GlobalConfig;
import com.jeecms.system.service.CmsSiteService;
import com.jeecms.system.service.GlobalConfigService;
import com.jeecms.util.JdbcUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/8/2 16:28
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Service
public class DatabaseBackupServiceImpl implements DatabaseBackupService {

    private static Logger log = LoggerFactory.getLogger(DatabaseBackupServiceImpl.class);

    private static final int DB_BACKUP_SERVER_PORT = 19889;


    /**
     * 在启动时把所有在还原中或备份中的状态改为还原失败或备份失败
     *
     * @author Zhu Kaixiao
     * @date 2019/8/6 9:50
     **/
    @PostConstruct
    private void fixLostState() {
        databaseBackupRecordService.fixLostState();
    }



    @Override
    public ResponseInfo backup(Integer siteId) throws GlobalException {
        return backup(siteId, null);
    }


    @Override
    public ResponseInfo backup(Integer siteId, String remark) throws GlobalException {

        if (!validDbDriverClass()) {
            throw new GlobalException(new SystemExceptionInfo("数据备份服务不支持当前数据库", (Object) "数据备份服务不支持当前数据库"));
        }

        // com.microsoft.sqlserver.jdbc.SQLServerDriver
        // oracle.jdbc.driver.OracleDriver
        // com.mysql.jdbc.Driver
        dataSourceProperties.getDriverClassName();

        // 调用DB服务器上的备份接口
        HashMap<String, Object> contentMap = new HashMap<>(5);
        contentMap.put("jdbcUrl", dataSourceProperties.getUrl());
        contentMap.put("username", dataSourceProperties.getUsername());
        contentMap.put("password", dataSourceProperties.getPassword());
        String baseUrl = getBaseUrl(siteId);
        contentMap.put("callbackUrl", baseUrl + "/databaseBackup/backupComplete");
//        contentMap.put("jdbcUrl", "jdbc:oracle:thin:@192.168.0.180:1521/ORCL.TEST");
//        contentMap.put("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test");
//        contentMap.put("jdbcUrl", "jdbc:sqlserver://192.168.0.2:1433;DatabaseName=zkx_backup_test");
//        contentMap.put("username", "root");
//        contentMap.put("password", "123456");

        Map<String, Object> dataSourceMap = JdbcUtil.resolveJdbcUrl((String) contentMap.get("jdbcUrl"));

        // 写入备份记录表
        DatabaseBackupRecord backupRecord = new DatabaseBackupRecord();
        backupRecord.setDatabaseName((String) dataSourceMap.get("databaseName"));
        backupRecord.setDataSourceIp((String) dataSourceMap.get("host"));
        backupRecord.setDataSourcePort((Integer) dataSourceMap.get("port"));
        backupRecord.setState(DatabaseBackupRecord.IN_BACKUP);
        backupRecord.setRemark(remark);
        databaseBackupRecordService.save(backupRecord);
        contentMap.put("backupId", backupRecord.getId());


        String url = String.format("http://%s:%d/jeecms-backup/backup", dataSourceMap.get("host"), DB_BACKUP_SERVER_PORT);
        // 从jdbcurl中提取ip  使用固定端口
        String backupRetStr = null;
        try {
            backupRetStr = HttpClientUtil.postJson(url, contentMap);
        } catch (IOException e) {
            log.error("调用备份接口失败");
            throw new GlobalException(new NotFundExceptionInfo(null, (Object) "连接数据备份服务失败"));
        }
        JSONObject backupRet = JSONObject.parseObject(backupRetStr);
        if (backupRet == null) {
            throw new GlobalException(new NotFundExceptionInfo(null, (Object) "连接数据备份服务失败, 当前ip不在数据备份服务的ip白名单中"));
        }
        Integer status = backupRet.getInteger("status");
        if (!Objects.equals(status, 200)) {
            // 备份失败
            log.error("数据库备份失败, url:[{}], dataSource:[{}], message:[{}]", url, contentMap, backupRet.getString("message"));
            backupRecord.setState(DatabaseBackupRecord.ERROR_BACKUP);
            backupRecord.setErrMsg(backupRet.getString("message"));
            databaseBackupRecordService.update(backupRecord);
            throw new RuntimeException(backupRet.getString("message"));
        }

        log.debug("备份任务启动成功");
        return new ResponseInfo();
    }

    @Override
    public ResponseInfo recovery(int backupId, Integer siteId) throws GlobalException {
        if (!validDbDriverClass()) {
            throw new GlobalException(new SystemExceptionInfo("数据备份服务不支持当前数据库", (Object) "数据备份服务不支持当前数据库"));
        }
        DatabaseBackupRecord backupRecord = databaseBackupRecordService.get(backupId);

        if (backupRecord.getState() == DatabaseBackupRecord.IN_BACKUP) {
            throw new GlobalException(new IllegalParamExceptionInfo(null, "当前备份记录正在备份中, 请稍后再试"));
        } else if (backupRecord.getState() == DatabaseBackupRecord.IN_RECOVERY) {
            throw new GlobalException(new IllegalParamExceptionInfo(null, "当前备份记录正在还原中, 请稍后再试"));
        }

        HashMap<String, Object> contentMap = new HashMap<>(6);
        contentMap.put("backupId", backupRecord.getId());
        contentMap.put("bakFilePath", backupRecord.getDbBakPath());
        contentMap.put("jdbcUrl", dataSourceProperties.getUrl());
        contentMap.put("username", dataSourceProperties.getUsername());
        contentMap.put("password", dataSourceProperties.getPassword());
        String baseUrl = getBaseUrl(siteId);
        contentMap.put("callbackUrl", baseUrl + "/databaseBackup/recoveryComplete");
//        contentMap.put("jdbcUrl", "jdbc:oracle:thin:@192.168.0.222:1521:orcl");
//        contentMap.put("jdbcUrl", "jdbc:oracle:thin:@192.168.0.180:1521/ORCL.TEST");
//        contentMap.put("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/test");
//        contentMap.put("jdbcUrl", "jdbc:sqlserver://192.168.0.2:1433;DatabaseName=zkx_backup_test");
//        contentMap.put("username", "zkx");
//        contentMap.put("password", "123456");


        backupRecord.setState(DatabaseBackupRecord.IN_RECOVERY);
        databaseBackupRecordService.update(backupRecord);

        Map<String, Object> dataSourceMap = JdbcUtil.resolveJdbcUrl((String) contentMap.get("jdbcUrl"));
        String url = String.format("http://%s:%d/jeecms-backup/recovery", dataSourceMap.get("host"), DB_BACKUP_SERVER_PORT);
        String backupRetStr;
        try {
            backupRetStr = HttpClientUtil.postJson(url, contentMap);
        } catch (IOException e) {
            log.error("调用还原接口失败");
            throw new GlobalException(new NotFundExceptionInfo(null, (Object) "连接数据备份服务失败"));
        }
        JSONObject backupRet = JSONObject.parseObject(backupRetStr);
        if (backupRet == null) {
            throw new GlobalException(new NotFundExceptionInfo(null, (Object) "连接数据备份服务失败, 当前ip不在数据备份服务的ip白名单中"));
        }
        Integer status = backupRet.getInteger("status");
        if (!Objects.equals(status, 200)) {
            // 还原失败
            log.error("数据库还原失败, url:[{}], dataSource:[{}], message:[{}]", url, contentMap, backupRet.getString("message"));
            backupRecord.setState(DatabaseBackupRecord.ERROR_RECOVERY);
            backupRecord.setErrMsg(backupRet.getString("message"));
            databaseBackupRecordService.update(backupRecord);
            throw new RuntimeException(backupRet.getString("message"));
        }

        log.debug("还原任务启动成功");
        return new ResponseInfo();
    }



    @Override
    public void onBackupComplete(CompleteDto dto) throws GlobalException {
        DatabaseBackupRecord backupRecord = databaseBackupRecordService.get(dto.getBackupId());
        // 状态不在备份中, 直接忽略
        if (backupRecord.getState() != DatabaseBackupRecord.IN_BACKUP) {
            return;
        }
        log.info("数据库备份{}", dto.getSuccess() ? "成功" : "失败");
        if (dto.getSuccess()) {
            backupRecord.setDbBakPath(dto.getBakFilePath());
            backupRecord.setFileSize(dto.getFileSize());
            backupRecord.setErrMsg("");
            backupRecord.setState(DatabaseBackupRecord.FINISH_BACKUP);
        } else {
            backupRecord.setState(DatabaseBackupRecord.ERROR_BACKUP);
            backupRecord.setErrMsg(dto.getErrMsg());
        }
        databaseBackupRecordService.update(backupRecord);
    }

    @Override
    public void onRecoveryComplete(CompleteDto dto) throws GlobalException {
        DatabaseBackupRecord backupRecord = databaseBackupRecordService.get(dto.getBackupId());
        // 状态不在还原中, 直接忽略
        if (backupRecord.getState() != DatabaseBackupRecord.IN_RECOVERY) {
            return;
        }
        log.info("数据库还原{}", dto.getSuccess() ? "成功" : "失败");
        if (dto.getSuccess()) {
            backupRecord.setState(DatabaseBackupRecord.FINISH_RECOVERY);
            backupRecord.setErrMsg("");
        } else {
            backupRecord.setState(DatabaseBackupRecord.ERROR_RECOVERY);
            backupRecord.setErrMsg(dto.getErrMsg());
        }
        databaseBackupRecordService.update(backupRecord);
    }


    @Override
    public File download(int backupId) throws IOException, GlobalException {
        DatabaseBackupRecord backupRecord = databaseBackupRecordService.get(backupId);
        File bakFile;

        if (backupRecord.getState() == DatabaseBackupRecord.IN_BACKUP) {
            throw new GlobalException(new IllegalParamExceptionInfo("正在备份中", null));
        } else if (backupRecord.getState() == DatabaseBackupRecord.ERROR_BACKUP) {
            throw new GlobalException(new IllegalParamExceptionInfo("备份已失败, 无法下载文件", null));
        }

        if (StringUtils.isNotBlank(backupRecord.getAppBakPath())) {
            bakFile = new File(backupRecord.getDbBakPath());
            if (bakFile.exists()) {
                return bakFile;
            }
        }

        bakFile = new File(backupRecord.getDbBakPath());
        // 如果应用程序和数据库备份程序部署在同一台服务器上,
        // 那么db服务器上的备份文件路径就是当前服务器上的路径
        if (!bakFile.exists()) {
            Map<String, Object> dataSourceMap = JdbcUtil.resolveJdbcUrl(dataSourceProperties.getUrl());
//            Map<String, Object> dataSourceMap = JdbcUtil.resolveJdbcUrl("jdbc:oracle:thin:@192.168.0.180:1521:orcl");
            String urlStr = String.format("http://%s:%d/jeecms-backup/download?filepath=%s",
                    dataSourceMap.get("host"), DB_BACKUP_SERVER_PORT, backupRecord.getDbBakPath());
            bakFile = TransportUtil.downloadFromUrl(urlStr,
                    "./database-backup/" + FilenameUtils.getName(backupRecord.getDbBakPath()));
        }

        backupRecord.setAppBakPath(bakFile.getCanonicalPath().replaceAll("\\\\", "/"));
        databaseBackupRecordService.update(backupRecord);
        return bakFile;
    }


    @Override
    public void deleteBackup(int backupId) throws GlobalException {
        DatabaseBackupRecord backupRecord = databaseBackupRecordService.get(backupId);
        if (backupRecord.getState() == DatabaseBackupRecord.IN_BACKUP) {
            throw new GlobalException(new IllegalParamExceptionInfo("正在备份中, 无法删除", null));
        } else if (backupRecord.getState() == DatabaseBackupRecord.IN_RECOVERY) {
            throw new GlobalException(new IllegalParamExceptionInfo("正在还原中, 无法删除", null));
        }
        File bakFile;
        // 删除文件
        if (StringUtils.isNotBlank(backupRecord.getAppBakPath())) {
            bakFile = new File(backupRecord.getDbBakPath());
            if (bakFile.exists()) {
                bakFile.delete();
            }
        }

        if (StringUtils.isNotBlank(backupRecord.getDbBakPath())) {
            bakFile = new File(backupRecord.getDbBakPath());
            // 如果应用程序和数据库备份程序部署在同一台服务器上,
            // 那么db服务器上的备份文件路径就是当前服务器上的路径
            if (!bakFile.exists()) {
                Map<String, Object> dataSourceMap = JdbcUtil.resolveJdbcUrl(dataSourceProperties.getUrl());
                String urlStr = String.format("http://%s:%d/jeecms-backup/deleteFile?filepath=%s",
                        dataSourceMap.get("host"), DB_BACKUP_SERVER_PORT, backupRecord.getDbBakPath());
                HttpClientUtil.get(urlStr);
            } else {
                bakFile.delete();
            }
        }

        // 删除记录
        databaseBackupRecordService.delete(backupId);
    }


    /**
     * 从配置表中取出当前服务的 协议,域名和端口
     *
     * @param siteId 站点id
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/8/15 10:51
     **/
    private String getBaseUrl(Integer siteId) throws GlobalException {
        CmsSite cmsSite = cmsSiteService.get(siteId);
        GlobalConfig globalConfig = globalConfigService.get();
        String baseUrl = cmsSite.getProtocol() + cmsSite.getDomain() + ":" + globalConfig.getConfigAttr().getServerPort();
        return baseUrl;
    }

    /**
     * 判断数据库备份服务是否支持当前数据库版本
     *
     * @return boolean 支持返回true, 否则返回false
     * @author Zhu Kaixiao
     * @date 2019/9/24 13:49
     **/
    private boolean validDbDriverClass() {
        String driverClassName = dataSourceProperties.getDriverClassName().toLowerCase();
        return driverClassName.contains("mysql")
                || driverClassName.contains("oracle")
                || driverClassName.contains("sqlserver");
    }

    @Autowired
    private CmsSiteService cmsSiteService;
    @Autowired
    private GlobalConfigService globalConfigService;
    @Autowired
    private DataSourceProperties dataSourceProperties;
    @Autowired
    private DatabaseBackupRecordService databaseBackupRecordService;
}
