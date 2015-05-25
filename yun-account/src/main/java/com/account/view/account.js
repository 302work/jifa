(function ($) {
    var printAreaCount = 0;
    $.fn.printArea = function () {
        var ele = $(this);
        var idPrefix = "printArea_";
        removePrintArea(idPrefix + printAreaCount);
        printAreaCount++;
        var iframeId = idPrefix + printAreaCount;
        var iframeStyle = 'position:absolute;width:0px;height:0px;left:-500px;top:-500px;';
        iframe = document.createElement('IFRAME');
        $(iframe).attr({
            style: iframeStyle,
            id: iframeId
        });
        document.body.appendChild(iframe);
        var doc = iframe.contentWindow.document;
        $(document).find("link").filter(function () {
            return $(this).attr("rel").toLowerCase() == "stylesheet";
        }).each(
                function () {
                    doc.write('<link type="text/css" rel="stylesheet" href="'
                            + $(this).attr("href") + '" >');
                });
        doc.write('<div class="' + $(ele).attr("class") + '">' + $(ele).html()
                + '</div>');
        doc.close();
        var frameWindow = iframe.contentWindow;
        frameWindow.close();
        frameWindow.focus();
        frameWindow.print();
    };
    var removePrintArea = function (id) {
        $("iframe#" + id).remove();
    };
})(jQuery);



var dirtyEntityNum = 0;

function refreshActions() {
	var node = view.get("#dataTreeAccount.currentNode");
	view.set({
		"^onCurrentNode.disabled" : node == null
	});
	var disabled = true;
	if (node != null) {
		var entity = node.get("data");
		disabled = !entity.isDirty();
	}
	view.set({
		"^onDirtyNode.disabled" : disabled,
		"^onDirty.disabled" : dirtyEntityNum == 0
	});
}

// @Bind @Account.onStateChange
// @Bind @Record.onStateChange
!function(arg) {
	if (arg.oldState == dorado.Entity.STATE_NONE) {
		dirtyEntityNum++;
	} else if (arg.newState == dorado.Entity.STATE_NONE) {
		dirtyEntityNum--;
	} else if (arg.oldState == dorado.Entity.STATE_NEW
			&& arg.newState == dorado.Entity.STATE_DELETED) {
		dirtyEntityNum--;
	}
	refreshActions();
};

// @Bind #dataTreeAccount.onContextMenu
!function(self, arg) {
	view.id("menuAccounts").show({
		position : {
			left : arg.event.pageX,
			top : arg.event.pageY
		}
	});
};

//@Bind #actionAdd.onExecute
!function(dsAccounts, dataTreeAccount) {
	var currentEntity = dsAccounts.getData("!CURRENT_ACCOUNT");	
	if (currentEntity) {
		var data = {
				name : "<新分类>",
				parentAccountId : currentEntity.get("id")
			};
		currentEntity.createChild("child", data);
	} else {
		dsAccounts.getData().createChild({name:"<新分类>"});
	}
	var currentNode = dataTreeAccount.get("currentNode");
	if (currentNode) {
		currentNode.expandAsync();
	}
	
};

//@Bind #actionRemove.onExecute
!function(dsAccounts) {
	var currentEntity = dsAccounts.getData("!CURRENT_ACCOUNT");
	if (currentEntity) {
	  view.id("ajaxActionCheckAccountChildren").set("parameter", currentEntity.get("id")).execute(function(count) {
	    if (count > 0) {
	      dorado.MessageBox.alert("请先删除子分类");
	    } else {
	      currentEntity.remove();        
	    }
	  });
	}
};

// @Bind #actionCancel.onExecute
!function(dsAccounts) {
	var entity = dsAccounts.getData("!CURRENT_ACCOUNT");
	if (entity) {
		entity.cancel();
	}
};

// @Bind #actionSaveAll.onSuccess
!function() {
	dirtyEntityNum = 0;
	refreshActions();
};

// @Bind #dataTreeAccount.onCurrentChange
!function(self, arg) {
	view.id("cardbook").set("currentControl", 1);
	refreshActions();

};

// @Bind #printBtn.onClick
!function(self, arg) {
	$("#d_recordDataGrid").printArea();
};


