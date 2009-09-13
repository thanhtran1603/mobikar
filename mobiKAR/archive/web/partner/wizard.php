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

$browser = getValFromItem($_SERVER['HTTP_USER_AGENT']);


trigger_error("request:".$par_request." REMOTE_ADDR:".getValFromItem($_SERVER['REMOTE_ADDR'])." HTTP_USER_AGENT:".getValFromItem($_SERVER['HTTP_USER_AGENT']), E_USER_NOTICE);

$message = NULL;

define('M1K0_REASON_NO_DIR', 1);
define('M1K0_REASON_LOAD', 2);
define('M1K0_REASON_NO_TEXT', 3);

$messages = array(
  M1K0_REASON_NO_DIR => "Nie można stworzyć katalou",
  M1K0_REASON_LOAD => "Plik nie został wczytany",
  M1K0_REASON_NO_TEXT => "Brak tekstu do przetworzenia",
);

$reason = 0;

$lyric_text = "";
$directory = "http://" . MOBIKAR_SERVER_DOMAIN;
$path_mlyr = null;
$title = "title";
$music = "music";

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
      $milis = 0;
      // poustawianie czasów trwania
      //print("<--\n");
      foreach ($track as $msgStr) {
        //print_r($msgStr);
        //print("\n");
        $msg = explode(' ', $msgStr);
        $milis = (int) ($msg[0] * $midi->getTempo() / $midi->getTimebase() / 1000);
        if ($msg[1] == 'Meta' && in_array($msg[2], $texttypes)) {
          $text = $NewEncoding->Convert(substr($msgStr, strpos($msgStr, '"')), "windows-1250", "utf-8");
          $text = str_replace("_", " ", $text);
          $text = str_replace("\'", "'", $text);
          $text = str_replace("\\", "/", $text);
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
      //print("\n-->\n");
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
              $objsNew[2] = str_replace("//", "/", $objsNew[2]);
              $objsNew[2] = str_replace("//", "/", $objsNew[2]);
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
            if (strrpos($objs[2], "/") > 0){
              //print("strpos:". strrpos($objs[2], "/")." -> \"".$objs[2]."\"\n");
              $objs[2] = str_replace("/", " ", $objs[2]);
              $objsNew = & $lyric[$key +1];
              $objsNew[2] = "/".$objsNew[2];
            }

          } else {
            $isFinished = TRUE;
            break;
          }
        }

      }

      unset($lyric[count($lyric)-1]);

      $lyric_text = "MILLIS\t" . $milis . "\nTITLE\tWypenij\nARTIST\tWypenij\nMUSIC\tWypenij\nLYRICS\tWypenij\nCREATOR\tWypenij\nVERSION\t0.1\nNOTE\tWypenij\n";
      $for_notice = "";
      foreach ($lyric as $objs) {
        if ($objs[0] == 0) {
          $lyric_text .= "//";
          // zapiszemy do loga
          $for_notice = $objs[2];
        }
        $lyric_text .= "$objs[0]\t$objs[1]\t0\t$objs[2]\n";
      }
      $lyric_text = mb_convert_encoding($lyric_text, "HTML-ENTITIES", "utf-8");
	  trigger_error("TEXT:".$for_notice, E_USER_NOTICE);
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
      copy($file_midi, $path . "/song.mid");
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

	  trigger_error("LOAD MLYR title:".$title." artist:". $artist . " music ".$music, E_USER_NOTICE);

      // przelecimy wgrany plik
      $lyric_text = "MILLIS\t" . $milis . "\nTITLE\t$title\nARTIST\t$artist\nMUSIC\tMUSIC\nLYRICS\t$lyrics\nCREATOR\t$creator\nVERSION\t$version\nNOTE\t$note\n";
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
//			print ("<!--");
//			print_r($parts);
//			print ("-->");
      $posRemark = strpos($line, "//");
      if ($posRemark !== FALSE && $posRemark == 0){
        continue;
      }

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
    $directory = "http://" . MOBIKAR_SERVER_DOMAIN . "/get/$par_dir/";
    $path_mlyr = $directory . "song.midi.mlyr";
    trigger_error("SAVE MLYR to http://" . MOBIKAR_SERVER_DOMAIN . "/get/$par_dir/ title:".$title." artist:". $artist . " music ".$music, E_USER_NOTICE);
  }
}

