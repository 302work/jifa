//查看详情
// @Bind #orderDetailBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",true);
    saveOrderBtn.set("visible",false);
    orderDetailDialog.show();
};

//编辑订单
// @Bind #editOrderBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",false);
    //设置某些字段不可编辑
    view.get("^readOnly").set("readOnly",true);
    saveOrderBtn.set("visible",true);
    orderDetailDialog.show();
};

//审核订单
// @Bind #auditOrderBtn.onClick
!function(self,arg,auditOrderDialog) {
    auditOrderDialog.show();
};

//审核通过订单
// @Bind #auditOrderPassBtn.onClick
!function(self,arg,auditOrderDialog,auditOrderAjaxAction,auditOrderAutoForm) {
    audit(1,auditOrderAjaxAction,auditOrderDialog,auditOrderAutoForm);
};

//审核不通过订单
// @Bind #auditOrderUnPassBtn.onClick
!function(self,arg,auditOrderDialog,auditOrderAjaxAction,auditOrderAutoForm) {
    audit(2,auditOrderAjaxAction,auditOrderDialog,auditOrderAutoForm);
};

//审核订单
function audit(status,auditOrderAjaxAction,auditOrderDialog,auditOrderAutoForm){
    //当前任务号
    var taskId = "${request.getParameter('taskId')}";
    //备注
    var remark = auditOrderAutoForm.get("entity").get("remark");
    if(!remark || remark==""){
        remark = "无";
    }
    auditOrderAjaxAction.set("parameter",
        {
            status:status,
            taskIds:taskId,
            remark:remark
        }
    ).execute(function(){
        auditOrderDialog.hide();
    });
}
//取消
// @Bind #closeAuditOrderDialog.onClick
!function(self,arg,auditOrderDialog) {
    auditOrderDialog.hide();
};

//保存订单按钮
// @Bind #saveOrderBtn.onClick
!function(self,arg,orderDetailDialog,saveOrderUpdateAction) {
    saveOrderUpdateAction.execute(function(){
        orderDetailDialog.hide();
    });
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
!function(self,arg,resultDialog,dsOrderRecord,resultIFrame,picDataGrid,dsRecordTestCondition,dsDevice) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        //
        var recordId = currRecord.get("id");
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
        dsRecordTestCondition.set("parameter",recordId).flushAsync();
        dsDevice.set("parameter",recordId).flushAsync();
        resultDialog.show();
    }
};

//关闭检测结果弹窗
// @Bind #closeResultDialogBtn.onClick
!function(self,arg,resultDialog) {
    resultDialog.hide();
};

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
};

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
};

//监听样品编号输入框的按键事件
// @Bind #sampleNoInput.onKeyDown
!function(self,arg,inputTestDataDialog,dsRecordTestCondition,dsOrderRecord,addResultIFrame,dsDevice,samplePicDataGrid) {
    //如果是回车
    if(arg.keyCode==13){
        var sampleNoInput = $("#d_sampleNoInput input").val();
        if(sampleNoInput){
            var entityList = dsOrderRecord.getData();
            var currRecord;
            entityList.each(function(entity){
                if(entity.get("sampleNo")==sampleNoInput){
                    currRecord = entity;
                    return false;
                }
            });
            if(currRecord){
                setAddResultIFrameParam(currRecord,addResultIFrame,dsRecordTestCondition,dsDevice,samplePicDataGrid);
                inputTestDataDialog.show();
            }else{
                dorado.MessageBox.alert("样品编号不存在");
            }
        }
    }
};

//检测数据录入
// @Bind #inputTestDataBtn.onClick
!function(self,arg,dsOrderRecord,addResultIFrame,inputTestDataDialog,dsRecordTestCondition,dsDevice,samplePicDataGrid) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord) {
        setAddResultIFrameParam(currRecord,addResultIFrame,dsRecordTestCondition,dsDevice,samplePicDataGrid);
        inputTestDataDialog.show();
    }

}

//设置iFrame的recordId
function setAddResultIFrameParam(currRecord,addResultIFrame,dsRecordTestCondition,dsDevice,samplePicDataGrid){
    var path = addResultIFrame.get("path");
    var index = path.indexOf("?");
    if (index != -1) {
        path = path.substring(0, index);
    }
    var recordId = currRecord.get("id");
    path += "?recordId=" + recordId;
    addResultIFrame.set("path", path);
    dsRecordTestCondition.set("parameter",recordId).flushAsync();
    dsDevice.set("parameter",{recordId:recordId}).flushAsync();
    if(currRecord){
        //显示图片
        //原样
        var samplePic = currRecord.get("samplePic");
        //测试样
        var testSamplePic = currRecord.get("testSamplePic");
        var data = [{
            samplePic2:samplePic,
            testSamplePic2:testSamplePic
        }];
        samplePicDataGrid.set("items",data);
    }
}
// @Bind #samplePicDataGrid.#samplePic2.onRenderCell
!function(arg) {
    var samplePic = arg.data.samplePic2;
    $(arg.dom).empty();
    if(samplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: samplePic,
            width:350
        });
    }
};

