<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utils.php');
$id= getParameter('id');
$db = null;
$db['login_id']['title']= "identyfikator u&#x17c;ytkownika";
$db['login_id']['text']= "Identyfikator u&#x17c;ytkownika jest unikaln&#x105; nazw&#x105; przypisan&#x105; dla u&#x17c;ytkownika portalu mobiKAR.net.<br/>"
. "Identyfikator u&#x17c;ytkownika wykorzystywany jest do logowania si&#x119; w portalu mobiKAR.net.";
$db['login_password']['title']= "has&#x142;o u&#x17c;ytkownika";
$db['login_password']['text']= "Has&#x142;o jest tajnym ci&#x105;giem znak&#xf3;w, kt&#xf3;ry w po&#x142;&#x105;czneniu z identyfikatorem stanowi przepustk&#x119; do portalu mobiKAR.<br/> Pami&#x119;taj i&#x17c; w zapami&#x119;tanym ha&#x15b;le wa&#x17c;na jest wielko&#x15b;&#x107; liter.";
$db['phones_midp20']['title']= "Telefony MIDP 2.0";
$db['phones_midp20']['text']= "Telefony posiadaj&#x105;ce &#x15b;rodowisko JAVA o profilu MIDP 2.0 standardowo maj&#x105; wbudowan&#x105; obs&#x142;ug&#x119; multimedi&#xf3;w MMAPI 1.1. Poni&#x17c;ej lista telefon&#xf3;w z MIDP 2.0. Je&#x15b;li w poni&#x17c;szej li&#x15b;cie nie znajdujesz swojego telefonu a uwa&#x17c;asz, i&#x17c; Tw&#xf3;j telefon ma &#x15b;rodowisko JAVA MIDP 2.0 - zignoruj poni&#x17c;sz&#x105; list&#x119;, gdy&#x17c; nie jest ona cz&#x119;sto aktualizowana.<br/>Motorola: A630, A668, A760, A768, A780, A845, C380, C385, C650, C975, C980, E1000, E398, E550, E680, T725, V180, V186, V220, V3, V300, V303, V330, V400, V500, V501, V505, V525, V536, V545, V547, V550, V551, V555, V557, V600, V620, V635, V690, V80, V878, V975, V980;<br/>Nokia: 3220, 5140, 6020, 6170, 6220, 6230, 6260, 6600, 6620, 6630, 6638, 6670, 7260, 7270, 7280, 7610;<br/>Samsung: D500, E700, E800, D710;<br/>Siemens: C65, C66, CFX65, CX65, CX70, M65, S65, S66, SK65, SL65;<br/>SonyEricsson: K300, K500, K700, P900, P910, S700;<br/> LG C1100";
$db['phones_mmapi']['title']= "Telefony MMAPI";
$db['phones_mmapi']['text']= "Lista telefon&#xf3;w posiadaj&#x105;cych &#x15b;rodowisko JAVA ze wsparciem dla multimedi&#xf3;w MMAPI jest nast&#x119;puj&#x105;ca:<br/>Nokia: 3330, 3650, N-Gage;<br/>Siemens: SX1;<br/>SonyEricsson T610, T630, P800.";
$db['profit']['title']= "Korzy&#x15b;ci";
$db['profit']['text']= "Loguj&#x105;c si&#x119; do mobiKAR.net mo&#x17c;esz<br/> - pobiera&#x107; darmowe piosenki<br/> - wielokrotnie pobiera&#x107; zakupione piosenki jako aplikacja mobiKAR jak r&#xf3;wnie&#x17c; bezpo&#x15b;rednio do aplikacji,<br/> - wybiera&#x107; dowolne piosenki z listy,<br/> - bra&#x107; udzia&#x142; w programach promocyjnych<br/> ";
$wap_title= "mobiKAR - pomoc";
include ("add_head.php");

?>
<h1>Pomoc</h1>
	<?php if ($id == null) { ?>
		<div>
			Witaj w systemie pomocy portalu mobiKAR.net
		</div>
		<h2>Tematy:</h2>
		<ul>
		<?php
			foreach (array_keys($db) as $key) {
				$title = $db[$key]['title'];
				echo("<li><a href='help.php".$mySID."&amp;id=$key'> $title </a></li>");
			} 
		?>
		</ul>
		<h2>Przejd&#x17a; do:</h2>
		<div>
			<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
		</div>

	<?php } else {?>
		<h2><?=$db[$id]['title']?></h2>
		<div><?=$db[$id]['text']?></div>
		<h2>Przejd&#x17a; do:</h2>
		<div>
			<a href='help.php<?=$mySID?>'> Tematy pomocy </a>
			<br/>
			<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
		</div>
	<?php } ?>
<?php
trigger_error("B", E_USER_NOTICE);

include ("add_foot.php");
?>