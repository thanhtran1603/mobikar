@REM MASK="0xF0F0F0" - 12 bitów

SET MASK=0xF0F0F0
SET CMD=java -cp D:\projects\java\ImageDeepDecrease ImageDeepDecrease

cd D:\projects\java\ME\mobiKAR\res

%CMD%  bkg.png     bkg.png     %MASK% 

SET MASK=
SET CMD=

cd D:\projects\java\ME\mobiKAR

pause