// @Bind #samplePicDataGrid.#testSamplePic2.onRenderCell
!function(arg) {
    var testSamplePic = arg.data.testSamplePic2;
    $(arg.dom).empty();
    if(testSamplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: testSamplePic,
            width:350
        });
    }
};
//关闭检测数据录入弹窗
// @Bind #closeInputTestDataDialogBtn.onClick
!function(self,arg,inputTestDataDialog) {
    inputTestDataDialog.hide();
}

//保存检测条件之前，设置recordId
// @Bind #saveTestConditionUpdateAction.beforeExecute
!function(self,arg,dsOrderRecord) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        self.set("parameter",currRecord.get("id"));
    }
}

//保存成功刷新检测条件
// @Bind #saveTestConditionUpdateAction.onSuccess
!function(self,arg,dsRecordTestCondition) {
    dsRecordTestCondition.flushAsync();
}

//添加检测仪器
// @Bind #addDeviceBtn.onClick
!function(selectDeviceDialog) {
    selectDeviceDialog.show();
}

//关闭选择检测仪器弹窗
// @Bind #closeSelectDeviceDialogBtn.onClick
!function(selectDeviceDialog) {
    selectDeviceDialog.hide();
}

//选择检测仪器
// @Bind #saveDeviceBtn.onClick
!function(deviceSubView,dsOrderRecord,selectDeviceDialog,addDeviceAjaxAction,dsDevice) {
    var subView = deviceSubView.get("subView");
    var currDevice = subView.id("dsDevice").getData("#");
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    if(currDevice){
        var status = currDevice.get("status");
        if(status==2){
            dorado.MessageBox.alert("该仪器已停用,无法使用");
            return false;
        }
        var deviceId = currDevice.get("id");
        var recordId = currRecord.get("id");
        if(deviceId && recordId){
            addDeviceAjaxAction.set("parameter",{recordId:recordId,deviceId:deviceId}).execute(function(){
                dsDevice.flushAsync();
                selectDeviceDialog.hide();
            });
        }
    }

}

//删除检测仪器
// @Bind #delDeviceBtn.onClick
!function(dsOrderRecord,dsDevice,delDeviceAjaxAction) {
    var currDevice = dsDevice.getData("#");
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    if(currDevice){
        var deviceId = currDevice.get("id");
        var recordId = currRecord.get("id");
        if(deviceId && recordId){
            delDeviceAjaxAction.set("parameter",{recordId:recordId,deviceId:deviceId}).execute(function(){
                dsDevice.flushAsync();
            });
        }
    }

}

//上传原样照片前设置记录id
// @Bind #samplePicUploadAction.beforeFileUpload
!function(self,dsOrderRecord){
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    var recordId = currRecord.get("id");
    //动态设置参数
    self.set("parameter", {
        recordId: recordId,
        type:1
    });
};

//上传测试样照片前设置记录id
// @Bind #testSamplePicUploadAction.beforeFileUpload
!function(self,dsOrderRecord){
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    var recordId = currRecord.get("id");
    //动态设置参数
    self.set("parameter", {
        recordId: recordId,
        type:2
    });
};

//上传原样图片
// @Bind #samplePicUploadAction.onFileUploaded
!function(arg,samplePicDataGrid,dsOrderRecord) {
    var returnValue = arg.returnValue;//获取FileResolver方法返回的信息
    var items = samplePicDataGrid.get("items");
    var data = [{
        samplePic2:returnValue,
        testSamplePic2:items[0].testSamplePic2
    }];
    samplePicDataGrid.set("items",data);
    dsOrderRecord.getData("#").set("samplePic",returnValue);
};

//上传测试样图片
// @Bind #testSamplePicUploadAction.onFileUploaded
!function(arg,samplePicDataGrid,dsOrderRecord) {
    var returnValue = arg.returnValue;//获取FileResolver方法返回的信息
    var items = samplePicDataGrid.get("items");
    var data = [{
        samplePic2:items[0].samplePic2,
        testSamplePic2:returnValue
    }];
    samplePicDataGrid.set("items",data);
    dsOrderRecord.getData("#").set("testSamplePic",returnValue);
};


//删除记录
// @Bind #deleteRecordBtn.onClick
!function(dsOrderRecord,deleteRecordAjaxAction) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord) {
        var recordId = currRecord.get("id");
        deleteRecordAjaxAction.set("parameter", {recordId: recordId}).execute(function () {
            dsOrderRecord.flushAsync();
        });
    }

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

