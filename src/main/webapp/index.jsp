<html>
<head>
	<script type="text/javascript">
		(function(){
			
			const eventSource = new EventSource("/serverSentEventWithKafka");
			eventSource.onmessage = function (event) {
				const jsonData = JSON.parse(event.data);
				console.log(jsonData);
				document.getElementById("foo").innerHTML += JSON. stringify(jsonData.after) + "<br /><br />";
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

