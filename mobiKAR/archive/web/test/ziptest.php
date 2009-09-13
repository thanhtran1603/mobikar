<?php
$zip= zip_open("./test.zip");
if ($zip) {
	while ($zip_entry= zip_read($zip)) {
		if (strcmp("META-INF/MANIFEST.MF", zip_entry_name($zip_entry)) == 0) {
			if (zip_entry_open($zip, $zip_entry, "r")) {
				echo "File Contents:\n";
				$buf= zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));
				zip_entry_close($zip_entry);
				$buf = trim($buf) . "\nMoje-dane: ala ma kota";
				echo trim($buf);
				echo realpath("./test.zip");
				$kod = "dsds";
				mkdir("../get/$kod");
				$file = fopen ("../get/$kod/file.txt", "wb");
				fwrite($file, $buf);
				fclose($file);
				copy("./test.zip", "../get/$kod/test.zip");
			}
		}
	}
	zip_close($zip);
	
}
?>