$(document).ready(function(){
	var delDC_id="";
	var delHost_id="";
	var delVm="";
	$("#datacenters-table").on("click","tr",function(){
		delDC_id=$(this).attr("id");
		$(".DC-selected-name").text(delDC_id);
		$(".hosts").show();
		$("#hosts-table tbody tr").hide();
		$("tr[class^='"+delDC_id+"']").show();//显示对应的tr
	});
	
	$("#testdiv ul").on("click","li", function() {
	     //do something here
	 });
	
	$("#vm-add").bind("click",function(){
		var length=$("#cloudlet-table tbody tr").length;
		var newRow="<tr>" +
				"<td><input type='text' name='vm_id' value='"+(length+1)+"'></input></td>" +
				"<td><input type='text' name='vm_ram' value='512'></input></td>" +
				"<td><input type='text' name='vm_storage' value='1000'></input></td>" +
				"<td><input type='text' name='vm_bw' value='1000'></input></td>"+
				"<td><input type='text' name='vm_mips' value='500'></input></td>"+
				"<td><input type='text' name='vm_pe' value='1'></input></td>"+
				"<td><input type='text' name='vm_technology' value='Xen'></input></td>"+
				"<td><select class='mechanism' name='vm_mechanism'><option value='1'>TimeShared</option><option value='2'>SpaceShared</option></select></td>" +
			"</tr>";
		$("#cloudlet-table tbody").append(newRow);	
	
	});
	$("#cloudlet-table tbody").on("click","tr",function(){
		$("#cloudlet-table tbody tr[name='vmdel']").attr("name","");//清空delvm
//		delVm=$(this).children().children().eq(0).val();//
		$(this).attr("name","vmdel");
		
	});
	$("#vm-remove").bind("click",function(){//删除vm
		$("#cloudlet-table tbody tr[name='vmdel']").remove();
		length=$("#cloudlet-table tbody tr").length;
		for(var i=0;i<length;i++)
		{
			$("#cloudlet-table tbody tr").eq(i).children().children().eq(0).val(i+1);
		}
		
	});
//	$(".dcbelong").click(){
//		
//	};
	$(this).on("click",".dcbelong",function(){
		var trlength=$("#datacenters-table tr").length-1;
		$(this).html("");
		for(var i=0;i<trlength;i++){
			$(this).append("<option>dc"+(i+1)+"</option>");
		}
	});
	
	
	$("#dc-add").bind("click",function(){
		var trlength=$("#datacenters-table tr").length;
		// 10以内添加0x形式
		var addDC_id = null;
		if(trlength < 10){
			addDC_id = "dc0" + trlength;
		}else{
			addDC_id = "dc" +  trlength;
		}
		var newRow="<tr class='panel2-tr' id='"+addDC_id+"'>"+
										"<td><input type='text' name='dc_id' value='"+addDC_id+"'></input></td>"+
										"<td><select name='dc_ragion'><option value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option></select></td>"+
										"<td><input type='text' name='dc_arch' value='x86'></input></td>"+
										"<td><input type='text' name='dc_os' value='Linux'></input></td>"+
										"<td><input type='text' name='dc_vmtechnology' value='Xen'></input></td>"+
										"<td><input type='text' name='dc_pecost' value='0.1'></input></td>"+
										"<td><input type='text' name='dc_ramcost' value='0.05'></input></td>"+
										"<td><input type='text' name='dc_storagecost' value='0.1'></input></td>"+
										"<td><input type='text' name='dc_bwcost' value='2'></input></td>"+
										"<td><input type='text' name='dc_hostnum' value='1'></input></td>"+
									"</tr>";
//		newRow="<tr class=\"panel2-tr\"><td><input value=\"yige\"></input></td></tr>";
		// $("#datacenters-table tr:last").after(newRow);
		$("#datacenters-table tbody").append(newRow);
//		alert(newRow);
		$(".hosts").show();//hosts列表进行操作
		$("#hosts-table tbody tr").hide();
		
		var newRowHost = null;
		if(trlength < 10){
			newRowHost = "<tr class='" +addDC_id+
			"-hosts'>" +
			"<input type='hidden' name='host_dcid' value='"+addDC_id+"'></input>" +
			"<td><input type='text' name='host_id' value='0"+trlength+"01'></input></td>" +
			"<td><input type='text' name='host_ram' value='2048'></input></td>" +
			"<td><input type='text' name='host_storage' value='100000'></input></td>" +
			"<td><input type='text' name='host_bw' value='10000'></input></td>" +
			"<td><input type='text' name='host_mips' value='1000'></input></td>" +
			"<td><input type='text' name='host_pe' value='1'></input></td>" +
			"<td><select class='mechanism' name='host_mechanism'><option value='1'>TimeShared</option><option value='2'>SpaceShared</option></select></td>"+
			"</tr>";
		}else{
			newRowHost = "<tr class='" +addDC_id+
			"-hosts'>" +
			"<input type='hidden' name='host_dcid' value='"+addDC_id+"'></input>" +
			"<td><input type='text' name='host_id' value='"+trlength+"01'></input></td>" +
			"<td><input type='text' name='host_ram' value='2048'></input></td>" +
			"<td><input type='text' name='host_storage' value='100000'></input></td>" +
			"<td><input type='text' name='host_bw' value='10000'></input></td>" +
			"<td><input type='text' name='host_mips' value='1000'></input></td>" +
			"<td><input type='text' name='host_pe' value='1'></input></td>" +
			"<td><select class='mechanism' name='host_mechanism'><option value='1'>TimeShared</option><option value='2'>SpaceShared</option></select></td>"+
			"</tr>";
		}
		
//		$("# tr:last").after(newRowHost);
		$("#hosts-table tbody").append(newRowHost);
		
		$(".dcbelong").append("<option>dc"+trlength+"</option>");
		$("tr[class^='"+addDC_id+"']").show();//显示对应的tr
		$(".DC-selected-name").text(addDC_id);
		delDC_id=addDC_id;
	});
	

		//删除 数据中心
	 $("#dc-remove").bind("click",function(){
	 	$("#datacenters-table tr[id="+delDC_id+"]").remove();
	 	$("#hosts-table tr[class="+delDC_id+"-hosts]").remove();
	 	delDC_id="";
	 	$(".DC-selected-name").text("");
		var datacenterLength=$("#datacenters-table tbody tr").length;
		var i=1;
		
		$("#datacenters-table tbody").find("tr").each(function(){
			var temp=$(this).attr("id");
			$(this).attr("id","dc"+i);
			$(this).children().children().eq(0).val("dc"+i);
			$("#hosts-table tbody").find("tr").each(function(){
				if($(this).attr("class")==(temp+"-hosts")){
					$(this).attr("class","dc"+i+"-hosts");
				}
			});
			i++;
		});
		
//		$(".dcbelong").html("<option></option>");
		$(".dcbelong option:last").remove(); 
	 });


	$("#hosts-add").bind("click",function(){
		//计算长度
		var Dc_Id=delDC_id.replace(/dc\s*/,'');
		var tr_length=$(".hosts tr[class^='dc"+Dc_Id+"']").length + 1;
		
		var newRowHost = null;
		if(tr_length < 10){
			newRowHost="<tr class='dc" +Dc_Id+
			"-hosts'>" +
			"<input type='hidden' name='host_dcid' value='dc"+Dc_Id+"'></input>"+
			"<td><input type='text' name='host_id' value='"+Dc_Id+"0"+tr_length+"'></input></td>" +
			"<td><input type='text' name='host_ram' value='2048'></input></td>" +
			"<td><input type='text' name='host_storage' value='100000'></input></td>" +
			"<td><input type='text' name='host_bw' value='10000'></input></td>" +
			"<td><input type='text' name='host_mips' value='1000'></input></td>" +
			"<td><input type='text' name='host_pe' value='1'></input></td>" +
			"<td><select class='mechanism' name='host_mechanism'><option value='1'>TimeShared</option><option value='2'>SpaceShared</option></select></td>"+
			"</tr>";
		}else{
			newRowHost="<tr class='dc" +Dc_Id+
			"-hosts'>" +
			"<input type='hidden' name='host_dcid' value='dc"+Dc_Id+"'></input>"+
			"<td><input type='text' name='host_id' value='"+Dc_Id+tr_length+"'></input></td>" +
			"<td><input type='text' name='host_ram' value='2048'></input></td>" +
			"<td><input type='text' name='host_storage' value='100000'></input></td>" +
			"<td><input type='text' name='host_bw' value='10000'></input></td>" +
			"<td><input type='text' name='host_mips' value='1000'></input></td>" +
			"<td><input type='text' name='host_pe' value='1'></input></td>" +
			"<td><select class='mechanism' name='host_mechanism'><option value='1'>TimeShared</option><option value='2'>SpaceShared</option></select></td>"+
			"</tr>";
		}
		
		$("#hosts-table tbody").append(newRowHost);
		
		var temp=$("#datacenters-table tbody tr[id=dc"+Dc_Id+"]").children().children().eq(9).val();
		$("#datacenters-table tbody tr[id=dc"+Dc_Id+"]").children().children().eq(9).val(temp-1+2);
		
	});
	
	$("#hosts-table").on("click","tr",function(){//溢出hosts配置
		$("#hosts-table tr[id=del]").attr("id","");
		$(this).attr("id","del");
	});
	$("#hosts-remove").bind("click",function(){
		var i=1;
		var classname=$("#hosts-table tr[id=del]").attr("class");
		$("#hosts-table tr[id=del]").remove();
		$("#hosts-table tbody").find("tr[class="+classname+"]").each(function(){
			$(this).children().children().eq(0).val(i);
			i++;
		});
		
		
	});
});


function fun1(btn)
{
	alert($(btn).text());
	
}
function arrayToJson(o) {     
    var r = [];     
    if (typeof o == "string") return "\"" + o.replace(/([\'\"\\])/g, "\\$1").replace(/(\n)/g, "\\n").replace(/(\r)/g, "\\r").replace(/(\t)/g, "\\t") + "\"";     
    if (typeof o == "object") {     
    if (!o.sort) {     
    for (var i in o)     
    r.push(i + ":" + arrayToJson(o[i]));     
    if (!!document.all && !/^\n?function\s*toString\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)) {     
    r.push("toString:" + o.toString.toString());     
    }     
    r = "{" + r.join() + "}";     
    } else {     
    for (var i = 0; i < o.length; i++) {     
    r.push(arrayToJson(o[i]));     
    }     
    r = "[" + r.join() + "]";     
    }     
    return r;     
    }     
    return o.toString();     
   }    