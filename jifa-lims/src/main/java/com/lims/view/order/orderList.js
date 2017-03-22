//查看详情
// @Bind #orderDetailBtn.onClick
!function(self,arg,orderDetailDialog) {
    orderDetailDialog.show();
};

//关闭
// @Bind #closeOrderDetailBtn.onClick
!function(self,arg,orderDetailDialog) {
    orderDetailDialog.hide();
};

//首次加载订单
// @Bind #dsOrder.onReady
!function(self,arg,dsOrder,dsOrderProject,dsOrderRecord) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        dsOrderProject.set("parameter",currOrder.get("id")).flushAsync();
        dsOrderRecord.set("parameter",currOrder.get("id")).flushAsync();
    }
};

//行点击
// @Bind #orderDataGrid.onDataRowClick
!function(self,arg,dsOrder,dsOrderProject,dsOrderRecord) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        dsOrderProject.set("parameter",currOrder.get("id")).flushAsync();
        dsOrderRecord.set("parameter",currOrder.get("id")).flushAsync();
    }
};

//记录的检测结果
// @Bind #recordResultBtn.onClick
!function(self,arg,resultDialog,dsOrderRecord,resultIFrame,picDataGrid,dsRecordTestCondition) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        //
        var recordId = currRecord.get("id");
        //刷新检测条件
        dsRecordTestCondition.set("parameter",recordId).flushAsync();

        var path = resultIFrame.get("path");
        var index = path.indexOf("?");
        if(index!=-1){
            path = path.substring(0,index);
        }
        path += "?recordId="+recordId;
        resultIFrame.set("path",path);

        //显示图片
        //原样
        var samplePic = currRecord.get("samplePic");
        //测试样
        var testSamplePic = currRecord.get("testSamplePic");
        var data = [{
            samplePic:samplePic,
            testSamplePic:testSamplePic
        }];
        picDataGrid.set("items",data);
        resultDialog.show();
    }
};

//关闭检测结果弹窗
// @Bind #closeResultDialogBtn.onClick
!function(self,arg,resultDialog) {
    resultDialog.hide();
};

//原样图片
// @Bind #picDataGrid.#samplePic.onRenderCell
!function(arg) {
    var samplePic = arg.data.samplePic;
    $(arg.dom).empty();
    if(samplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: samplePic,
            width:350
        });
    }
}

//测试样图片
// @Bind #picDataGrid.#testSamplePic.onRenderCell
!function(arg) {
    var testSamplePic = arg.data.testSamplePic;
    $(arg.dom).empty();
    if(testSamplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: testSamplePic,
            width:350
        });
    }
}

//打印记录的检测结果
// @Bind #printResultBtn.onClick
!function(self,arg) {
    var options = {
        mode : "popup",
        popClose : false,
        popHt      : 1000,
        popWd      : 800,
        popX       : 0,
        popY       : 0
    };
    //$("#d_resultDialog .dialog-body-wrap").printArea(options);
    //$("#d_picDataGrid").printArea(options);
    var printArea = [];
    printArea.push("#d_resultDialog d-dialog-caption-bar");
    printArea.push("#d_resultDialog .dialog-body");
    printArea = printArea.join(",");
    $("#d_resultDialog").printArea(options);

};

//项目的检测结果
// @Bind #projectResultBtn.onClick
!function(self,arg,projectResultDialog,dsOrder,queryOrderRecordAjaxAction,dsOrderProject,projectResultIFrame,projectPicDataGrid,dsProjectRecordTestCondition) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        //当前订单id
        var orderId = currOrder.get("id");
        var currOrderProject = dsOrderProject.getData("#");
        if(currOrderProject) {
            //项目id
            var projectId = currOrderProject.get("id");
            //刷新检测条件
            dsProjectRecordTestCondition.set("parameter", {orderId:orderId,projectId:projectId}).flushAsync();

            var path = projectResultIFrame.get("path");
            var index = path.indexOf("?");
            if (index != -1) {
                path = path.substring(0, index);
            }
            path += "?orderId=" + orderId+"&projectId="+projectId;
            projectResultIFrame.set("path", path);
            var data = [];
            //所有检查记录
            queryOrderRecordAjaxAction.set("parameter",{orderId:orderId,projectId:projectId}).execute(function(recordList){
                recordList.each(function (record) {
                    //显示图片
                    //原样
                    var samplePic = record.samplePic;
                    console.log("samplePic:"+samplePic);
                    //测试样
                    var testSamplePic = record.testSamplePic;
                    //样品编号
                    var sampleNo = record.sampleNo;
                    data.push({
                        projectSampleNo:sampleNo,
                        projectSamplePic: samplePic,
                        projectTestSamplePic: testSamplePic
                    });
                });
                console.log("dataLength:"+data.length);
                console.log("dataLength:"+data);
                projectPicDataGrid.set("items", data);
                projectResultDialog.show();
            });
        }
    }
};

//项目检测结果 原样图片
// @Bind #projectPicDataGrid.#projectSamplePic.onRenderCell
!function(arg) {
    var samplePic = arg.data.projectSamplePic;
    $(arg.dom).empty();
    if(samplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: samplePic,
            width:350
        });
    }
}

