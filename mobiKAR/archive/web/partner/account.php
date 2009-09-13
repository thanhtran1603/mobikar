<?php
include_once ('../scripts/config.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');
$user = (isset($_SERVER["PHP_AUTH_USER"])? $_SERVER["PHP_AUTH_USER"] : null);
$passwd = (isset($_SERVER["PHP_AUTH_PW"])? $_SERVER["PHP_AUTH_PW"] : null);
$isAllowed = false;
$rowPartner = null;
$sum = 0;
$sum_coins = 0;
$sum_songs = 0;
$sum_payed = 0;
$soldCoins = null;	
$soldSongs = null;	
$services = null;
$songs = null;
$history_month_coins = null;
$history_month_songs = null;
$history_cash = null;

if ($user != null && $passwd != null){
	$db = new Database();
	$rowPartner = $db->getPartner($user, null);
	if ($rowPartner != null) {
		if (isset($rowPartner['password']) && (strcmp($passwd, $rowPartner['password']) == 0)) {
			$partnerId = null;
			if (isset($rowPartner['id']))
				$partnerId = $rowPartner['id'];
			$soldCoins = $db->getPartnerSoldCoins($partnerId);
			$soldSongs = $db->getPartnerSoldSongs($partnerId);
			$services = $db->getPartnerServices($partnerId);
			$songs = $db->getPartnerSongs($partnerId);
			$history_month_coins = $db->getPartnerHistoryMonthlyCoins($partnerId);
			$history_month_songs = $db->getPartnerHistoryMonthlySongs($partnerId);
			$history_cash = $db->getPartnerHistoryCashing($partnerId);
			$isAllowed = true;
		}
	}
	$db->destroy();
	foreach($soldCoins as $sold){
		$id = getValFromItem($sold['id']);
		$cost = getValFromItem($sold['cost']);
		$sold = getValFromItem($sold['sold']);
		$percentage = 20;
		if ($id == 2 || $id == 3)
			$percentage = 12;
		$profit = ($cost * $sold) * ($percentage / 100) / 100;	
		$sum_coins += $profit;
	}
	foreach($soldSongs as $row){
		$cost = getValFromItem($row['cost']);
		$coins = getValFromItem($row['coins']);
		$sold = getValFromItem($row['sold']);
		$sum = ($cost / $coins)  * $sold ;
		$percentage = 3;
		$sum_songs = $sum * ($percentage / 100) / 100;	
	}
	foreach($history_cash as $cash){
		$sum_payed += getValFromItem($cash['amount']);
	}
	
}	
if ( ! $isAllowed){
 // Bad or no username/password.
 // Send HTTP 401 error to make the
 // browser prompt the user.
 header("WWW-Authenticate: " .
        "Basic realm=\"Strefa dla partnerow. Zaloguj sie\"");
 header("HTTP/1.0 401 Unauthorized");
 // Display message if user cancels dialog
 ?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>partner mobiKAR.net - Nieprawidłowa autoryzacja</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
 <h1>Nieprawidłowa autoryzacja</h1>
 <p>Bez podania poprawnego identyfikatora jak i hasła 
 	nie jest możliwe oglądanie stron serwisu dla partnerów.
 	<br/>
 	Przeładuj stronę aby ponownie zalogować się.
 </p>
 </body>
 </html>
<?php } else{ ?>

<?php

$account = round(($sum_coins + $sum_songs) - $sum_payed, 2);
$password = getValFromItem($rowPartner['password'], "");
$suffix= getValFromItem($rowPartner['suffix'], "");
$email= getValFromItem($rowPartner['email'], "");
$name= getValFromItem($rowPartner['name'], "");
$address= getValFromItem($rowPartner['address'], "");
$post= getValFromItem($rowPartner['post'], "");
$bank= getValFromItem($rowPartner['bank'], "");
$note= getValFromItem($rowPartner['note'], "");
?>

 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>partner mobiKAR.net</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	<h1>Strefa dla partnerów</h1>
	login: <b><?php print($user); ?></b>
	suffix: <b><?php print($suffix); ?></b>
	email: <b><?php print($email); ?></b>
	<br/>
	Adres: <b><?php print($name); ?></b>, 
	<b><?php print($address); ?></b>,
	<b><?php print($post); ?></b>
	<br/>
	Konto bankowe: <b><?php print($bank); ?></b>
	<br/>
	Notatka: <i><?php print($note); ?></i>
	<br/>
	<br/>
	
	Stan konta: <b><?php print($account); ?> zł</b>
	<br/>
	<?php if($account >= 50){ ?>
		<form title="Wplata" name="Wplata" action="send.php" method="get">
			Wyślij żądanie wypłaty
			<br/>
			<input name="user" value="<?=$user?>" type="hidden"/>
			<textarea name="msg" cols="40" rows="5">Komentarz</textarea>
			<br/>
			<input name="submit" type="submit" value="Wyslij"/>
		</form>
		
	<?php } else { ?>
		<i>Wypłata możliwa jest po osiągnięciu progu 50 zł. </i>
	<?php } ?>
	<h2> Historia wypłat</h2>
	<table border="1">
		<tr>
			<td> Wartość </td>
			<td> zamówiono </td>
			<td> zrealizowano </td>
			<td> komentarz </td>
		</tr>
	<?php
		//print_r($history_cash);
		foreach($history_cash as $cash){
			$amount = (isset($cash['amount']) ? $cash['amount'] : null);
			$ordered = (isset($cash['ordered']) ? $cash['ordered'] : null);
			$realized = (isset($cash['realized']) ? $cash['realized'] : null);
			$describe = (isset($cash['describe']) ? $cash['describe'] : null);
			print("<tr>");
			print("<td style='text-align:right'>$amount zł</td>");
			print("<td>$ordered</td>");
			print("<td>$realized</td>");
			print("<td><i>$describe</i></td>");
			print("</tr>\n");
		}
		if (count($history_cash)==0){
			print("<tr>");
			print("<td colspan='4'>Brak danych</td>");
			print("</tr>\n"); 
		}
	?>
	</table>
	<h2> Twoje serwisy </h2>
	<table border="1">
		<tr>
			<td> Id </td>
			<td> adres WWW </td>
			<td> adres CSS </td>
		</tr>
	<?php
		//print_r($services);
		foreach($services as $service){
			$id = (isset($service['id']) ? $service['id'] : null);
			$web = (isset($service['web']) ? $service['web'] : null);
			$css = (isset($service['css']) ? $service['css'] : null);
			print("<tr>");
			print("<td>$id</td>");
			print("<td><a href=\"$web\">$web</a></td>");
			print("<td><a href=\"$css\">$css</a></td>");
			print("</tr>\n"); 
		}
		if (count($services)==0){
			print("<tr>");
			print("<td colspan='3'>Brak danych</td>");
			print("</tr>\n"); 
		}
	?>
	</table>
	<h2> Dochody ze sprzedaży produktów</h2>
	<table border="1">
		<tr>
			<td> rodzaj </td>
			<td> koszt </td>
			<td> liczba </td>
			<td> suma</td>
			<td> procent </td>
			<td> zarobek </td>
		</tr>
	<?php
		//print_r($soldCoins);
		
		foreach($soldCoins as $row){
			$id = getValFromItem($row['id']);
			$name = getValFromItem($row['name']);
			$cost = getValFromItem($row['cost']);
			$sold = getValFromItem($row['sold']);
			$sum = $cost * $sold;
			$percentage = 20;
			if ($id == 2 || $id == 3)
				$percentage = 12;
			$profit = $sum * ($percentage / 100) / 100;	
//			if ($profit <= 0)
//				continue; 
			print("<tr>");
			printf("<td>$name</td>");
			printf("<td>".round($cost/100, 2)."</td>");
			printf("<td>$sold</td>");
			printf("<td>".round($sum/100, 2)."</td>");
			printf("<td>$percentage %%</td>");
			printf("<td>".round($profit,2)."</td>");
			print("</tr>\n");
		}
		if (count($soldCoins)==0){
			print("<tr>");
			print("<td colspan='6'>Brak danych</td>");
			print("</tr>\n"); 
		}
	?>
	</table>
	<h2> Historia sprzedaży żetonów</h2>
	<table border="1">
		<tr>
			<td> Miesiąc </td>
			<td> Sprzedaż </td>
		</tr>
	<?php
		//print_r($history_month);
		foreach($history_month_coins as $month){
			$year = getValFromItem($month['year']);
			$month1 = getValFromItem($month['month']);
			$coins = getValFromItem($month['coins']);
			print("<tr>");
			printf("<td>$year-%02d</td>", $month1);
			print("<td style='text-align:right'>$coins</td>");
			print("</tr>\n");
		}
		if (count($history_month_coins)==0){
			print("<tr>");
			print("<td colspan='2'>Brak danych</td>");
			print("</tr>\n"); 
		}
	?>
	</table>
	<h2> Twoje opracowania piosenek </h2>
	<table border="1">
		<tr>
			<td> tytuł </td>
			<td> wykonawca </td>
			<td> muzyka </td>
			<td> słowa </td>
			<td> wersja </td>
			<td> zamówień </td>
		</tr>
	<?php
		//print_r($songs);
		foreach($songs as $song){
			$title = getValFromItem($song['title']);
			$artist = getValFromItem($song['artist']);
			$music = getValFromItem($song['music']);
			$lyrics = getValFromItem($song['lyrics']);
			$version = getValFromItem($song['version']);
			$ordered = getValFromItem($song['ordered'], 0);
			print("<tr>");
			print("<td>$title</td>");
			print("<td>$artist</td>");
			print("<td>$music</td>");
			print("<td>$lyrics</td>");
			print("<td>$version</td>");
			print("<td>$ordered</td>");
			print("</tr>\n"); 
		}
		if (count($songs)==0){
			print("<tr>");
			print("<td colspan='6'>Brak danych</td>");
			print("</tr>\n"); 
		}
	?>
	</table>
	</table>
	<h2> Dochody ze sprzedaży piosenek</h2>
	<table border="1">
		<tr>
			<td> rodzaj </td>
			<td> koszt </td>
			<td> liczba </td>
			<td> suma</td>
			<td> procent </td>
			<td> zarobek </td>
		</tr>
	<?php
		//print_r($soldCoins);
		foreach($soldSongs as $row){
			$id = getValFromItem($row['id']);
			$name = getValFromItem($row['name']);
			$cost = getValFromItem($row['cost']);
			$coins = getValFromItem($row['coins']);
			$sold = getValFromItem($row['sold']);
			$sum = ($cost / $coins)  * $sold ;
			$percentage = 3;
			$profit = $sum * ($percentage / 100) / 100;	
//			if ($profit <= 0)
//				continue; 
			print("<tr>");
			printf("<td>$name</td>");
			printf("<td>".round(($cost/$coins)/100, 3)."</td>");
			printf("<td>$sold</td>");
			printf("<td>".round($sum/100, 2)."</td>");
			printf("<td>$percentage %%</td>");
			printf("<td>".round($profit,2)."</td>");
			print("</tr>\n");
		}
		if (count($soldSongs)==0){
			print("<tr>");
			print("<td colspan='7'>Brak danych</td>");
			print("</tr>\n"); 
		}
	?>
	</table>
	<h2> Historia sprzedaży opracowań</h2>
	<table border="1">
		<tr>
			<td> Miesiąc </td>
			<td> Sprzedaż </td>
		</tr>
	<?php
		//print_r($history_month);
		foreach($history_month_songs as $month){
			$year = getValFromItem($month['year']);
			$month1 = getValFromItem($month['month']);
			$orders = getValFromItem($month['orders']);
			print("<tr>");
			printf("<td>$year-%02d</td>", $month1);
			print("<td style='text-align:right'>$orders</td>");
			print("</tr>\n");
		}
		if (count($history_month_songs)==0){
			print("<tr>");
			print("<td colspan='2'>Brak danych</td>");
			print("</tr>\n"); 
		}
	?>
	</table>
	
</body>
</html>


 <?php //print_r($GLOBALS); ?>
<?php } ?>