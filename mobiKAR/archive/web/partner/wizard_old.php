<?php
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utils.php');
// parametry
$par_request = getParameter('request', NULL);
$par_partner = getParameter('partner', "0");
$par_format = getParameter('format', "xhtml");
$par_lang = getParameter('lang', "pl");
$par_css = getParameter('css', "1");
$par_dir = getParameter('dir', "dir");

$message = NULL;

define('M1K0_REASON_NO_DIR', 1);
define('M1K0_REASON_LOAD', 2);
define('M1K0_REASON_NO_TEXT', 3);

$messages = array(
	M1K0_REASON_NO_DIR => "Nie można stworzyc katalou",
	M1K0_REASON_LOAD => "Plik nie zosta wczytany",
	M1K0_REASON_NO_TEXT => "Brak tekstu do przetworzenia",
);

$reason = 0;

$lyric_text = "";

if (strcmp($par_request, 'upload_kar') == 0) {
	$file = (isset ($_FILES['file_kar']) && $_FILES['file_kar']['tmp_name'] != '') ? $_FILES['file_kar']['tmp_name'] : '';
	if ($file != '') {
		$par_dir = getCode();
		// skopiowanie pliku
		$path = "../wap/get/" . $par_dir;
		// TODO: sprawdzi, czy taki kod już jest w filesystemie
		if (mkdir($path) == FALSE) {
			$reason = M1K0_REASON_NO_DIR;
			$par_request = NULL;
		} else {
			copy($file, $path . "/song.midi");
			require ('../scripts/midi.class.php');
			require ('../scripts/ConvertCharset.class.php');
			$NewEncoding = new ConvertCharset;

			$midi = new Midi();
			$midi->importMid($file, 0);
			$track = $midi->getTrack(0);

			// list of meta events that we are interested in (adjust!)
			$texttypes = array (
				'Text',
				'Copyright',
				'TrkName',
				'InstrName',
				'Lyric',
				'Marker',
				'Cue'
			);

			$lyric = array ();

			// poustawianie czasów trwania
			foreach ($track as $msgStr) {
				//print_r($msgStr);
				$msg = explode(' ', $msgStr);
				if ($msg[1] == 'Meta' && in_array($msg[2], $texttypes)) {
					$milis = (int) ($msg[0] * $midi->getTempo() / $midi->getTimebase() / 1000);
					$text = $NewEncoding->Convert(substr($msgStr, strpos($msgStr, '"')), "windows-1250", "utf-8");
					$text = str_replace("_", " ", $text);
					$text = str_replace("\n", "/", $text);
					$text = str_replace("\r", "/", $text);
					$text = str_replace("//", "/", $text);
					$text = str_replace("//", "/", $text);
					$text = substr($text, 1, strlen($text) - 2);
					array_push($lyric, array (
						$milis,
						0,
						$text
					));
				}
			}
			define("SYLABE_TIME_MIN", 50);
			define("SYLABE_TIME_MAX", 1000);
			define("SYLABE_GAP_MIN", 100);

			$oldObjs = null;
			foreach ($lyric as $arrKey => $newObjs) {
				if ($oldObjs == null) {
					$oldObjs = $newObjs;
				}
				$delta = $newObjs[0] - $oldObjs[0] - SYLABE_GAP_MIN;
				$delta = (int) (min(SYLABE_TIME_MAX, max(0, $delta)));
				$oldObjs[1] = $delta;
				if ($oldObjs == $newObjs)
					continue;
				$lyric[$arrKey -1] = $oldObjs;
				$oldObjs = $newObjs;
			}
			// usuwanie czasów trwania 0

			$isFinished = FALSE;
			while ($isFinished == FALSE) {
				foreach ($lyric as $key => $obj) {
					$objs = & $lyric[$key];
					$delta = $objs[1];
					// sprawdzamy czy to nie koniec
					if (array_key_exists($key +1, $lyric)) {
						if ($delta <= SYLABE_TIME_MIN) {
							$objsNew = & $lyric[$key +1];
							$objsNew[2] = $objs[2] . $objsNew[2];
							$objsNew[2] = str_replace("//", "/", $objsNew[2]);
							$newArray = array ();
							foreach ($lyric as $key3 => $obj3) {
								if ($key == $key3)
									continue;
								array_push($newArray, $obj3);
							}
							$lyric = $newArray;
							// od początku
							break;
						}
					} else {
						$isFinished = TRUE;
						break;
					}
				}

			}

			$lyric_text = "MILLIS\t" . 12345 . "\nTITLE\t<empty>\nARTIST\t<empty>\nMUSIC\t<empty>\nLYRICS\t<empty>\nCREATOR\t<empty>\nVERSION\t<empty>\n";
			foreach ($lyric as $objs) {
				if ($objs[0] == 0) {
					$lyric_text .= "//";
				}
				$lyric_text .= "$objs[0]\t$objs[1]\t0\t$objs[2]\n";
			}
			$lyric_text = mb_convert_encoding($lyric_text, "HTML-ENTITIES", "utf-8");

		}
	} else {
		$reason = M1K0_REASON_LOAD;
		$par_request = NULL;
	}
}
elseif (strcmp($par_request, 'upload_mlyr') == 0) {
	$file_midi = (isset ($_FILES['file_midi']) && $_FILES['file_midi']['tmp_name'] != '') ? $_FILES['file_midi']['tmp_name'] : '';
	$file_mlyr = (isset ($_FILES['file_mlyr']) && $_FILES['file_mlyr']['tmp_name'] != '') ? $_FILES['file_mlyr']['tmp_name'] : '';
	if ($file_midi == '' || $file_mlyr == '') {
		$reason = M1K0_REASON_LOAD;
		$par_request = NULL;
	} else {
		$par_dir = getCode();
		// skopiowanie pliku
		$path = "../wap/get/" . $par_dir;
		// TODO: sprawdzi, czy taki kod już jest w filesystemie
		if (mkdir($path) == FALSE) {
			$reason = M1K0_REASON_NO_DIR;
			$par_request = NULL;
		} else {
			copy($file_midi, $path . "/song.midi");
		}

		$fh = fopen($file_mlyr, 'rb');
		if ($fh != FALSE) {

			$title = "title";
			$music = "music";
			$lyrics = "lyrics";
			$artist = "artist";
			$creator = "creator";
			$version = "version";
			$note = "note";
			$text = "";
			$milis = 0;
			$idxs = array ();
			$extractedData = array ();

			$tag_name = ""; // nazwa czastki - chunk-a
			$tag_size = 0; // rozmiar czastki - chunk-a
			$tag_name = fread($fh, 4);
			if (strcmp("mLYR", $tag_name) != 0) {
				return M1K0_REASON_UNKNOWN_FILE_FORMAT;
			}
			$tag_size = current(unpack("N", fread($fh, 4)));
			do {
				$tag_name = fread($fh, 4);
				$tag_size = current(unpack("N", fread($fh, 4)));
				if (strcmp("TITL", $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$title = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp("ARTI", $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$artist = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp("MUSI", $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$music = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp("LYRI", $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$lyrics = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp("CREA", $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$creator = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp('VER$', $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$version = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp("NOTE", $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$note = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp("MSEK", $tag_name) == 0) {
					$a = fread($fh, 4); // to jest nieważne...
					$milis = current(unpack("N", fread($fh, 4)));
				}
				elseif (strcmp("TEXT", $tag_name) == 0) {
					$string_length = current(unpack("n", fread($fh, 2)));
					$text = mb_convert_encoding(fread($fh, $string_length), "HTML-ENTITIES", "UTF-8");
				}
				elseif (strcmp("IDXS", $tag_name) == 0) {
					$count = current(unpack("N", fread($fh, 4)));
					for ($i = 0; $i < $count; $i++) {
						$pack1 = current(unpack("N", fread($fh, 4)));
						$pack2 = current(unpack("N", fread($fh, 4)));
						array_push($idxs, $pack1);
						array_push($idxs, $pack2);
						array_push($extractedData, unpackData($pack1, $pack2));
					}
					break;
				} else {
					fread($fh, $tag_size);
				}
			} while (TRUE);

			fclose($fh);

			// przelecimy wgrany plik
			$lyric_text = "MILLIS\t" . $milis . "\nTITLE\t$title\nARTIST\t$artist\nMUSIC\t<empty>\nLYRICS\t$lyrics\nCREATOR\t$creator\nVERSION\t$version\nNOTE\t$note\n";
			foreach ($extractedData as $objs) {
				/*
				 * Array
				(
				    [pos1_24bit] => 223907
				    [pos2_16bit] => 150
				    [pos3_16bit] => 712
				    [pos4_6bit] => 5
				    [pos5_1bit] => 0
				)

				 */
				$substr = mb_substr($text, $objs['pos3_16bit'], $objs['pos4_6bit'], "HTML-ENTITIES");
				$lyric_text .= $objs['pos1_24bit']."\t".$objs['pos2_16bit']."\t".$objs['pos5_1bit']."\t".$substr."\n";
			}

		}

		$par_request = 'upload_kar';

	}
	//	print_r($_FILES);
	//	printf("file_midi:%s\n",$file_midi);
	//	printf("file_mlyr:%s\n",$file_mlyr);

}
elseif (strcmp($par_request, 'changed_text') == 0) {
	// odczytac wprowadzony tekst
	$lyric_text = getParameter("lyric", NULL);
	if ($lyric_text == NULL || strlen(trim($lyric_text)) == 0) {
		$reason = M1K0_REASON_NO_TEXT;
		$par_request = NULL;
	} else {

		require ('../scripts/ConvertCharset.class.php');
		$NewEncoding = new ConvertCharset;

		$title = "title";
		$music = "music";
		$lyrics = "lyrics";
		$artist = "artist";
		$creator = "creator";
		$version = "version";
		$note = "note";
		$text = "";
		$milis = 0;
		$idxs = array ();
		$lines = split("\n", $lyric_text);
		foreach ($lines as $line) {
			$line = str_replace("\r", "", $line);
			$line = str_replace("\n", "", $line);
			$parts = split("\t", $line);
			print ("<!--");
			print_r($parts);
			print ("-->");
			if (count($parts) == 2) {
				if (strcmp($parts[0], "TITLE") == 0) {
					$title = trim($parts[1]);
				}
				if (strcmp($parts[0], "ARTIST") == 0) {
					$artist = trim($parts[1]);
				}
				if (strcmp($parts[0], "MUSIC") == 0) {
					$music = trim($parts[1]);
				}
				if (strcmp($parts[0], "LYRICS") == 0) {
					$lyrics = trim($parts[1]);
				}
				if (strcmp($parts[0], "CREATOR") == 0) {
					$creator = trim($parts[1]);
				}
				if (strcmp($parts[0], "VERSION") == 0) {
					$version = trim($parts[1]);
				}
				if (strcmp($parts[0], "NOTE") == 0) {
					$note = trim($parts[1]);
				}
				if (strcmp($parts[0], "MILLIS") == 0) {
					$milis = trim($parts[1]);
				}
			}
			if (count($parts) == 4) {

				$timeStart = $parts[0];
				$timeLong = $parts[1];
				$singer = $parts[2];
				$sylabe = $parts[3];
				$sylabe_ascii = mb_convert_encoding($sylabe, "ISO-8859-1", "UTF-8");
				// $sylLen to liczba liter a nie bajtów!!!
				$sylLen = strlen($sylabe_ascii);
				$text_ascii = mb_convert_encoding($text, "ISO-8859-1", "UTF-8");
				$sylIdx = strlen($text_ascii);
				$text .= $sylabe;

				packData($idxs, $timeStart, $timeLong, $sylIdx, $sylLen, $singer);
			}
		}

		print ("<!--\n");
		//print("text: " . $text . "\n");
		$path = "../wap/get/" . $par_dir;
		$fh = fopen($path . "/song.midi.mlyr", "wb");
		// TODO: wstawi skopilowany kod
		fwrite($fh, "mLYR");
		$posMLYR = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, "TITL");
		$posTITL = ftell($fh);
		print ("posTITL:" . $posTITL . "\n");
		fwrite($fh, pack("N", 0)); // wielkosc chunka
		fwrite($fh, pack("n", 0)); // dwa bajty dla dlugosci tekstu dla kompatybilnoasci z readUTF8()
		fwrite($fh, $title);
		$posEOF = ftell($fh);
		fseek($fh, $posTITL);
		fwrite($fh, pack("N", $posEOF - $posTITL -4));
		fwrite($fh, pack("n", $posEOF - $posTITL -4 - 2));
		// Muzyka
		fseek($fh, $posEOF);
		fwrite($fh, "MUSI");
		$posMUSI = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("n", 0));
		fwrite($fh, $music);
		$posEOF = ftell($fh);
		fseek($fh, $posMUSI);
		fwrite($fh, pack("N", $posEOF - $posMUSI -4));
		fwrite($fh, pack("n", $posEOF - $posMUSI -4 - 2));
		// Tekst
		fseek($fh, $posEOF);
		fwrite($fh, "LYRI");
		$posLYRI = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("n", 0));
		fwrite($fh, $lyrics);
		$posEOF = ftell($fh);
		fseek($fh, $posLYRI);
		fwrite($fh, pack("N", $posEOF - $posLYRI -4));
		fwrite($fh, pack("n", $posEOF - $posLYRI -4 - 2));
		// Artysta - wykonawca
		fseek($fh, $posEOF);
		fwrite($fh, "ARTI");
		$posARTI = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("n", 0));
		fwrite($fh, $artist);
		$posEOF = ftell($fh);
		fseek($fh, $posARTI);
		fwrite($fh, pack("N", $posEOF - $posARTI -4));
		fwrite($fh, pack("n", $posEOF - $posARTI -4 - 2));
		// Twórca opracowania
		fseek($fh, $posEOF);
		fwrite($fh, "CREA");
		$posCREA = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("n", 0));
		fwrite($fh, $creator);
		$posEOF = ftell($fh);
		fseek($fh, $posCREA);
		fwrite($fh, pack("N", $posEOF - $posCREA -4));
		fwrite($fh, pack("n", $posEOF - $posCREA -4 - 2));
		// wersja opracowania
		fseek($fh, $posEOF);
		fwrite($fh, 'VER$');
		$posVERS = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("n", 0));
		fwrite($fh, $version);
		$posEOF = ftell($fh);
		fseek($fh, $posVERS);
		fwrite($fh, pack("N", $posEOF - $posVERS -4));
		fwrite($fh, pack("n", $posEOF - $posVERS -4 - 2));
		// notatka
		fseek($fh, $posEOF);
		fwrite($fh, 'NOTE');
		$posNOTE = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("n", 0));
		fwrite($fh, $note);
		$posEOF = ftell($fh);
		fseek($fh, $posNOTE);
		fwrite($fh, pack("N", $posEOF - $posNOTE -4));
		fwrite($fh, pack("n", $posEOF - $posNOTE -4 - 2));
		// czast trwania w int4
		fseek($fh, $posEOF);
		fwrite($fh, 'MSEK');
		$posMSEK = ftell($fh);
		fwrite($fh, pack("N", 8)); // 8 - long
		fwrite($fh, pack("N", 0)); // pierwsza czastak longa
		fwrite($fh, pack("N", $milis)); // druga czastak longa
		$posEOF = ftell($fh);
		// brak cofania sie
		// tekst
		fseek($fh, $posEOF);
		fwrite($fh, 'TEXT');
		$posTEXT = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("n", 0));
		fwrite($fh, $text);
		$posEOF = ftell($fh);
		fseek($fh, $posTEXT);
		fwrite($fh, pack("N", $posEOF - $posTEXT -4));
		fwrite($fh, pack("n", $posEOF - $posTEXT -4 - 2));
		// indeksy
		fseek($fh, $posEOF);
		fwrite($fh, 'IDXS');
		$posIDXS = ftell($fh);
		fwrite($fh, pack("N", 0));
		fwrite($fh, pack("N", count($idxs) / 2)); // liczba indeksów / 2
		foreach ($idxs as $idx) {
			fwrite($fh, pack("N", $idx));
		}
		$posEOF = ftell($fh);
		fseek($fh, $posIDXS);
		fwrite($fh, pack("N", $posEOF - $posIDXS -4));
		// wpisanie na początek wielkości caego zbioru danych
		fseek($fh, $posMLYR);
		fwrite($fh, pack("N", $posEOF - $posMLYR -4));
		fclose($fh);

		print (" -->\n");

		// skopiowanie strony powitalnej
		copy("../wap/wizard_index.php", "../wap/get/$par_dir/index.php");

		// TODO: generowanie aplikacji przenieś do miejsca wywoania ze strony
		// wygenerowanie aplikacji
		/*
		require ('../scripts/create_midlet.php');
		$midletFileName = $path . "/mobiKAR";
		// nazwa ma nie by modyfikowania czyli == NULL
		$midletName = NULL;
		$id_producttype = 2; //singielek
		// TODO: przecież nie teraz powinno to by tworzone! Dopiero jak kolo wejdzie na stron WAP i wybiertze wersje
		$appType = "midp20";
		$musicType = "midi";
		$arrSongFiles = array($path . "/song");
		createMidlet($midletFileName, $midletName, $id_producttype, $appType, $musicType, $arrSongFiles);
		*/

	}
}