if ($reason > 0)
	$message = $messages[$reason];
$dict['pl']['tab_load'] = "Wczytaj";
$dict['pl']['tab_edit'] = "Edytuj";
$dict['pl']['tab_getit'] = "Pobierz";
$dict['pl']['tab_help'] = "Pomoc";
$dict['pl']['txt_js'] = "JavaScript nie załadowany!";
$dict['pl']['txt_load'] ="Je&#347;li posiadasz plik KARAOKE w formacie MIDI z tekstem piosenki (rozszerzenie .KAR, .MID, .MIDI) to ju&#380; za chwil&#281; mo&#380;esz mie&#263; to KARAOKE w swoim telefonie! <br/> KARAOKE przygotowuje si&#281; w dw&#243;ch krokach: wczytanie piosenki i sprawdzenie tekstu. <br/> W ka&#380;dej chwili mo&#380;esz skorzysta&#263; z karty <a href=\"javascript:setTab(\'help\')\">Pomoc</a>. <br/> <br/> Wgraj plik KARAOKE (format MIDI z tekstem w jednym pliku)";
$dict['pl']['but_process'] = "Przetwórz >>";
$dict['pl']['lnk_forAdvanced'] = "dla zaawansowanych >>";
$dict['pl']['txt_advLoad'] = "Wgraj muzyke i tekst w osobnych plikach";
$dict['pl']['txt_edit'] = "Nag&#322;&#243;wek przedstawia autor&#243;w piosenki. Poni&#380;ej znajduje si&#281; tekst podzielony na sylaby, kt&#243;ra ka&#380;da jest w osobnej linii i sk&#322;ada si&#281; z: czasu startu, czasu trwania, numeru &#347;piewaka i sylaby. Wi&#281;cej przeczytasz w karcie <a href=\"javascript:setTab(\'help\')\">Pomoc</a>.<br/><br/>Przeedytuj słowa piosenki";
$dict['pl']['txt_getit'] = "KARAOKE z piosenką <b> $title ($music)</b> jest już gotowa do pobrania. <br/> Aby pobrać gotową aplikację mobiKAR z przygotowaną piosenką wejdź przeglądarką internetową swojego telefonu na adres <br/> <br/> <b>$directory</b> <br/> <br/> Plik formatu mLYR dostpny jest spod adresu: <br/> <a href=\"$path_mlyr\">$path_mlyr</a> <br/> Zachęcamy również do zakupu gotowych piosenek z serwisu mobikar.net.";


$dict['en']['tab_load'] = "Load";
$dict['en']['tab_edit'] = "Edit";
$dict['en']['tab_getit'] = "Get it";
$dict['en']['tab_help'] = "Help";
$dict['en']['txt_js'] = "JavaScript is not loaded!";
$dict['en']['txt_load'] ="If you already have any KARAOKE files in MIDI format with the embedded song text (format .KAR, .MID, .MIDI), You can have them loaded to your mobile phone in no time.<br/>KARAOKE files can be prepared in two simple steps: loading the song and correcting the text. <br/>Application <a href=\"javascript:setTab(\'help\')\">Help</a> facility is available at any time.<br/><br/>Load KARAOKE file (single MIDI file with embedded text).";
$dict['en']['but_process'] = "Process >>";
$dict['en']['lnk_forAdvanced'] = "for advanced >>";
$dict['en']['txt_advLoad'] = "Load songs that have separate music (MIDI) and text (mLYR) files";
$dict['en']['txt_edit'] = "The header shows the name of the Author. Below,  the text of a song (with syllabus text, start time, duration and number of a singer in single lines each ) is displayed. See the application <a href=\"javascript:setTab(\'help\')\">help</a> for more information. <br/><br/>Now the words of the song can be edited to your  satisfaction.";
$dict['en']['txt_getit'] = "At this stage the KARAOKE with song <b> $title ($music)</b> is ready for download via pointing your mobile phone browser to the site <br/> <br/> <b>$directory</b> <br/> <br/>The mLYR file is available here:<br/> <a href=\"$path_mlyr\">$path_mlyr</a> <br/>A nice selection of ready-made songs is also available for purchase at our service mobikar.net.";

