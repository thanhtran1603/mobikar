<?php
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utf8ToEntities.php');
include_once ('../scripts/Database.php');
$reason= "BAD_USER";
$tid= NULL;
$isHasCategories= FALSE;
$productName=null;
if (isset ($_GET['tid'])) {
	$tid= $_GET['tid'];
	$db= new Database();
	$categories= $db->getCategories($tid);
	if ($categories != NULL) {
		$isHasCategories= TRUE;
		if ($tid == 2)
			$productName = "Single";
		else
			$productName = "Paczki";
	}
	$db->destroy();
}
$wap_title= "mobiKAR - kategorie";
include ("add_head.php");
?>
<h1><?=$productName?></h1>
<h2>Kategorie</h2>
<ul>
	<?php
		$cid = -1;
		echo '<li><a href="products.php'.$mySID.'&amp;tid='.$tid.'&amp;cid='.$cid.'">Nowo&#x15b;ci</a></li>'."\n";
		foreach ($categories as $category) {
			$cid = $category['id'];
			$name= utf8ToEntities($category['name']);
			echo '<li><a href="products.php'.$mySID.'&amp;tid='.$tid.'&amp;cid='.$cid.'">'.$name.'</a></li>'."\n";
		}
	?>
</ul>
<h2>Przejd&#x17a; do:</h2>
<div>
	<a href='start.php<?=$mySID?>'> Strona startowa </a>
</div>
<?php
include ("add_foot.php");
?>