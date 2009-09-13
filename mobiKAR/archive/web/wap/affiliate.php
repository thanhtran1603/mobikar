<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');
$code1= null;
if (isset ($_GET['code1']))
	$code1= $_GET['code1'];
$code2= null;
if (isset ($_GET['code2']))
	$code2= $_GET['code2'];
$isAllowed= false;
$reason_text= null;
$user1= null;
$user2= null;
if ($code1 != null && $code2 != null) {
	$db= new Database();
	$code1= strtoupper($code1);
	$rowPayment= $db->getPayment($code1);
	if ($rowPayment == null) {
		$reason_text= "Kod nr 1 jest nieprawid&#x142;owy";
	}
	elseif (isset ($rowPayment['id_user'])) 
		$user1= $rowPayment['id_user'];
	$code2= strtoupper($code2);
	$rowPayment= $db->getPayment($code2);
	if ($rowPayment == null) {
		$reason_text .= "<br/>Kod nr 2 jest nieprawid&#x142;owy";
	}
	elseif (isset ($rowPayment ['id_user'])){
			$user2= $rowPayment ['id_user'];
	}
	trigger_error("user1: $user1 user2: $user2", E_USER_NOTICE);
	if ($user1 != $user2 && $user1 != null && $user2 != null 
	   && $user1 != 0 && $user2 != 0) {
		$rowGuest= $db->getInvited($user1);
		if ($rowGuest != null) {
			$reason_text .= "W&#x142;a&#x15b;ciciel kodu 1 zosta&#x142; ju&#x17c; zaproszony";
		}
		else {
			$rowGuest= $db->getInvited($user2);
			if ($rowGuest != null) {
				$reason_text .= "W&#x142;a&#x15b;ciciel kodu 2 zosta&#x142; ju&#x17c; zaproszony";
			}
			else {
				// dodajemy zaproszenia
				$hostId= $uid;
				$db->addInvited($user1, $hostId);
				$db->addInvited($user2, $hostId);
				// dodajemy p&#x142;atno&#x15b;&#x107;
				$code= getCode();
				$idPaymentType= 6; // 6 - Werbunek 
				$idProvider= 3; // 3 - Affiliate Program
				$db->addCodeToPayment($code, $idPaymentType, $idProvider);
				if ($db->updateUserAccount($uid, $code) == true) {
					$isAllowed= true;
				}
			}
		}
	}
	$db->destroy();
}

$wap_title= "mobiKAR - przy&#x142;&#x105;cz si&#x119;";

include ("add_head.php");
?>
<h1>Powiadom znajomych</h1>
<?php if ($isAllowed == false) { ?>
	<div>
		<?php if ($reason_text != null) echo "$reason_text<br/>"; ?>
		Je&#x15b;li masz ju&#x17c; mobiKARa i chcesz wi&#x119;cej piosenek - mo&#x17c;esz do&#x142;adowa&#x107; konto 
		wykupuj&#x105;c kod lub otrzyma&#x107; <b>do&#x142;adowanie za darmo</b>.
		<br/>
		Wystarczy powiadomi&#x107; swoich znajomych o aplikacji mobiKAR. 
		<br/>
		Je&#x15b;li ze znajomych co najmniej dwie osoby zakupi&#x105; kod i zaloguj&#x105; si&#x119; 
		w serwisie jako nowi u&#x17c;ytkownicy, w&#xf3;wczas mo&#x17c;esz poda&#x107; poni&#x17c;ej kody zam&#xf3;wie&#x144; 
		Twoich znajomych a otrzymasz jeden punkt, kt&#xf3;ry pozwoli Ci pobra&#x107;  
		aplikacj&#x119; mobiKAR z dowoln&#x105; piosenk&#x119; z serwisu.
	</div>
	<div style="text-align: center">
		<form title="Sprawdz" name="Sprawdz" action="affiliate.php" method="get">
			<fieldset>
				<input name="uid" value="<?=$uid?>" type="hidden"/>
				<input name="sid" value="<?=$sid?>" type="hidden"/>
				<input name="kod" value="<?=$kod?>" type="hidden"/>
				Kod 1:  <input name="code1" size="8" maxlength="8" type="text"/>
				<br/>
				Kod 2:  <input name="code2" size="8" maxlength="8" type="text"/>
				<br/>
				<input name="submit" type="submit" value="Sprawdz"/>
			</fieldset>
		</form>
	</div>

<?php } elseif ($isAllowed == true) { ?>
	<h2>Dzi&#x119;kujemy za namow&#x119;</h2>
	<div>
		Dzi&#x119;ki Tobie poszerzy&#x142;o si&#x119; grono u&#x17c;ytkownik&#xf3;w aplikacji mobiKAR.
		<br/>
		Staramy si&#x119; aby mobiKAR by&#x142; aplikacj&#x105; najwy&#x17c;szej jako&#x15b;ci a zarazem 
		spe&#x142;nia&#x142; potrzeby naszych u&#x17c;ytkownik&#xf3;w.
		<br/>
		Drogi u&#x17c;ytkowniku,
		<br/>
		Je&#x15b;li posiadasz pomys&#x142; na popraw&#x119; aplikacji mobiKAR,
		prosimy, aby&#x15b; po&#x15b;wi&#x119;ci&#x142; chwilk&#x119; czasu i podzieli&#x142; si&#x119; z nami Twoim pomys&#x142;em.
		<br/>
		Pozwoli to nam ci&#x105;gle poprawia&#x107; jako&#x15b;&#x107;, funkcjonalno&#x15b;&#x107; i co za tym idzie zadowolenie naszych u&#x17c;ytkownik&#xf3;w.
		<br/>
		adres: <b>info</b>(ma&#x142;pa)<b>mobikar.net</b>
		<br/>
		Zesp&#xf3;&#x142; mobiKAR.net  
	</div>
<?php } ?>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
</div>
<?php
include ("add_foot.php");
?>