echo<<< END_OF_TEXT
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>mobiKAR.net - mobile KARAOKE</title>
<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
<link rel="stylesheet" type="text/css" title="CSS" href="mobikar.css"/>
<script type='text/javascript' src='wizard.js'></script>
</head>
END_OF_TEXT;
?>
<? if ($par_request == NULL) { ?>
<body onload="init();">
<div id='mk_tab_nojs'> <?print($dict[$par_lang]['txt_js']);?> </div>
<div class='mk_tab_menu'>
  <ul id='mk_tab_menu'>
    <li><a id='mk_tab_menu_load' href="javascript:setTab('load')"><?print($dict[$par_lang]['tab_load']);?></a></li>
    <li><span id='mk_tab_menu_edit' ><?print($dict[$par_lang]['tab_edit']);?></span></li>
    <li><span id='mk_tab_menu_getit'><?print($dict[$par_lang]['tab_getit']);?></span></li>
    <li><a id='mk_tab_menu_help' href="javascript:setTab('help')"><?print($dict[$par_lang]['tab_help']);?></a></li>
<? } elseif (strcmp($par_request, 'upload_kar') == 0) { ?>
<body onload='init();setTab("edit")'>
<div id='mk_tab_nojs'> <?print($dict[$par_lang]['txt_js']);?> </div>
<div class='mk_tab_menu'>
  <ul id='mk_tab_menu'>
    <li><a id='mk_tab_menu_load' href="javascript:setTab('load')"><?print($dict[$par_lang]['tab_load']);?></a></li>
    <li><a id='mk_tab_menu_edit' href="javascript:setTab('edit')"><?print($dict[$par_lang]['tab_edit']);?></a></li>
    <li><span id='mk_tab_menu_getit' href="javascript:setTab('getit')"><?print($dict[$par_lang]['tab_getit']);?></span></li>
    <li><a id='mk_tab_menu_help' href="javascript:setTab('help')"><?print($dict[$par_lang]['tab_help']);?></a></li>
<? } elseif (strcmp($par_request, 'changed_text') == 0) { ?>
<body onload='init();setTab("getit")'>
<div id='mk_tab_nojs'> <?print($dict[$par_lang]['txt_js']);?> </div>
<div class='mk_tab_menu'>
  <ul id='mk_tab_menu'>
    <li><a id='mk_tab_menu_load' href="javascript:setTab('load')"><?print($dict[$par_lang]['tab_load']);?></a></li>
    <li><a id='mk_tab_menu_edit' href="javascript:setTab('edit')"><?print($dict[$par_lang]['tab_edit']);?></a></li>
    <li><a id='mk_tab_menu_getit' href="javascript:setTab('getit')"><?print($dict[$par_lang]['tab_getit']);?></a></li>
    <li><a id='mk_tab_menu_help' href="javascript:setTab('help')"><?print($dict[$par_lang]['tab_help']);?></a></li>
<? } ?>

  </ul>
</div>
<div class="clr"></div>
<div class="mk_tab_body">
  <div class="mk_tab_content" id="mk_tab_load">

