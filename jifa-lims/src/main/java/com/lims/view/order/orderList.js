(function($) {
    var counter = 0;
    var modes = { iframe : "iframe", popup : "popup" };
    var standards = { strict : "strict", loose : "loose", html5 : "html5" };
    var defaults = { mode       : modes.iframe,
        standard   : standards.html5,
        popHt      : 500,
        popWd      : 400,
        popX       : 200,
        popY       : 200,
        popTitle   : '',
        popClose   : false,
        extraCss   : '',
        extraHead  : '',
        retainAttr : ["id","class","style"] };

    var settings = {};//global settings

    $.fn.printArea = function( options )
    {
        $.extend( settings, defaults, options );

        counter++;
        var idPrefix = "printArea_";
        $( "[id^=" + idPrefix + "]" ).remove();

        settings.id = idPrefix + counter;

        var $printSource = $(this);

        var PrintAreaWindow = PrintArea.getPrintWindow();

        PrintArea.write( PrintAreaWindow.doc, $printSource );

        setTimeout( function () { PrintArea.print( PrintAreaWindow ); }, 1000 );
    };

    var PrintArea = {
        print : function( PAWindow ) {
            var paWindow = PAWindow.win;

            $(PAWindow.doc).ready(function(){
                paWindow.focus();
                paWindow.print();

                if ( settings.mode == modes.popup && settings.popClose )
                    setTimeout(function() { paWindow.close(); }, 2000);
            });
        },
        write : function ( PADocument, $ele ) {
            PADocument.open();
            PADocument.write( PrintArea.docType() + "<html>" + PrintArea.getHead() + PrintArea.getBody( $ele ) + "</html>" );
            PADocument.close();
        },
        docType : function() {
            if ( settings.mode == modes.iframe ) return "";

            if ( settings.standard == standards.html5 ) return "<!DOCTYPE html>";

            var transitional = settings.standard == standards.loose ? " Transitional" : "";
            var dtd = settings.standard == standards.loose ? "loose" : "strict";

            return '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01' + transitional + '//EN" "http://www.w3.org/TR/html4/' + dtd +  '.dtd">';
        },
        getHead : function() {
            var extraHead = "";
            var links = "";

            if ( settings.extraHead ) settings.extraHead.replace( /([^,]+)/g, function(m){ extraHead += m });

            $(document).find("link")
                .filter(function(){ // Requirement: <link> element MUST have rel="stylesheet" to be considered in print document
                    var relAttr = $(this).attr("rel");
                    return ($.type(relAttr) === 'undefined') == false && relAttr.toLowerCase() == 'stylesheet';
                })
                .filter(function(){ // Include if media is undefined, empty, print or all
                    var mediaAttr = $(this).attr("media");
                    return $.type(mediaAttr) === 'undefined' || mediaAttr == "" || mediaAttr.toLowerCase() == 'print' || mediaAttr.toLowerCase() == 'all'
                })
                .each(function(){
                    links += '<link type="text/css" rel="stylesheet" href="' + $(this).attr("href") + '" >';
                });
            if ( settings.extraCss ) settings.extraCss.replace( /([^,\s]+)/g, function(m){ links += '<link type="text/css" rel="stylesheet" href="' + m + '">' });

            return "<head><title>" + settings.popTitle + "</title>" + extraHead + links + "</head>";
        },
        getBody : function ( elements ) {
            var htm = "";
            var attrs = settings.retainAttr;
            elements.each(function() {
                var ele = PrintArea.getFormData( $(this) );

                var attributes = ""
                for ( var x = 0; x < attrs.length; x++ )
                {
                    var eleAttr = $(ele).attr( attrs[x] );
                    if ( eleAttr ) attributes += (attributes.length > 0 ? " ":"") + attrs[x] + "='" + eleAttr + "'";
                }

                htm += '<div ' + attributes + '>' + $(ele).html() + '</div>';
            });

            return "<body>" + htm + "</body>";
        },
        getFormData : function ( ele ) {
            var copy = ele.clone();
            var copiedInputs = $("input,select,textarea", copy);
            $("input,select,textarea", ele).each(function( i ){
                var typeInput = $(this).attr("type");
                if ($.type(typeInput) === 'undefined') typeInput = $(this).is("select") ? "select" : $(this).is("textarea") ? "textarea" : "";
                var copiedInput = copiedInputs.eq( i );

                if ( typeInput == "radio" || typeInput == "checkbox" ) copiedInput.attr( "checked", $(this).is(":checked") );
                else if ( typeInput == "text" ) copiedInput.attr( "value", $(this).val() );
                else if ( typeInput == "select" )
                    $(this).find( "option" ).each( function( i ) {
                        if ( $(this).is(":selected") ) $("option", copiedInput).eq( i ).attr( "selected", true );
                    });
                else if ( typeInput == "textarea" ) copiedInput.text( $(this).val() );
            });
            return copy;
        },
        getPrintWindow : function () {
            switch ( settings.mode )
            {
                case modes.iframe :
                    var f = new PrintArea.Iframe();
                    return { win : f.contentWindow || f, doc : f.doc };
                case modes.popup :
                    var p = new PrintArea.Popup();
                    return { win : p, doc : p.doc };
            }
        },
        Iframe : function () {
            var frameId = settings.id;
            var iframeStyle = 'border:0;position:absolute;width:0px;height:0px;right:0px;top:0px;';
            var iframe;

            try
            {
                iframe = document.createElement('iframe');
                document.body.appendChild(iframe);
                $(iframe).attr({ style: iframeStyle, id: frameId, src: "#" + new Date().getTime() });
                iframe.doc = null;
                iframe.doc = iframe.contentDocument ? iframe.contentDocument : ( iframe.contentWindow ? iframe.contentWindow.document : iframe.document);
            }
            catch( e ) { throw e + ". iframes may not be supported in this browser."; }

            if ( iframe.doc == null ) throw "Cannot find document.";

            return iframe;
        },
        Popup : function () {
            var windowAttr = "location=yes,statusbar=no,directories=no,menubar=no,titlebar=no,toolbar=no,dependent=no";
            windowAttr += ",width=" + settings.popWd + ",height=" + settings.popHt;
            windowAttr += ",resizable=yes,screenX=" + settings.popX + ",screenY=" + settings.popY + ",personalbar=no,scrollbars=yes";

            var newWin = window.open( "", "_blank",  windowAttr );

            newWin.doc = newWin.document;

            return newWin;
        }
    };
})(jQuery);

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
            $("#deliveryDate").html(order.deliveryDate);
            $("#finishDate").html(order.finishDate);
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
        });

    }
};
//转换日期格式 原格式2017-03-21T13:08:03Z
//替换成2017-03-21
function changeDate(date) {
    // date = date.replace("T"," ");
    // date = date.replace("Z","");
    date = date.substring(0,10);
    return date;
}

//打印检测协议
// @Bind #printJiancexieyiBtn.onClick
!function(self,arg) {
    $("#jiancexieyiDiv").printArea({mode:"popup"});
};

//关闭检测协议
// @Bind #closeJiancexieyiBtn.onClick
!function(self,arg,jiancexieyiDialog) {
    jiancexieyiDialog.hide();
};

