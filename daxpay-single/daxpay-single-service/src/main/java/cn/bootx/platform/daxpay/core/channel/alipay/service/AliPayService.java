package cn.bootx.platform.daxpay.core.channel.alipay.service;

import cn.bootx.platform.daxpay.code.AliPayCode;
import cn.bootx.platform.daxpay.code.AliPayWay;
import cn.bootx.platform.daxpay.code.PayStatusEnum;
import cn.bootx.platform.daxpay.code.PayWayEnum;
import cn.bootx.platform.daxpay.common.context.AsyncPayLocal;
import cn.bootx.platform.daxpay.common.context.NoticeLocal;
import cn.bootx.platform.daxpay.common.local.PaymentContextLocal;
import cn.bootx.platform.daxpay.core.channel.alipay.entity.AlipayConfig;
import cn.bootx.platform.daxpay.core.order.pay.entity.PayOrder;
import cn.bootx.platform.daxpay.exception.pay.PayFailureException;
import cn.bootx.platform.daxpay.param.channel.AliPayParam;
import cn.bootx.platform.daxpay.param.pay.PayWayParam;
import cn.bootx.platform.daxpay.util.PayUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Method;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.*;
import com.ijpay.alipay.AliPayApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static cn.bootx.platform.daxpay.code.AliPayCode.QUICK_MSECURITY_PAY;


