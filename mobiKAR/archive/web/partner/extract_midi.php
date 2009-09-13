<?php

error_reporting(E_ALL);
$PHP_SELF = $_SERVER['PHP_SELF'];
$file=(isset($_FILES['mid_upload'])&&$_FILES['mid_upload']['tmp_name']!='')?$_FILES['mid_upload']['tmp_name']:'';
?>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Meta-Messages</title>
<style>
body {font-family:arial;font-size:11px;margin:5px;}
input {font-family:arial;font-size:11px}
</style>
</head>
<body>

<form enctype="multipart/form-data" action="<?=$PHP_SELF?>" method="post"
onsubmit="if (this.mid_upload.value==''){alert('Please choose a mid-file
to upload!');return false}"> <input type="hidden" name="MAX_FILE_SIZE"
	value="1048576"><!-- 1 MB --> MIDI file (*.mid) to upload: <input
	type="file" name="mid_upload"> <br>
<input type="submit" value=" send "></form>
<?php
if ($file!=''){
	echo 'File: '.$_FILES['mid_upload']['name'];
	echo '<hr><pre>';

	/****************************************************************************
	MIDI CLASS CODE
	****************************************************************************/
	require('./classes/midi.class.php');
	require ('../ConvertCharset.class.php');
	$NewEncoding = new ConvertCharset;

	$midi = new Midi();
	$midi->importMid($file,0);
	$track = $midi->getTrack(0);

	// list of meta events that we are interested in (adjust!)
	$texttypes = array('Text','Copyright','TrkName','InstrName','Lyric','Marker','Cue');

	$lyric = array();

	// poustawianie czasów trwania
	foreach ($track as $msgStr){
		//print_r($msgStr);
		$msg = explode(' ',$msgStr);
		if ($msg[1]=='Meta'&&in_array($msg[2],$texttypes)){
			$milis = (int)($msg[0] * $midi->getTempo() / $midi->getTimebase() / 1000);
			$text = $NewEncoding->Convert(substr($msgStr,strpos($msgStr,'"')), "windows-1250", "utf-8");
			$text = str_replace("_", " ", $text);
			$text = str_replace("\n", "/", $text);
			$text = str_replace("\r", "/", $text);
			$text = str_replace("//", "/", $text);
			$text = str_replace("//", "/", $text);
			$text = substr($text, 1, strlen($text) - 2);
			array_push($lyric, array($milis, 0, $text));
		}
	}
	define("SYLABE_TIME_MIN", 50);
	define("SYLABE_TIME_MAX", 1000);
	define("SYLABE_GAP_MIN", 100);

	$oldObjs = null;
	foreach($lyric as $arrKey => $newObjs){
		if ($oldObjs == null){
			$oldObjs = $newObjs;
		}
		$delta = $newObjs[0] -$oldObjs[0] - SYLABE_GAP_MIN;
		$delta = (int)(min(SYLABE_TIME_MAX, max(0, $delta)));
		$oldObjs[1] = $delta;
		if ($oldObjs == $newObjs)
		continue;
		$lyric[$arrKey-1] = $oldObjs;
		$oldObjs = $newObjs;
	}
    // usuwanie czasów trwania 0

	function array_remove($array, $key){
		$newArray = array();
		foreach($array as $key2 => $obj){
			if ($key2 == $key)
			continue;
			array_push($newArray, $obj);
		}
		return $newArray;
	}
	
	
	$isFinished = FALSE;
	while ( $isFinished == FALSE){
		foreach($lyric as $key => $obj){
			$objs = &$lyric[$key];
			$delta = $objs[1];
			// sprawdzamy czy to nie koniec
			if (array_key_exists($key+1, $lyric)){
				if ($delta <= SYLABE_TIME_MIN){
					$objsNew = &$lyric[$key+1];
					$objsNew[2] = $objs[2] . $objsNew[2];
					$objsNew[2] = str_replace("//", "/", $objsNew[2]);
					$newArray = array();
					foreach($lyric as $key3 => $obj3){
						if ($key == $key3)
						continue;
						array_push($newArray, $obj3);
					}
					$lyric = $newArray;
					// od początku
					break;
				}
			}else {
				$isFinished = TRUE;
				break;
			}
		}
		
	}
	foreach($lyric as $objs){
		print("$objs[0]\t$objs[1]\t0\t$objs[2]\n");
	}
	echo '</pre>';
}
?>
</body>
</html>
