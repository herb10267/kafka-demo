<html>
<head>
	<script type="text/javascript">
		(function(){
			var eventSource = new EventSource("/serverSentEventDemo");
			eventSource.onmessage = function (event) {
				console.log("data: " + event.data)
				document.getElementById("foo").innerHTML += event.data + "<br />";
			};
		})();
	</script>
</head>
<body>
data: <span id="foo"></span>
<br />
<br />
</body>
</html>

