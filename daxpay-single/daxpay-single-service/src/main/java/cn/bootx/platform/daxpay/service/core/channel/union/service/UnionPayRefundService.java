package cn.bootx.platform.daxpay.service.core.channel.union.service;

import cn.bootx.platform.daxpay.code.RefundStatusEnum;
import cn.bootx.platform.daxpay.exception.pay.PayFailureException;
import cn.bootx.platform.daxpay.service.code.UnionPayCode;
import cn.bootx.platform.daxpay.service.common.context.RefundLocal;
import cn.bootx.platform.daxpay.service.common.local.PaymentContextLocal;
import cn.bootx.platform.daxpay.service.core.channel.union.entity.UnionPayConfig;
import cn.bootx.platform.daxpay.service.core.order.pay.entity.PayChannelOrder;
import cn.bootx.platform.daxpay.service.core.order.refund.entity.RefundOrder;
import cn.hutool.core.util.StrUtil;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.unionpay.UnionPayApi;
import com.ijpay.unionpay.enums.ServiceEnum;
import com.ijpay.unionpay.model.RefundModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static cn.bootx.platform.daxpay.service.code.UnionPayCode.*;

/**
 * 云闪付退款操作
 * @author xxm
 * @since 2024/3/7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UnionPayRefundService {


    /**
     * 退款方法
     */
    public void refund(RefundOrder refundOrder, int amount, PayChannelOrder channelOrder, UnionPayConfig unionPayConfig) {
        Map<String, String> params = RefundModel.builder()
                .service(ServiceEnum.REFUND.toString())
                .mch_id(unionPayConfig.getMachId())
                .out_trade_no(String.valueOf(refundOrder.getPaymentId()))
                .out_refund_no(String.valueOf(refundOrder.getId()))
                .total_fee(String.valueOf(channelOrder.getAmount()))
                .refund_fee(String.valueOf(amount))
                .op_user_id(unionPayConfig.getMachId())
                .nonce_str(WxPayKit.generateStr())
                .build()
                .createSign(unionPayConfig.getAppKey(), SignType.MD5);

        String xmlResult = UnionPayApi.execution(unionPayConfig.getServerUrl(), params);
        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
        this.verifyErrorMsg(result);
        // 云闪付退款是否成功需要查询状态, 所以设置为退款中状态
        RefundLocal refundInfo = PaymentContextLocal.get().getRefundInfo();
        refundInfo.setStatus(RefundStatusEnum.PROGRESS)
                .setGatewayOrderNo(result.get(REFUND_ID));
    }

    /**
     * 验证错误信息
     */
    private void verifyErrorMsg(Map<String, String> result) {
        String status = result.get(UnionPayCode.STATUS);
        String returnCode = result.get(UnionPayCode.RESULT_CODE);

        // 判断查询是否成功
        if (!(Objects.equals(SUCCESS, status) && Objects.equals(SUCCESS, returnCode))){
            String errorMsg = result.get(ERR_MSG);
            if (StrUtil.isBlank(errorMsg)) {
                errorMsg = result.get(MESSAGE);
            }
            log.error("订单退款失败 {}", errorMsg);
            RefundLocal refundInfo = PaymentContextLocal.get().getRefundInfo();
            refundInfo.setErrorMsg(errorMsg);
            refundInfo.setErrorCode(Optional.ofNullable(returnCode).orElse(returnCode));
            throw new PayFailureException(errorMsg);
        }
    }
}
