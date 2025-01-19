# Define the array of folder paths to search
$folderPaths = @(
    "C:\github\GeoRocksUNAM\app\src\main\java\com\enigma\georocks",
    "C:\github\GeoRocksUNAM\app\src\main"
)

# Specify the path for the output text file
$outputFile = "C:\github\GeoRocksUNAM\app\prompt.txt"  # <-- Update this path if necessary

# Initialize or clear the output file with UTF8 encoding without BOM
# For PowerShell 6.0 and above
$utf8NoBomEncoding = [System.Text.Encoding]::UTF8
$utf8NoBomEncodingWithoutBOM = New-Object System.Text.UTF8Encoding($false)
Set-Content -Path $outputFile -Value "" -Encoding UTF8

# Initialize a hash set to keep track of processed files and avoid duplication
$processedFiles = @{}

# Loop through each folder path
foreach ($folderPath in $folderPaths) {
    if (Test-Path -Path $folderPath) {
        # Get all .kt and .xml files recursively from the current folder
        $files = Get-ChildItem -Path $folderPath -Recurse -Include *.kt, *.xml -File

        foreach ($file in $files) {
            # Check if the file has already been processed (to avoid duplicates)
            if (-not $processedFiles.ContainsKey($file.FullName)) {
                # Add the file path to the hash set
                $processedFiles[$file.FullName] = $true

                # Create a header with the file name
                $header = "----- $($file.FullName) -----`n"

                # Append the header to the output file
                [System.IO.File]::AppendAllText($outputFile, $header, $utf8NoBomEncodingWithoutBOM)

                # Read the content of the current file
                $content = Get-Content -Path $file.FullName -Raw

                # Append the content to the output file
                [System.IO.File]::AppendAllText($outputFile, $content + "`n`n", $utf8NoBomEncodingWithoutBOM)
            }
        }
    }
    else {
        Write-Warning "The path '$folderPath' does not exist."
    }
}

# Notify the user that the operation is complete
Write-Host "All .kt and .xml files from the specified directories have been successfully saved to $outputFile"