//查看原始记录
// @Bind #yuanshijiluBtn.onClick
!function(self,arg,dsOrderRecord,yuanshijiluDialog,yuanshijiluAjaxAction) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        var recordId = currRecord.get("id");
        yuanshijiluDialog.show();
        yuanshijiluAjaxAction.set("parameter",{recordId:recordId}).execute(function(data){
            //样品编号
            var record = data.record;
            $("#sampleNo").html(record.sampleNo);
            //检测项目
            var project = data.project;
            $("#projectName").html(project.name);
            //方法标准
            var methodStandard = data.methodStandard;
            $("#methodStandard").html(methodStandard.name+"（"+methodStandard.standardNo+"）")
            //检测人
            var testUserNamePic = data.testUserNamePic;
            if(testUserNamePic){
                $("#testUserNamePic").html("<img width='50' src='"+testUserNamePic+"'/>");
            }
            //审核人
            var auditUserPic = data.auditUserPic;
            if(auditUserPic){
                $("#auditUserPic").html("<img width='50' src='"+auditUserPic+"'/>");
            }
            //检测日期
            var testDate = changeDate(record.testDate);
            $("#testDate").html(testDate);

            //检测条件
            var recordTestConditionList = data.recordTestConditionList;
            if(recordTestConditionList){
                var recordTestCondition = null;
                recordTestConditionList.each(function (tc) {
                    var rtc = tc.name+"："+tc.value;
                    if(recordTestCondition == null){
                        recordTestCondition = rtc;
                    }else{
                        recordTestCondition += "，"+rtc;
                    }
                });
                $("#recordTestCondition").html(recordTestCondition);
            }
            //检测仪器
            var deviceList =  data.deviceList;
            if(deviceList){
                var devices = null;
                deviceList.each(function (device) {
                    var d = device.name;
                    if(device.deviceNo){
                        d += "（"+device.deviceNo+"）";
                    }
                    if(devices == null){
                        devices = d;
                    }else{
                        devices += "，"+d;
                    }
                });
                $("#deviceList").html(devices);
            }
            //所有列
            var resultColumnList = data.resultColumnList;
            if(resultColumnList){
                var resultColumnHtml = "<td>次数</td>";
                resultColumnList.each(function (resultColumn) {
                    resultColumnHtml += "<td>"+resultColumn.name+"</td>";
                });
                resultColumnHtml += "<td>状态</td>";
                resultColumnHtml += "<td class=\"noborder-right\">备注</td>";
                $("#resultColumn").html(resultColumnHtml);
            }
            //检测结果
            var resultList = data.resultList;
            //如果没有检测记录，构建一个空的
            if(!resultList || resultList.length==0){
                resultList = [];
                for(var i=0;i<7;i++){
                    var result = {index:i+1,status:null,remark:null};
                    resultColumnList.each(function (resultColumn) {
                        result["col_"+resultColumn.id] = "";
                    });
                    resultList.push(result);
                }
            }
            if(resultList && resultList.length>0){
                var resultListHtml = "";
                resultList.each(function (result) {
                    if(result.status == 2) {
                        resultListHtml += "<tr style='color: red;'>";
                    }else{
                        resultListHtml += "<tr>";
                    }
                    //次数
                    resultListHtml += "<td>"+result.index+"</td>";
                    //所有列
                    resultColumnList.each(function (resultColumn) {
                        var colId = resultColumn.id;
                        resultListHtml += "<td>"+eval('result.col_'+colId)+"</td>";
                    });
                    //状态
                    var statusStr = "";
                    if(result.status == 1){
                        statusStr = "有效";
                    }
                    if(result.status == 2){
                        statusStr = "无效";
                    }
                    resultListHtml += "<td>"+statusStr+"</td>";
                    //备注
                    var remark = "";
                    if(result.remark){
                        remark = result.remark;
                    }
                    resultListHtml += "<td>"+remark+"</td>";
                    resultListHtml += "</tr>";
                });
                //平均值
                resultListHtml += "<tr class=\"noborder-bottom\">";
                resultListHtml += "<td>avg</td>";
                resultColumnList.each(function () {
                    resultListHtml += "<td></td>";
                });
                resultListHtml += "<td></td>";
                resultListHtml += "<td></td>";
                resultListHtml += "</tr>";
                $("#resultValue").html(resultListHtml);
            }

            //条形码
            JsBarcode("#barcode", record.sampleNo, {
                height:30
            });
        });

    }
};

//打印原始记录
// @Bind #printYuanshijiluBtn.onClick
!function(self,arg) {
    $("#yuanshijiluDiv").print({
        globalStyles:false
    });


};

//关闭原始记录
// @Bind #closeYuanshijiluBtn.onClick
!function(self,arg,yuanshijiluDialog) {
    yuanshijiluDialog.hide();
};