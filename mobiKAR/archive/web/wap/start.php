<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');
$addResultText = "";
$code = getParameter('code');
if ($code != null) {
	$code = strtoupper($_GET['code']);
	$db = new Database();
	
	if ($db->updateUserAccount($uid, $code) == true) {
		$addResultText = "Dzi&#x119;kujemy za zakup<br/>";
		include ('session_check.php');
	} else {
		$addResultText = "Podany kod jest niew&#x142;a&#x15b;ciwy<br/>";
	}
	
	$db->destroy();
}
$user_login = "go&#x15b;ciu";
// -1 aby nie proponować gościowi piosenki za darochę
$user_coins = -1;
if ($uid != null) {
	$user_login = $rowUser['login'];
	$user_coins = $rowUser['coins'];
}
trigger_error("user_coins: ". $user_coins, E_USER_NOTICE);
trigger_error("$rowUser[coins]: ". $rowUser['coins'], E_USER_NOTICE);
 
$wap_title = "mobiKAR";
include ("add_head.php");
?>
<h1>Witaj <?=$user_login?></h1>
<div>
	<?php if ($user_coins == 0){ ?>
		Przed kupnem upewnij si&#x119;, &#x17c;e mobiKAR dzia&#x142;a na Twoim 
		telefonie pobieraj&#x105;c za darmo pe&#x142;n&#x105; wersj&#x119; aplikacji
		<br/>
		<a href='product.php<?=$mySID?>&amp;id=66'> Testuj mobiKAR-a </a>
		<br/>
	<?php }?>
	<?php if ($addResultText != NULL) echo "$addResultText<br/>"; ?>
	
	<?php if ($user_coins >= 0){ ?> 
		Liczba Twoich &#x17c;eton&#xf3;w: <?=$user_coins?>
		<br/>
	
	Dokup <a href="pay.php<?=$mySID?>"> kod </a> do&#x142;adowuj&#x105;cy konto
	lub do&#x142;aduj konto za <a href="affiliate.php<?=$mySID?>">darmo</a>.
	<br/>
	Poni&#x17c;ej podaj kod do&#x142;adowuj&#x105;cy Twoje konto
</div>
<div style="text-align: center">
	<form title="Doladuj" name="Doladuj" action="start.php<?=$mySID?>" method="get">
		<fieldset>
			<input name="uid" value="<?=$uid?>" type="hidden"/>
			<input name="sid" value="<?=$sid?>" type="hidden"/>
			<input name="kod" value="<?=$kod?>" type="hidden"/>
			KOD: <input name="code" size="8" maxlength="8" type="text"/>
			<br/>
			<input name="submit" type="submit" value="Doladuj"/>
		</fieldset>
	</form>
</div>
<div>
	<?php }?>

	
	Poni&#x17c;ej lista piosenek - nieustannie aktualizowana.
	<br/>
	Przegl&#x105;daj i &#x15b;ci&#x105;gaj na telefon aplikacj&#x119; mobiKAR z jedn&#x105; lub trzema piosenkami		
</div>
<h2>Przegl&#x105;daj</h2>
<div>
	<a href="categories.php<?=$mySID?>&amp;tid=2"> Single </a>
	<br/>
	<a href="categories.php<?=$mySID?>&amp;tid=3"> Pakiety </a>
</div>
<h2>Skacz</h2>
<div>
 	Je&#x15b;li masz ju&#x17c; wybran&#x105; piosenk&#x119; czy paczk&#x119;, 
 	wpisz jej numer aby szybko do niej przeskoczy&#x107;
</div>
<div style="text-align: center">
	<form title="Skacz" name="Skacz" action="product.php" method="get">
		<fieldset>
			<input name="uid" value="<?=$uid?>" type="hidden"/>
			<input name="sid" value="<?=$sid?>" type="hidden"/>
			<input name="kod" value="<?=$kod?>" type="hidden"/>
			Numer: <input name="id" size="8" maxlength="8" type="text"/>
			<br/>
			<input name="submit" type="submit" value="Skacz"/>
		</fieldset>
	</form>
</div>
<h2>Kontakt</h2>
<div>
 	Je&#x15b;li chcesz nawi&#x105;za&#x107; kontakt lub przesa&#x142;a&#x107; swoje uwagi to wype&#x142;nij formularz i wy&#x15b;lij go nam.
 	<br/>
 	Dzi&#x119;kujemy za wszelkie uwagi, kt&#xf3;re pomog&#x105; mam jeszcze lepiej dopracowa&#x107; nasz serwis.
</div>
<div style="text-align: center">
	<form title="Wyslij" name="Wyslij" action="send.php" method="get">
		<fieldset>
			<input name="uid" value="<?=$uid?>" type="hidden"/>
			<input name="sid" value="<?=$sid?>" type="hidden"/>
			<input name="kod" value="<?=$kod?>" type="hidden"/>
			<input name="user" value="Tw&#xf3;j ID lub e-mail" type="text"/>
			<br/>
			<textarea name="msg" cols="40" rows="5"></textarea>
			<br/>
			<input name="submit" type="submit" value="Wyslij"/>
		</fieldset>
	</form>
</div>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href='reg.php<?=$mySID?>'> Regulamin </a>
	<br/>
	<a href='index.php<?=$mySID?>'> Strona startowa </a>
</div>

<?php

include ("add_foot.php");
?>