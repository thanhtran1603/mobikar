<?php
/*
 * Created on 2005-06-15
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
$jar= null;
include ("../scripts/config.php");
if (isset ($cfg['Programs']['jar']))
	$jar= $cfg['Programs']['jar'];
$output= null;
echo exec($jar . $opts, $output);
$opts=" -cMf ala.jar .";
system($jar . $opts);
print_r($output);
?>