<? if ($message !== NULL){
	print("<b>$message</b><br/>");
}
?>
		<?print($dict[$par_lang]['txt_load']);?>
    <form enctype="multipart/form-data" action="wizard.php" method="post">
      <input type="hidden" name="lang" value="<?print $par_lang?>"/>
      <input type="hidden" name="request" value="upload_kar"/>
      <input type="hidden" name="dir" value="<?print $par_dir?>"/>
      <input type="file" name="file_kar"/>
      <br/>
      <input type="submit" value="<?print($dict[$par_lang]['but_process']);?>"/>
    </form>
    <a style="font-weight:bold; text-decoration:underline" href="javascript:load_advanced()"> <?print($dict[$par_lang]['lnk_forAdvanced']);?></a>
    <div id="mk_tab_load_adv" style="background-color:#CCCCCC; width:auto; display:none">
      <p> <?print($dict[$par_lang]['txt_advLoad']);?> </p>
      <form enctype="multipart/form-data" action="wizard.php" method="post">
      	<input type="hidden" name="lang" value="<?print $par_lang?>"/>
        <input type="hidden" name="request" value="upload_mlyr"/>
        <input type="hidden" name="dir" value="$par_dir"/>
        <label> MIDI
          <input type="file" name="file_midi"/>
        </label>
        <br/>
        <label> mLYR
          <input type="file" name="file_mlyr"/>
        </label>
        <br/>
        <input type="submit" value="<?print($dict[$par_lang]['but_process']);?>"/>
      </form>
    </div>
  </div>
  <div class="mk_tab_content" id="mk_tab_edit">
    <?print($dict[$par_lang]['txt_edit']);?>
  <form name="form1" id="form1" method="post" action="">
  	<input type="hidden" name="lang" value="<?print $par_lang?>"/>
    <input type="hidden" name="request" value="changed_text"/>
    <input type="hidden" name="dir" value="<?print $par_dir?>"/>
      <textarea name="lyric" cols="64" rows="13" wrap="OFF"><?print $lyric_text?></textarea>
      <br/>
      <input name="" type="submit" value="<?print($dict[$par_lang]['but_process']);?>" />
  </form>
  </div>
  <div class="mk_tab_content" id="mk_tab_getit">
      <?print($dict[$par_lang]['txt_getit']);?>

  </div>

  <div class="mk_tab_content" id="mk_tab_help">