$message = $messages[$reason];

// renderowanie stron
print ("<?xml version=\"1.0\"?>\n");
print ("<!DOCTYPE html PUBLIC \" - //WAPFORUM//DTD XHTML Mobile 1.0//EN\" \"http://www.wapforum.org/DTD/xhtml-mobile10.dtd\">\n");
print ("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");

if ($par_request == NULL) {
?><?


	echo<<< END_OF_TEXT
	<head>
		<title>TYTUŁ</title>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	</head>
	<body>

  		<p> $message </p>
		<div>
			Wgraj plik KARAOKE (format MIDI z tekstem w jednym pliku)
		</div>
		<form enctype="multipart/form-data" action="wizard.php" method="post">
			<input type="hidden" name="request" value="upload_kar"/>
			<input type="hidden" name="dir" value="$par_dir"/>
			<input type="file" name="file_kar"/>
			<br/>
			<input type="submit" value="Przetwórz >>"/>
		</form>
  		<p> Dla zaawansowanych </p>
		<div>
			Wgraj muzyke i tekst w osobnych plikach
		</div>
		<form enctype="multipart/form-data" action="wizard.php" method="post">
			<input type="hidden" name="request" value="upload_mlyr"/>
			<input type="hidden" name="dir" value="$par_dir"/>
			<label> plik MIDI
				<input type="file" name="file_midi"/>
			</label>
			<br/>
			<label> plik mLYR
				<input type="file" name="file_mlyr"/>
			</label>
			<br/>
			<input type="submit" value="Przetwórz >>"/>
		</form>

	</body>
</html>
END_OF_TEXT;
?><?


}
elseif (strcmp($par_request, 'upload_kar') == 0) {
?><?


	echo<<< END_OF_TEXT
	<head>
		<title>TYTUŁ</title>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	</head>
	<body>

		<form name="form1" id="form1" method="post" action="">
			<input type="hidden" name="request" value="changed_text"/>
			<input type="hidden" name="dir" value="$par_dir"/>
      		<textarea name="lyric" cols="64" rows="16" wrap="OFF">$lyric_text</textarea>
	  		<br/>
	  		<input name="" type="submit" value="Przetwórz >>" />
    	</form>

	</body>
</html>
END_OF_TEXT;
?><?


}
elseif (strcmp($par_request, 'changed_text') == 0) {
	$directory = "http://" . MOBIKAR_SERVER_DOMAIN . "/get/$par_dir/";
?><?


	echo<<< END_OF_TEXT
	<head>
		<title>TYTUŁ</title>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	</head>
	<body>

		<p>
			Pobierz aplikacj wchodząc telefonem na adres
			<br/>
			$directory
		</p>

	</body>
</html>
END_OF_TEXT;
?><?


}

print ("par_request:" . $par_request);
//print_r($GLOBALS);
?>