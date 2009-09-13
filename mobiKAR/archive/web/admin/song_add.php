<?php
include ("../scripts/myErrorHandler.php");
include_once ("Base.php");
include_once("../scripts/utils.php");
$title = getParameter('title', 'title');
$atrist = getParameter('artist','artist');
$music = getParameter('music','music');
$lyrics = getParameter('lyrics','lyrics');
$creator = getParameter('creator','creator');
$version = getParameter('version','1.0');
$score = getParameter('score', 5);
$id_song = getParameter('id_song');

$DEBUG_STRING = "";

$db = new Base();
$categories = null;
$categories = $db->getCategories();
$choices = NULL;
foreach ($categories as $category) {
	$id = $category['id'];
	if (isset ($_GET["cat_$id"])) {
		$choices[$id] = $id;
	}
}
$isHasMLYR = false;
$stan = null;
if (count($_GET) == 0 && count($_POST) == 0)
	$stan = "BEFORE_ADD_NEW";
if (count($_FILES) != 0)
	$stan = "UPLOAD_FILE";
if (count($_GET) != 0) {
	if (isset ($_GET['id_song']))
		$stan = "VIEW";
	if (isset ($_GET['title']))
		$stan = "CHANGE";
}
$heading = null;
$button = null;
trigger_error("stan:$stan", E_USER_NOTICE);
if (strcmp($stan, "BEFORE_ADD_NEW") == 0) {
	trigger_error("i!!!!@@@@@@@@@@@@@@", E_USER_NOTICE);
	$title = "TITLE";
	$artist = "ARTIST";
	$writer = "WRITER";
	$creator = "CREATOR";
	$score = 5;
	$heading = "Dodaj piosenkę";
	$button = "Dodaj";
}
elseif ($stan == "VIEW") {
	$heading = "Edycja piosenki o id $id_song";
	$button = "Zmień";
}
elseif ($stan == "UPLOAD_FILE") {
	if ($id_song == NULL) {
		$id_song = $db->addSong("TITLE", "ATRIST", "MUSIC", "LYRICS", "CREATOR", "VERSION", 5);
		trigger_error(" nowy id_song:$id_song", E_USER_NOTICE);
	}
	trigger_error("id_song: $id_song", E_USER_NOTICE);
	if (isset ($_POST['submit_midi'])) {
		if (isset ($_FILES['file_midi']['tmp_name'])) {
			$file_midi = $_FILES['file_midi']['tmp_name'];
			trigger_error("file == $file_midi", E_USER_NOTICE);
			$midiName = MOBIKAR_PRODUCTS_DIR.'songs/'.$id_song.".midi";
			trigger_error("midiName:".$midiName, E_USER_NOTICE);
			copy($file_midi, $midiName);
			$heading = "Wczytano plik MIDI dla piosenki o id $id_song";
			$button = "Zmień";
		}
	}
	if (isset ($_POST['submit_mlyr'])) {
		if (isset ($_FILES['file_mlyr']['tmp_name'])) {
			$file_mlyr = $_FILES['file_mlyr']['tmp_name'];
			trigger_error("file == $file_mlyr", E_USER_NOTICE);
			$mlyrName = MOBIKAR_PRODUCTS_DIR.'songs/'.$id_song.".mlyr";
			trigger_error("mlyrName:".$mlyrName, E_USER_NOTICE);
			copy($file_mlyr, $mlyrName);
			$heading = "Wczytano plik mLyr dla piosenki o id $id_song";
			$button = "Zmień";
			// dadano
			// odczytanie danych z piosenki
			$DEBUG_STRING .= "plik do odczytu to: " . $mlyrName . "<br/>";
			$fh = fopen($mlyrName, 'rb');
			$bin = fread($fh, 4);
			$DEBUG_STRING .= "bin: " . $bin . "<br/>";
			$tag = $bin;
			$bin = fread($fh, 4);
			$liczba = unpack('N', $bin);

			while (!feof($fh)) {
				$tag_name = fread($fh, 4);
				$bin = fread($fh, 4);
				$array = unpack('N', $bin);
				$tag_size = $array[1];
				$pad = fread($fh, 2);
				$tag_value = fread($fh, $tag_size -2);
				if (strpos($tag_name, 'TITL') !== FALSE) {
					$title = $tag_value;
				}
				elseif (strpos($tag_name, 'MUSI') !== FALSE) {
					$music = $tag_value;
				}
				elseif (strpos($tag_name, 'LYRI') !== FALSE) {
					$lyrics = $tag_value;
				}
				elseif (strpos($tag_name, 'ARTI') !== FALSE) {
					$artist = $tag_value;
				}
				elseif (strpos($tag_name, 'CREA') !== FALSE) {
					$creator = $tag_value;
				}
				elseif (strpos($tag_name, 'VER$') !== FALSE) {
					$version = $tag_value;
				}
				elseif (strpos($tag_name, 'TEXT') !== FALSE) {
					break;
				}
				elseif (strpos($tag_name, 'IDXS') !== FALSE) {
					break;
				}
			}
			$DEBUG_STRING .= "title:" . $title . "<br/>";
			$DEBUG_STRING .= "id_song:" . $id_song . "<br/>";
			$DEBUG_STRING .= "setSong($id_song, $title, $artist, $music, $lyrics, $creator, $version, $score)" . "<br/>";
			$db->setSong($id_song, $title, $artist, $music, $lyrics, $creator, $version, $score);
		}
	}
}
elseif ($stan == "CHANGE") {
	$heading = "Edycja piosenki o id $id_song";
	$button = "Zmień";
	if ($id_song == NULL) {
		if ($title != NULL && $artist != NULL && $music != NULL && $lyrics != NULL && $creator != NULL && $score != NULL) {
			$id_song = $db->addSong($title, $artist, $music, $lyrics, $creator, $version, $score);
			$heading = "Dodano piosenkę o id $id_song";
			$button = "Zmień";
		}
	} else {
		if ($title != NULL && $artist != NULL && $music != NULL && $lyrics != NULL && $creator != NULL && $score != NULL) {
			$db->setSong($id_song, $title, $artist, $music, $lyrics, $creator, $version, $score);
			$heading = "Zmieniono piosenkę o id $id_song";
			$button = "Zmień";
		}
	}
	$db->cleanConSongCategory($id_song);
	foreach ($choices as $choice) {
		$id_category = $choice;
		$db->addConSongCategory($id_song, $id_category);
	}
}
trigger_error("id_song:$id_song", E_USER_NOTICE);
$fileInfo = "";
// edycja
if ($id_song != NULL) {
	$DEBUG_STRING .= "odczytanie z bazy danych:" . $id_song . "<br/>";
	// odczytanie z bazy danych
	$song = $db->getSong($id_song);
	if (isset ($song['title']))
		$title = $song['title'];
	if (isset ($song['artist']))
		$artist = $song['artist'];
	if (isset ($song['writer']))
		$writer = $song['writer'];
	if (isset ($song['creator']))
		$creator = $song['creator'];
	if (isset ($song['score']))
		$score = $song['score'];
	$songCategories = $db->getSongCategories($id_song);
	$choices = NULL;
	foreach ($songCategories as $category) {
		if (isset ($category['id_category'])) {
			$id_category = $category['id_category'];
			$choices["$id_category"] = $id_category;
		}
	}
	if (count($choices) <= 0) {
		$fileInfo .= " Brak Kategorii<br/> ";
	}
	$midiName = MOBIKAR_PRODUCTS_DIR.'songs/'.$id_song.".midi";
	trigger_error("midiName: $midiName", E_USER_NOTICE);
	if (file_exists($midiName) == FALSE) {
		$fileInfo .= " Brak pliku midi ";
	} else {
		$fileInfo .= " Plik [$midiName] zmodyfikowany ".date("Y-m-d H:i:s", filemtime($midiName));
		$fileInfo .= ", rozmiar ".filesize($midiName)." bajtów";
	}
	$mlyrName = MOBIKAR_PRODUCTS_DIR.'songs/'.$id_song.".mlyr";
	if (file_exists($mlyrName) == FALSE) {
		$fileInfo .= "<br/> Brak pliku mlyr ";
	} else {
		$fileInfo .= "<br/> Plik [$mlyrName] zmodyfikowany ".date("Y-m-d H:i:s", filemtime($mlyrName));
		$fileInfo .= ", rozmiar ".filesize($mlyrName)." bajtów";
		$isHasMLYR = true;
	}
}
$db->destroy();
/*
 * Created on 2005-05-27
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>mobiKAR - admin - dodaj piosenkę</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<h1><?=$heading?></h1>
<!--
<?php print_r($GLOBALS); ?>
-->
	<?=$fileInfo?>
<?php if ($isHasMLYR == true){ ?>	
	<br/>
	<a href="mlyr_show.php?id_song=<?=$id_song?>" target="_blank">Pokaż zawartość pliku z piosenką</A>
<?php } ?>	
	<br/>
	<form action="song_add.php" method="post" enctype="multipart/form-data">
<?php


if ($id_song != NULL)
	echo '<input type="hidden" name="id_song" value="'.$id_song.'">';
?>
		Wyślij plik mLyr: <input type="file" name="file_mlyr"><br>
		<input type="submit" name="submit_mlyr" value="Wyślij">
	</form>
	<br/>

	<form action="song_add.php" method="post" enctype="multipart/form-data">
<?php


if ($id_song != NULL)
	echo '<input type="hidden" name="id_song" value="'.$id_song.'">';
?>
		Wyślij plik MIDI: <input type="file" name="file_midi"><br>
		<input type="submit" name="submit_midi" value="Wyślij">
	</form>
	<br/>


		<form  action="song_add.php" method="get">
<?php


if ($id_song != NULL)
	echo '<input type="hidden" name="id_song" value="'.$id_song.'">';
?>

	
			Tytuł piosenki: <input type="text" name="title" size="30" maxlength="100" <?=($title!=NULL?'value="'.$title.'"':'')?> >
			<br/>
			Wykonawca: <input type="text" name="artist" size="30" maxlength="100" <?=($artist!=NULL?'value="'.$artist.'"':'')?> >
			<br/>
			Autor muzyki: <input type="text" name="music" size="30" maxlength="100" <?=($music!=NULL?'value="'.$music.'"':'')?> >
			<br/>
			Autor słów: <input type="text" name="lyrics" size="30" maxlength="100" <?=($lyrics!=NULL?'value="'.$lyrics.'"':'')?> >
			<br/>
			Autor opracowania: <input type="text" name="creator" size="30" maxlength="100" <?=($creator!=NULL?'value="'.$creator.'"':'')?> >
			<br/>
			Wersja opracowania: <input type="text" name="version" size="30" maxlength="100" <?=($version!=NULL?'value="'.$version.'"':'')?> >
			<br/>
			Ocena: <input type="text" name="score"  maxlength="1" <?=($score!=NULL?'value="'.$score.'"':'')?> >
			<br/>
			

  
<?php


foreach ($categories as $category) {
	$id = $category['id'];
	$name = $category['name'];
	$checked = "";
	if (isset ($choices[$id])) {
		$checked = 'checked="true"';
	}
	echo "&#160; $name<input type='checkbox' name='cat_".$id."' value='".$id."' $checked/>; ";
}
?>			
			<br/>
			<input type="submit"  value="<?=$button?>">
		</form>
	<?=$DEBUG_STRING?>
	</body>
</html>
















