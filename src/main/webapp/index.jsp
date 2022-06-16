<html>
<head>
	<script type="text/javascript">
		(function(){
			// var eventSource = new EventSource("/serverSentEventDemo");
			// var eventSource = new EventSource("/serverSentEventWithAsync");
			// eventSource.onmessage = function (event) {
			// 	console.log("data: " + event.data)
			// 	document.getElementById("foo").innerHTML += event.data + "<br />";
			// };
			
			var eventSource = new EventSource("/serverSentEventWithKafka");
			eventSource.onmessage = function (event) {
				const jsonData = JSON.parse(event.data);
				document.getElementById("foo").innerHTML += JSON. stringify(jsonData.payload.after) + "<br /><br />";
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

