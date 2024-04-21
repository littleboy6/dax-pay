package cn.bootx.platform.daxpay.util;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单号审查工具类
 * @author yxc
 * @since 2024/4/15
 */
@Slf4j
public class OrderNoGenerateUtil {

    private static final AtomicLong ATOMIC_LONG = new AtomicLong();
    private final static long ORDER_MAX_LIMIT = 999999L;
    /** 机器号 */
    @Setter
    private static String machineNo;

    /**
     * 生成支付订单号
     */
    public static String trade() {
        StringBuilder orderNo = new StringBuilder();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        long id = ATOMIC_LONG.incrementAndGet();
        orderNo.append("P").append(dateStr).append(machineNo).append(String.format("%06d", Math.abs(id) % ORDER_MAX_LIMIT));
        return orderNo.toString();
    }

    /**
     * 生成退款订单号
     */
    public static String refund() {
        StringBuilder orderNo = new StringBuilder();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        long id = ATOMIC_LONG.incrementAndGet();
        orderNo.append("R").append(dateStr).append(machineNo).append(String.format("%06d", Math.abs(id) % ORDER_MAX_LIMIT));
        return orderNo.toString();
    }

    /**
     * 生成转账订单号
     */
    public static String transfer() {
        StringBuilder orderNo = new StringBuilder();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        long id = ATOMIC_LONG.incrementAndGet();
        orderNo.append("T").append(dateStr).append(machineNo).append(String.format("%06d", Math.abs(id) % ORDER_MAX_LIMIT));
        return orderNo.toString();
    }

    /**
     * 生成分账订单号
     */
    public static String allocation() {
        StringBuilder orderNo = new StringBuilder();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        long id = ATOMIC_LONG.incrementAndGet();
        orderNo.append("A").append(dateStr).append(machineNo).append(String.format("%06d", Math.abs(id) % ORDER_MAX_LIMIT));
        return orderNo.toString();
    }

    /**
     * 生成对账订单号
     */
    public static String reconciliation() {
        StringBuilder orderNo = new StringBuilder();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        long id = ATOMIC_LONG.incrementAndGet();
        orderNo.append("C").append(dateStr).append(machineNo).append(String.format("%06d", Math.abs(id) % ORDER_MAX_LIMIT));
        return orderNo.toString();
    }
}
