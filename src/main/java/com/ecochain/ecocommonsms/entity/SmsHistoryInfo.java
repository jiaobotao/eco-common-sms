package com.ecochain.ecocommonsms.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "historyinfo", schema = "sms", catalog = "")
public class SmsHistoryInfo implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "主键")
    private Integer id;
    @Column(name = "content")
    @ApiModelProperty(value = "短信内容")
    private String content;
    @Column(name = "mobile")
    @ApiModelProperty(value = "手机号码")
    private String mobile;
    @Column(name = "sendtime")
    @ApiModelProperty(value = "发送时间")
    private Timestamp sendTime;
    @Column(name = "issuccess")
    @ApiModelProperty(value = "发送是否成功")
    private Integer isSuccess;
    @Column(name="type")
    @ApiModelProperty(value = "发送类型")
    private String type;
    @Column(name="senderid")
    @ApiModelProperty(value = "发送方ID")
    private Integer senderID;


}
