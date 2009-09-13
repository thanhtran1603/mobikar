<?php
$postdata = file_get_contents("php://input");
ob_end_clean();
header( "Content-type: text/html" );
echo "postdata: $postdata";
?>