/**
 * 支付宝支付服务
 *
 * @author xxm
 * @since 2021/2/26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliPayService {

    /**
     * 支付前检查支付方式是否可用
     */
    public void validation(PayWayParam payWayParam, AlipayConfig alipayConfig) {

        if (CollUtil.isNotEmpty(alipayConfig.getPayWays())){
            throw new PayFailureException("支付宝未配置可用的支付方式");
        }
        // 发起的支付类型是否在支持的范围内
        PayWayEnum payWayEnum = Optional.ofNullable(AliPayWay.findByCode(payWayParam.getWay()))
            .orElseThrow(() -> new PayFailureException("非法的支付宝支付类型"));
        if (!alipayConfig.getPayWays().contains(payWayEnum.getCode())) {
            throw new PayFailureException("该支付宝支付方式不可用");
        }
    }

    /**
     * 调起支付
     */
    public void pay(PayOrder payOrder, PayWayParam payWayParam, AliPayParam aliPayParam, AlipayConfig alipayConfig) {
        Integer amount = payWayParam.getAmount();
        String payBody = null;
        // 异步线程存储
        AsyncPayLocal asyncPayInfo = PaymentContextLocal.get().getAsyncPayInfo();
        // wap支付
        if (Objects.equals(payWayParam.getWay(), PayWayEnum.WAP.getCode())) {
            payBody = this.wapPay(amount, payOrder, alipayConfig);
        }
        // 程序支付
        else if (Objects.equals(payWayParam.getWay(), PayWayEnum.APP.getCode())) {
            payBody = this.appPay(amount, payOrder, alipayConfig);
        }
        // pc支付
        else if (Objects.equals(payWayParam.getWay(), PayWayEnum.WEB.getCode())) {
            payBody = this.webPay(amount, payOrder, alipayConfig);
        }
        // 二维码支付
        else if (Objects.equals(payWayParam.getWay(), PayWayEnum.QRCODE.getCode())) {
            payBody = this.qrCodePay(amount, payOrder, alipayConfig);
        }
        // 付款码支付
        else if (Objects.equals(payWayParam.getWay(), PayWayEnum.BARCODE.getCode())) {
            String tradeNo = this.barCode(amount, payOrder, aliPayParam, alipayConfig);
            asyncPayInfo.setTradeNo(tradeNo);
        }
        // 通常是发起支付的参数
        asyncPayInfo.setPayBody(payBody);
    }

    /**
     * wap支付
     */
    public String wapPay(int amount, PayOrder payment, AlipayConfig alipayConfig) {
        NoticeLocal noticeInfo = PaymentContextLocal.get().getNoticeInfo();
        AsyncPayLocal asyncPayInfo = PaymentContextLocal.get().getAsyncPayInfo();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setSubject(payment.getTitle());
        model.setOutTradeNo(String.valueOf(payment.getId()));
        model.setTotalAmount(String.valueOf(amount*0.01));
        // 过期时间
        model.setTimeExpire(PayUtil.getAliTimeExpire(asyncPayInfo.getExpiredTime()));
        model.setProductCode(AliPayCode.QUICK_WAP_PAY);
        // 中途退出地址
        model.setQuitUrl(noticeInfo.getQuitUrl());

        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizModel(model);
        // 异步回调必须到当前系统中
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        // 同步回调地址
        request.setReturnUrl(noticeInfo.getReturnUrl());

        try {
            // 通过GET方式的请求, 返回URL的响应, 默认是POST方式的请求, 返回的是表单响应
            AlipayTradeWapPayResponse response = AliPayApi.pageExecute(request, Method.GET.name());
            return response.getBody();
        }
        catch (AlipayApiException e) {
            log.error("支付宝手机支付失败", e);
            throw new PayFailureException("支付宝手机支付失败");
        }
    }

    /**
     * app支付
     */
    public String appPay(int amount, PayOrder payment, AlipayConfig alipayConfig) {
        AsyncPayLocal asyncPayInfo = PaymentContextLocal.get().getAsyncPayInfo();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        model.setSubject(payment.getTitle());
        model.setProductCode(QUICK_MSECURITY_PAY);
        model.setOutTradeNo(String.valueOf(payment.getId()));
        // 过期时间
        model.setTimeExpire(PayUtil.getAliTimeExpire(asyncPayInfo.getExpiredTime()));
        model.setTotalAmount(String.valueOf(amount*0.01));

        try {
            // 异步回调必须到当前系统中
            AlipayTradeAppPayResponse response = AliPayApi.appPayToResponse(model, alipayConfig.getNotifyUrl());
            return response.getBody();
        }
        catch (AlipayApiException e) {
            log.error("支付宝APP支付失败", e);
            throw new PayFailureException("支付宝APP支付失败");
        }
    }

    /**
     * PC支付
     */
    public String webPay(int amount, PayOrder payment, AlipayConfig alipayConfig) {
        NoticeLocal noticeInfo = PaymentContextLocal.get().getNoticeInfo();
        AsyncPayLocal asyncPayInfo = PaymentContextLocal.get().getAsyncPayInfo();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        model.setSubject(payment.getTitle());
        model.setOutTradeNo(String.valueOf(payment.getId()));
        // 过期时间
        model.setTimeExpire(PayUtil.getAliTimeExpire(asyncPayInfo.getExpiredTime()));
        model.setTotalAmount(String.valueOf(amount*0.01));
        // 目前仅支持FAST_INSTANT_TRADE_PAY
        model.setProductCode(AliPayCode.FAST_INSTANT_TRADE_PAY);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizModel(model);
        // 异步回调必须到当前系统中
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        // 同步回调
        request.setReturnUrl(noticeInfo.getReturnUrl());
        try {
            // 通过GET方式的请求, 返回URL的响应, 默认是POST方式的请求, 返回的是表单响应
            AlipayTradePagePayResponse response = AliPayApi.pageExecute(request, Method.GET.name());
            return response.getBody();
        }
        catch (AlipayApiException e) {
            log.error("支付宝PC支付失败", e);
            throw new PayFailureException("支付宝PC支付失败");
        }
    }

    /**
     * 二维码支付(扫码支付)
     */
    public String qrCodePay(int amount, PayOrder payment, AlipayConfig alipayConfig) {
        AsyncPayLocal asyncPayInfo = PaymentContextLocal.get().getAsyncPayInfo();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setSubject(payment.getTitle());
        model.setOutTradeNo(String.valueOf(payment.getId()));
        model.setTotalAmount(String.valueOf(amount*0.01));

        // 过期时间
        model.setTimeExpire(PayUtil.getAliTimeExpire(asyncPayInfo.getExpiredTime()));

        try {
            AlipayTradePrecreateResponse response = AliPayApi.tradePrecreatePayToResponse(model, alipayConfig.getNotifyUrl());
            this.verifyErrorMsg(response);
            return response.getQrCode();
        }
        catch (AlipayApiException e) {
            log.error("支付宝手机支付失败", e);
            throw new PayFailureException("支付宝手机支付失败");
        }
    }

    /**
     * 付款码支付
     */
    public String barCode(int amount, PayOrder payment, AliPayParam aliPayParam, AlipayConfig alipayConfig) {
        AsyncPayLocal asyncPayInfo = PaymentContextLocal.get().getAsyncPayInfo();
        AlipayTradePayModel model = new AlipayTradePayModel();

        model.setSubject(payment.getTitle());
        model.setOutTradeNo(String.valueOf(payment.getId()));
        model.setScene(AliPayCode.BAR_CODE);
        model.setAuthCode(aliPayParam.getAuthCode());

        // 过期时间
        model.setTimeExpire(PayUtil.getAliTimeExpire(asyncPayInfo.getExpiredTime()));
        model.setTotalAmount(String.valueOf(amount*0.01));
        try {
            AlipayTradePayResponse response = AliPayApi.tradePayToResponse(model, alipayConfig.getNotifyUrl());

            // 支付成功处理 金额2000以下免密支付
            if (Objects.equals(response.getCode(), AliPayCode.SUCCESS)) {
                payment.setStatus(PayStatusEnum.SUCCESS.getCode()).setPayTime(LocalDateTime.now());
                return response.getTradeNo();
            }
            // 非支付中响应码, 进行错误处理
            if (!Objects.equals(response.getCode(), AliPayCode.INPROCESS)) {
                this.verifyErrorMsg(response);
            }
        }
        catch (AlipayApiException e) {
            log.error("主动扫码支付失败", e);
            throw new PayFailureException("主动扫码支付失败");
        }
        return null;
    }

    /**
     * 验证错误信息
     */
    private void verifyErrorMsg(AlipayResponse alipayResponse) {
        if (!Objects.equals(alipayResponse.getCode(), AliPayCode.SUCCESS)) {
            String errorMsg = alipayResponse.getSubMsg();
            if (StrUtil.isBlank(errorMsg)) {
                errorMsg = alipayResponse.getMsg();
            }
            log.error("支付失败 {}", errorMsg);
            throw new PayFailureException(errorMsg);
        }
    }

}