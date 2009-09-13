<?php
include_once ('../scripts/utils.php');
$par_file = getParameter('filename', "");
$result = chmod($par_file, 0777);
print ("result:" . $result);
?>
