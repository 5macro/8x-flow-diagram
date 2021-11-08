package classwork.architecture_design

import doxflow.diagram_8x_flow
import doxflow.dsl.fulfillment
import doxflow.models.diagram.Relationship.*

diagram_8x_flow {
    lateinit var prepaid_fulfillment: fulfillment
    lateinit var invoice_fulfillment: fulfillment
    lateinit var payment_fulfillment: fulfillment
    lateinit var info_promotion_fulfillment: fulfillment

    context("信息推广上下文") {
        val promoter = participant_party("思沃租房") play role_party("推广商")
        val advertiser = participant_party("预充值用户") play role_party("广告主")
//        val rental_info = participant_thing("房屋租赁信息")

        proposal("信息推广方案", promoter, Companion.ONE_TO_ONE) {
            key_timestamps("创建时间")
            key_data("每点击报价")

            contract("信息推广服务合同", promoter, advertiser) {
                key_timestamps("签订时间")


                info_promotion_fulfillment = fulfillment("信息推广", Companion.ONE_TO_N) {
                    request(advertiser) {
                        key_timestamps("创建时间", "过期时间", "终止时间")
                    }

                    confirmation(promoter) {
                        key_timestamps("创建时间")
                    }
                }

                fulfillment("推广取消", Companion.ONE_TO_N) {
                    request(advertiser) {
                        key_timestamps("创建时间", "过期时间")
                    }

                    confirmation(promoter) {
                        key_timestamps("创建时间")
                    }
                }

                fulfillment("推广重启", Companion.ONE_TO_N) {
                    request(advertiser) {
                        key_timestamps("创建时间", "过期时间")
                    }

                    confirmation(promoter) {
                        key_timestamps("创建时间")
                    }
                }

                payment_fulfillment = fulfillment("支付", Companion.ONE_TO_N) {
                    request(promoter) {
                        key_timestamps("创建时间", "过期时间", "终止时间")
                        key_data("金额")
                    }

                    confirmation(advertiser) {
                        key_timestamps("创建时间")
                        key_data("金额")
                    }
                }
            }
        }
    }

    context("三方数据监测上下文") {
        val adx_data_monitor = role_party("ADX数据监测系统")
        val data_monitor_requester = role_party("监测请求方")

        contract("三方数据监测协议", adx_data_monitor, data_monitor_requester) {
            fulfillment("数据监测", Companion.ONE_TO_N) {
                request(data_monitor_requester) {
                    key_timestamps("创建时间", "过期时间")
                }
                confirmation(adx_data_monitor) {
                    key_timestamps("创建时间")

                    val evidence = evidence("点击数据统计表单") {
                        key_timestamps("创建时间")
                    }

                    evidence play info_promotion_fulfillment.confirmation
                }
            }
        }
    }

    context("预充值协议上下文") {

        val prepaid_user = participant_party("房产经纪人") play role_party("预充值用户")
        val siwo_rental = role_party("思沃租房")

        contract("预充值协议", prepaid_user, siwo_rental) {
            key_timestamps("签订时间")
            participant_place("预充值账户") relate this

            prepaid_fulfillment = fulfillment("预充值", Companion.ONE_TO_N) {
                request(siwo_rental) {
                    key_timestamps("创建时间", "过期时间")
                    key_data("金额")
                }
                confirmation(prepaid_user) {
                    key_timestamps("创建时间")
                    key_data("金额")
                }
            }

            fulfillment("余额退款", Companion.ONE_TO_N) {
                request(prepaid_user) {
                    key_timestamps("创建时间", "过期时间")
                    key_data("金额")
                }
                confirmation(siwo_rental) {
                    key_timestamps("创建时间")
                    key_data("金额")
                }
            }

            fulfillment("支付推广费用", Companion.ONE_TO_N) {
                request(siwo_rental) {
                    key_timestamps("创建时间", "过期时间")
                    key_data("金额")
                }
                confirmation(prepaid_user) {
                    key_timestamps("创建时间")
                    key_data("金额")

                    val evidence = evidence("支付推广费凭证") {
                        key_timestamps("支付时间")
                        key_data("金额")
                    }

                    evidence play payment_fulfillment.confirmation
                }
            }

            fulfillment("账单发送", Companion.ONE_TO_N) {
                request(prepaid_user) {
                    key_timestamps("创建时间", "过期时间")
                    key_data("金额")
                }
                confirmation(siwo_rental) {
                    key_timestamps("发送时间")
                }
            }

            invoice_fulfillment = fulfillment("发票开具", Companion.ONE_TO_N) {
                request(prepaid_user) {
                    key_timestamps("创建时间", "过期时间")
                    key_data("金额")
                }
                confirmation(siwo_rental) {
                    key_timestamps("创建时间")
                    key_data("金额")
                }
            }
        }
    }

    context("三方支付上下文") {
        val xx_payment_user = role_party("XX支付用户")
        val xx_payment_service = role_3rd_system("XX支付")
        contract("XX支付协议", xx_payment_user, xx_payment_service) {
            key_timestamps("签订时间")

            fulfillment("代付", Companion.ONE_TO_N) {
                request(xx_payment_user) {
                    key_timestamps("创建时间", "过期时间")
                    key_data("金额")
                }

                confirmation(xx_payment_service) {
                    key_timestamps("创建时间")
                    key_data("金额")

                    val evidence = evidence("支付凭证") {
                        key_timestamps("支付时间")
                        key_data("金额")
                    }

                    evidence play prepaid_fulfillment.confirmation
                }
            }
        }
    }

    context("发票代开服务上下文") {
        val invoice_customer = role_party("发票客户")
        val invoice_generation_service = role_3rd_system("发票代开服务")
        contract("发票代开协议", invoice_customer, invoice_generation_service) {
            key_timestamps("签订时间")

            fulfillment("代开发票", Companion.ONE_TO_N) {
                request(invoice_customer) {
                    key_timestamps("创建时间", "过期时间")
                    key_data("金额")
                }

                confirmation(invoice_generation_service) {
                    key_timestamps("创建时间")
                    key_data("金额")

                    val evidence = evidence("发票") {
                        key_timestamps("开具时间")
                        key_data("金额")
                    }

                    evidence play invoice_fulfillment.confirmation
                }
            }
        }
    }
} export "./diagrams/all.png"