<?php if ("pl" == $par_lang){ ?>
  	<p><font size="4">Format pliku KARAOKE</font></p>
  	<p>
  		Zabawa KARAOKE polega na &#347;piewaniu piosenki podczas odtwarzania
		muzyki. Najpopularniejszym formatem plik&#243;w jest format MIDI z
		zapisanym tekstem. Rozszerzenia dla tych plik&#243;w to MID, MIDI i
		KAR. Pliki z rozszerzeniami MID czy MIDI mog&#261; czasem nie
		zawiera&#263;
		tekst&#243;w - s&#322;&#243;w piosenki. Plik z
		rozszerzeniem KAR
		powinien zawsze zawiera&#263; w sobie s&#322;owa piosenki
		<br/>
		Więcej o generowaniu wałasnego KARAOKE  na stronie z <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=42&amp;lang=pl" target="_blank">artykułem o generowaniu</a>
		<br/>
		Wi&#281;cej informacji o formacie mLYR na stronie z <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=40&amp;lang=pl" target="_blank">pomoc&#261; o mLYR</a>
		<br/>
  	</p>
  	<p><font size="4">Poprawianie tekstu</font></p>
	<p>
		Wybrana piosenka KARAOKE zostaje wys&#322;ana do serwisu
		mobikar.net.
		Nast&#281;pnie jest wyci&#261;gany z piosenki tekst i
		prezentowany w oknie
		edycyjnym. Na pocz&#261;tku umieszczone s&#261; parametry
		piosenki takie jak:
		czas trwania w milisekundach (MILLIS), tytu&#322; (TITLE),
		wykonawca
		(ARTIST), kompozytor (MUSIC), s&#322;owa (LYRICS), tw&#243;rca
		aran&#380;acji &#8211; pliku KARAOKE (CREATOR), wersja pliku
		(VERSION) i
		notatka do piosenki, pliku jak r&#243;wnie&#380; wszelkie
		komunikaty
		dotycz&#261;ce praw autorskich, itp. (NOTE).
	</p>
	<p>
		Poni&#380;ej znajduje si&#281; tekst piosenki rozbity
		za sylaby czy
		pojedyncze frazy tekstu od&#347;piewywane w ustalonym czasie.
		Ka&#380;da nowa
		linia sk&#322;ada si&#281; z: czasu w kt&#243;rym sylaba
		zaczyna by&#263;
		&#347;piewana, czasu definiuj&#261;cego
		d&#322;ugo&#347;&#263; &#347;piewania sylaby, numer
		&#347;piewaka i na ko&#324;cu sylaba do od&#347;piewania.
	</p>
	<p>
		Czas podawany jest w milisekundach. Numer &#347;piewaka
		okre&#347;la, kto
		ma &#347;piewa&#263; dan&#261; sylab&#281; i przyjmuje
		warto&#347;ci 0 i 1. Wykorzysta&#263;
		mo&#380;na go np. gdy piosenka jest &#347;piewana przez dwie
		osoby, lub jedn&#261;
		osob&#281;, kt&#243;rej towarzyszy ch&#243;rek.
	</p>
	<p>
		Linia rozpoczynaj&#261;ca si&#281; od dw&#243;ch
		znak&#243;w uko&#347;nika
		(//) nie wchodzi w sk&#322;ad tekstu piosenki, przez co
		mo&#380;na w takich
		liniach umieszcza&#263; w&#322;asne komentarze.
	</p>
	<p>
		Poszczeg&#243;lne kolumny danych rozdzielone s&#261;
		specjalnym
		pojedynczym znakiem tabulacji. W sylabach wa&#380;ne s&#261;
		znaki odst&#281;pu
		(spacje). Je&#347;li si&#281; o nich zapomni to tekst
		b&#281;dzie sklejony
		podczas prezentacji jego w aplikacji mobile KARAOKE &#8211; mobiKAR.
		<br/>
		Więcej o generowaniu wałasnego KARAOKE  na stronie z <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=42&amp;lang=pl" target="_blank">artykułem o generowaniu</a>
		<br/>
		Wi&#281;cej informacji o formacie mLYR na stronie z <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=40&amp;lang=pl" target="_blank">pomoc&#261; o mLYR</a>
		<br/>
  	</p>
<? } else if ("en" == $par_lang) {?>

  	<p><font size="4">KARAOKE file format</font></p>
  	<p>
  		The fun with karaoke is to sing a song while playing its music.
  		The most commonly used format  is MIDI with embedded song text with typical extensions MID, MIDI and KAR.
		Files with extension MID or MIDI may not always have the text of a song embedded.
		Files with extension KAR should always contain embedded text.
		<br/>
		More information on creating your <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=42&amp;lang=en" target="_blank">own KARAOKE files</a>
		and the <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=40&amp;lang=en" target="_blank">mLYR format</a> is available on our WEB site.
		<br/>
  	</p>
  	<p><font size="4">Editing text</font></p>
	<p>
		A selected KARAOKE song is being sent to the mobikar.net service.  The text is then extracted from the song and displayed in the editor.
The top lines in the editor display certain parameters of the song - duration in milliseconds (MILLIS), the title of the song (TITLE), the name of the artist (ARTIST),
name of the composer (MUSIC), the name of the author of the song text (LYRICS), the name of the person creating this KARAOKE file (CREATOR),
version of the file (VERSION), comments and notes related to the song and file and the copyright information etc (NOTE).

	</p>
	<p>
		The subsequent lines display text of the song broken down into single syllabus or text phrases sang in predetermined time.
Each new line contains the start time, duration,  number of a singer and the syllabus itself.
Time is displayed is milliseconds. The number of a singer denotes respective singer and can have values of 0 or 1.
The number of a singer is used two people are singing or it is a single person accompanied by a chorus.
Line of text starting with two forward slashes (//) is ignored by the application and can be used as a comment.
The TAB character separates columns of data. The white spaces within syllabus are important.
Incorrect use of spaces will result in incorrect compilation and subsequently reproduction of the song by the mobiKAR.
	</p>
	<p>
		More information on creating your <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=42&amp;lang=en" target="_blank">own KARAOKE files</a>
		and the <a href="http://www.mobikar.net/index.php?option=com_content&amp;task=view&amp;id=40&amp;lang=en" target="_blank">mLYR format</a> is available on our WEB site.
		<br/>
	</p>

<? } ?>
  </div>
</div>
</body>
</html>
