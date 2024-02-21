package cn.bootx.platform.daxpay.gateway.controller;

import cn.bootx.platform.common.core.annotation.CountTime;
import cn.bootx.platform.common.core.annotation.IgnoreAuth;
import cn.bootx.platform.daxpay.param.pay.*;
import cn.bootx.platform.daxpay.result.DaxResult;
import cn.bootx.platform.daxpay.result.pay.PayResult;
import cn.bootx.platform.daxpay.result.pay.RefundResult;
import cn.bootx.platform.daxpay.result.pay.SyncResult;
import cn.bootx.platform.daxpay.service.annotation.PaymentApi;
import cn.bootx.platform.daxpay.service.core.payment.close.service.PayCloseService;
import cn.bootx.platform.daxpay.service.core.payment.pay.service.PayService;
import cn.bootx.platform.daxpay.service.core.payment.refund.service.RefundService;
import cn.bootx.platform.daxpay.service.core.payment.sync.service.RefundSyncService;
import cn.bootx.platform.daxpay.service.core.payment.sync.service.PaySyncService;
import cn.bootx.platform.daxpay.util.DaxRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统一支付接口
 * @author xxm
 * @since 2023/12/15
 */
@IgnoreAuth
@Tag(name = "统一支付接口")
@RestController
@RequestMapping("/unipay")
@RequiredArgsConstructor
public class UniPayController {
    private final PayService payService;
    private final RefundService refundService;
    private final PaySyncService paySyncService;
    private final PayCloseService payCloseService;
    private final RefundSyncService refundSyncService;


    @CountTime
    @PaymentApi("pay")
    @Operation(summary = "统一支付接口")
    @PostMapping("/pay")
    public DaxResult<PayResult> pay(@RequestBody PayParam payParam){
        return DaxRes.ok(payService.pay(payParam));
    }

    @CountTime
    @PaymentApi("simplePay")
    @Operation(summary = "简单支付接口")
    @PostMapping("/simplePay")
    public DaxResult<PayResult> simplePay(@RequestBody SimplePayParam payParam){
        return DaxRes.ok(payService.simplePay(payParam));
    }

    @CountTime
    @PaymentApi("close")
    @Operation(summary = "支付关闭接口")
    @PostMapping("/close")
    public DaxResult<Void> close(@RequestBody PayCloseParam param){
        payCloseService.close(param);
        return DaxRes.ok();
    }

    @CountTime
    @PaymentApi("refund")
    @Operation(summary = "统一退款接口")
    @PostMapping("/refund")
    public DaxResult<RefundResult> refund(@RequestBody RefundParam param){
        return DaxRes.ok(refundService.refund(param));
    }

    @CountTime
    @PaymentApi("simpleRefund")
    @Operation(summary = "简单退款接口")
    @PostMapping("/simpleRefund")
    public DaxResult<RefundResult> simpleRefund(@RequestBody SimpleRefundParam param){
        return DaxRes.ok(refundService.simpleRefund(param));
    }

    @CountTime
    @PaymentApi("syncPay")
    @Operation(summary = "支付同步接口")
    @PostMapping("/syncPay")
    public DaxResult<SyncResult> syncPay(@RequestBody PaySyncParam param){
        return DaxRes.ok(paySyncService.sync(param));
    }

    @CountTime
    @PaymentApi("syncRefund")
    @Operation(summary = "退款同步接口")
    @PostMapping("/syncRefund")
    public DaxResult<SyncResult> syncRefund(@RequestBody RefundSyncParam param){
        return DaxRes.ok(refundSyncService.sync(param));
    }

}