//项目检测结果 测试样图片
// @Bind #projectPicDataGrid.#projectTestSamplePic.onRenderCell
!function(arg) {
    var testSamplePic = arg.data.projectTestSamplePic;
    $(arg.dom).empty();
    if(testSamplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: testSamplePic,
            width:350
        });
    }
}

//关闭项目的检测结果弹层
// @Bind #closeProjectResultDialogBtn.onClick
!function(self,arg,projectResultDialog) {
    projectResultDialog.hide();
};

//打印项目的检测结果
// @Bind #printProjectResultBtn.onClick
!function(self,arg,projectResultDialog) {
    alert("想打啥");
};

//查看检测协议
// @Bind #jiancexieyiBtn.onClick
!function(self,arg,dsOrder,jiancexieyiDialog,jiancexieyiAjaxAction) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        var orderId = currOrder.get("id");
        jiancexieyiDialog.show();
        jiancexieyiAjaxAction.set("parameter",{orderId:orderId}).execute(function(data){
            var order = data.order;
            var consumer = data.consumer;
            $("#consumerName").html(consumer.name);
            $("#consumerAddress").html(consumer.address);
            $("#orderNo").html(order.orderNo);
            $("#testType").html(order.testType);
            $("#client").html(order.client);
            $("#clientPhone").html(order.clientPhone);
            $("#clientFax").html(order.clientFax);
            $("#clientZip").html(order.clientZip);
            $("#payer").html(order.payer);
            $("#payerPhone").html(order.payerPhone);
            $("#payerFax").html(order.payerFax);
            $("#payerZip").html(order.payerZip);
            $("#deliveryDate").html(changeDate(order.deliveryDate));
            $("#finishDate").html(changeDate(order.finishDate));
            $("#area").html(order.area);
            $("#sampleName").html(order.sampleName);
            $("#sampleCount").html(order.sampleCount);
            $("#sampleDesc").html(order.sampleDesc);
            $("#fibreComponent").html(order.fibreComponent);
            $("#weight").html(order.weight);
            //要求完成时间
            var timeLimit = order.timeLimit;
            var timeLimitStr = "";
            if(timeLimit=="5"){
                timeLimitStr = "常规";
            }
            if(timeLimit=="3"){
                timeLimitStr = "加急";
            }
            if(timeLimit=="2"){
                timeLimitStr = "特快";
            }
            if(timeLimit=="0"){
                timeLimitStr = "当天";
            }
            $("#timeLimit").html(timeLimitStr);
            //剩余样品处理方式
            var sampleHandleType = order.sampleHandleType;
            var sampleHandleTypeStr = "";
            if(sampleHandleType=="1"){
                sampleHandleTypeStr = "自取";
            }
            if(sampleHandleType=="2"){
                sampleHandleTypeStr = "不退";
            }
            if(sampleHandleType=="3"){
                sampleHandleTypeStr = "寄回";
            }
            $("#sampleHandleType").html(sampleHandleTypeStr);
            //报告发送方式
            var reportSendWay = order.reportSendWay;
            var reportSendWayStr = "";
            if(reportSendWay=="1"){
                reportSendWayStr = "自取";
            }
            if(reportSendWay=="2"){
                reportSendWayStr = "邮寄";
            }
            $("#reportSendWay").html(reportSendWayStr);
            //报告语言
            var reportLanguage = order.reportLanguage;
            var reportLanguageStr = "";
            if(reportLanguage=="ch"){
                reportLanguageStr = "中文";
            }
            if(reportLanguage=="en"){
                reportLanguageStr = "英文";
            }
            if(reportLanguage=="ch,en"){
                reportLanguageStr = "中英文";
            }
            $("#reportLanguage").html(reportLanguageStr);

            $("#testResult").html(order.testResult);
            $("#signDate").html(order.signDate);
            $("#crTime").html(changeDate(order.crTime));
            if(data.auditUserPic) {
                $("#auditUserPic").html("<img width=\"50\" src=\""+data.auditUserPic+"\">");
            }

            //检测项目
            var projectList = data.projectList;
            if(projectList){
                var projectHtml = "<tr style='font-weight:bold;'><td>检测项目</td><td>检测标准</td></tr>";
                projectList.each(function (projectData) {
                    var project = projectData.project;
                    var projectName = project.name;

                    var methodStandard = projectData.methodStandard;
                    var methodStandardName = methodStandard.name;
                    var methodStandardNo = methodStandard.standardNo;
                    projectHtml += "<tr><td>"+projectName+"</td><td>"+methodStandardName+"（"+methodStandardNo+"）</td></tr>";
                });
                $("#projectList").html(projectHtml);
            }
            //条形码
            JsBarcode("#barcode", order.orderNo, {
                height:40
            });
        });

    }
};
//转换日期格式 原格式2017-03-21T13:08:03Z
//替换成2017-03-21
function changeDate(date) {
    // date = date.replace("T"," ");
    // date = date.replace("Z","");
    if(date){
        date = date.substring(0,10);
        return date;
    }
    return "";
}

//打印检测协议
// @Bind #printJiancexieyiBtn.onClick
!function(self,arg) {
    // jQuery.print("#jiancexieyiDiv");
    $("#jiancexieyiDiv").print({
        // iframe : false,
        globalStyles:false
    });


};

//关闭检测协议
// @Bind #closeJiancexieyiBtn.onClick
!function(self,arg,jiancexieyiDialog) {
    jiancexieyiDialog.hide();
};

