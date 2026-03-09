// 文件路径：src/main/java/org/example/entity/Train.java
package org.example.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Train {

    private Long id;
    /**
     * 车次编号，如 G123、D456、K789
     */
    private String trainNumber;

    /**
     * 出发站
     */
    private String departureStation;

    /**
     * 到达站
     */
    private String arrivalStation;

    /**
     * 出发时间（含日期）
     */
    private Date departureTime;

    /**
     * 到达时间（含日期）
     */
    private Date arrivalTime;

    /**
     * 一等座总座位数
     */
    private Integer firstClassCapacity;

    /**
     * 二等座总座位数
     */
    private Integer secondClassCapacity;

    /**
     * 已售一等座数量
     */
    private Integer firstClassSold;

    /**
     * 已售二等座数量
     */
    private Integer secondClassSold;

    /**
     * 一等座票价
     */
    private BigDecimal firstClassPrice;

    /**
     * 二等座票价
     */
    private BigDecimal secondClassPrice;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}