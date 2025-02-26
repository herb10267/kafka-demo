<html>
<head>
	<script type="text/javascript">
		(function(){
			
			const eventSource = new EventSource("/serverSentEventWithKafka");
			eventSource.onmessage = function (event) {
				const jsonData = JSON.parse(event.data);
				console.log(jsonData);
				
				const now = new Date();
				const hours = now.getHours().toString().padStart(2, '0');
				const minutes = now.getMinutes().toString().padStart(2, '0');
				const seconds = now.getSeconds().toString().padStart(2, '0');
				const milliseconds = now.getMilliseconds().toString().padStart(3, '0');
				const timeString = `${hours}:${minutes}:${seconds}.${milliseconds}`;
				
				const displayContent = `<div><span style="color:#666; font-size:0.9em;">[${timeString}]</span> ${JSON.stringify(jsonData.after)}</div><br />`;
				document.getElementById("data").insertAdjacentHTML('afterbegin', displayContent);
			};
			
		})();
	</script>
</head>
<body>
<span>SSE data flow :</span><br/><br/>
<span id="data"></span>
<br />
<br />
</body>
</html>

