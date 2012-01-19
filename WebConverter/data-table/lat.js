	
	function processURL() 
	{
		var params = document.URL.split("?");
		
		if (params.length < 2) alert("No group specified for lattice diagrams");		
		else 
		{
			var gID = params[1];
			var order = gID.split("-")[0];
			document.getElementById("return-top").href = params[3] + ".html#" + params[2] + order;
			document.getElementById("return-bot").href = params[3] + ".html#" + params[2] + order;
			
			var title = "Fix-orders of group " + gID;
			document.title = title;
			document.getElementById("titleH1").innerHTML = title;
		
			var cells = document.getElementsByTagName("td");
			for (var i=0;i<cells.length;i++)
			{
				var ci = cells[i];
				if (ci.id.length > 0)
				{
					var imgElem = document.createElement('img');
					imgElem.src = "img/g-" + gID + "." + ci.id;
					ci.appendChild(imgElem);
				}
			}
		}
		
	}


	function isClear(id)
	{
		return !($("#"+id).is(':checked'));
	}

	function setVis()
	{
		// show everything:
		$("td").show();
		$("th").show();
		
		// hide elements meant to be hidden
		var opts = $("input");
		for (var i=0;i<opts.length;i++)
		{
			if (isClear(opts[i].id)) 
				$("." + opts[i].id).hide(); 
		}		

		// special case: autoH tablecell
		(isClear("row1") && isClear("row2")) ? $("#autoH").hide() : $("#autoH").show();
	}

	window.onload = processURL;
