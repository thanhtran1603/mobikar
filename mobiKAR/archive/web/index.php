<?php
	 //error_reporting(E_ERROR | E_WARNING | E_PARSE);
	 if (strstr($_SERVER['HTTP_ACCEPT'],'text/vnd.wap.wml') == true 
	  && strcmp($_SERVER['HTTP_HOST'],'mobikar.net') == 0){
	 	header('Location: http://wap.mobikar.net/');
	 	return;
	 }
	 else{
	 	header('Location: http://www.mobikar.net/start.html');
	 	return;
	}
?>