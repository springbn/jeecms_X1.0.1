package com.jeecms.backup.domain;

import com.jeecms.common.base.domain.AbstractDomain;
import org.apache.commons.io.FilenameUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/8/3 10:03
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Entity
@Table(name = "jc_database_backup_record")
public class DatabaseBackupRecord extends AbstractDomain<Integer> implements Serializable {
    /**
     * 备份中
     */
    public static final int IN_BACKUP = 1;
    /**
     * 还原中
     */
    public static final int IN_RECOVERY = 2;
    /**
     * 备份完成
     */
    public static final int FINISH_BACKUP = 3;
    /**
     * 还原完成
     */
    public static final int FINISH_RECOVERY = 4;
    /**
     * 备份失败
     */
    public static final int ERROR_BACKUP = 5;
    /**
     * 还原失败
     */
    public static final int ERROR_RECOVERY = 6;

    private String dataSourceIp;
    private Integer dataSourcePort;
    private String databaseName;
    private String appBakPath;
    private String dbBakPath;
    private Long fileSize;
    private Integer state;
    private String remark;
    private String errMsg;


    @Id
    @Column(name = "id", nullable = false)
    @TableGenerator(name = "jc_database_backup_record", pkColumnValue = "jc_database_backup_record", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "jc_database_backup_record")
    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
    }

    @Basic
    @Column(name = "data_source_ip", nullable = false, length = 15)
    public String getDataSourceIp() {
        return dataSourceIp;
    }

    public void setDataSourceIp(String dataSourceIp) {
        this.dataSourceIp = dataSourceIp;
    }

    @Basic
    @Column(name = "data_source_port", nullable = false)
    public Integer getDataSourcePort() {
        return dataSourcePort;
    }

    public void setDataSourcePort(Integer dataSourcePort) {
        this.dataSourcePort = dataSourcePort;
    }

    @Basic
    @Column(name = "database_name", nullable = false, length = 60)
    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Basic
    @Column(name = "app_bak_path", nullable = true, length = 255)
    public String getAppBakPath() {
        return appBakPath;
    }

    public void setAppBakPath(String appBakPath) {
        this.appBakPath = appBakPath;
    }

    @Basic
    @Column(name = "db_bak_path", nullable = false, length = 255)
    public String getDbBakPath() {
        return dbBakPath;
    }

    public void setDbBakPath(String dbBakPath) {
        this.dbBakPath = dbBakPath;
    }

    @Basic
    @Column(name = "file_size")
    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Basic
    @Column(name = "state", nullable = false)
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Basic
    @Column(name = "remark", length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "err_msg", length = 255)
    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Transient
    public String getFilename() {
        return FilenameUtils.getName(Optional.ofNullable(getDbBakPath()).orElse(""));
    }

    @Transient
    public String getHumanFileSize() {
        if (fileSize == null) {
            return "";
        }
        double base;
        String suffix;
        if (fileSize > Math.pow(1024, 4)) {  // TB
            base = Math.pow(1024, 4);
            suffix = "TB";
        } else if (fileSize > Math.pow(1024, 3)) {  // GB
            base = Math.pow(1024, 3);
            suffix = "GB";
        } else if (fileSize > Math.pow(1024, 2)) {  // MB
            base = Math.pow(1024, 2);
            suffix = "MB";
        } else {    // KB
            base = 1024;
            suffix = "KB";
        }

        double size = Math.ceil((fileSize / base) * 100) / 100.0;
        return String.format("%.2f %s", size, suffix);
    }

}
