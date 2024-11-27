@echo off
:: Set the input folder to the folder where the script is located
set input_dir=%~dp0
set output_dir=%input_dir%trimmed_images

:: Create the output folder if it doesn't exist
if not exist "%output_dir%" mkdir "%output_dir%"

:: Process all PNG files in the folder
for %%f in (%input_dir%*.png) do (
    magick "%%f" -trim +repage "%output_dir%\%%~nxf"
    echo Trimmed %%f
)

pause
