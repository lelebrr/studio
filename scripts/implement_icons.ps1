Add-Type -AssemblyName System.Drawing

function Resize-And-Process {
    param (
        [string]$InputPath,
        [string]$OutputPath,
        [int]$Width,
        [int]$Height,
        [bool]$CleanForeground = $false
    )

    $img = [System.Drawing.Bitmap]::FromFile($InputPath)
    $newImg = New-Object System.Drawing.Bitmap($Width, $Height)
    $g = [System.Drawing.Graphics]::FromImage($newImg)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.DrawImage($img, 0, 0, $Width, $Height)
    $g.Dispose()

    if ($CleanForeground) {
        for ($x = 0; $x -lt $Width; $x++) {
            for ($y = 0; $y -lt $Height; $y++) {
                $c = $newImg.GetPixel($x, $y)
                $dist = [Math]::Sqrt([Math]::Pow($x - ($Width/2), 2) + [Math]::Pow($y - ($Height/2), 2))
                
                # Check for blue/white metallic parts
                $isBlue = ($c.B -gt $c.R -and $c.B -gt $c.G)
                $isBright = ($c.R -gt 180 -and $c.G -gt 180 -and $c.B -gt 180)
                
                if (($isBlue -or $isBright) -and ($dist -lt ($Width * 0.42))) {
                    # Keep
                } else {
                    $newImg.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
                }
            }
        }
    }

    $newImg.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $newImg.Dispose()
    $img.Dispose()
}

$inputIcon = "D:\Projetos\Studio de fotos\icone.png"
$outputDir = "D:\Projetos\Studio de fotos\generated_icons"
$resDir = "d:\Projetos\Studio de fotos\studio\app\src\main\res"

if (-not (Test-Path $outputDir)) { New-Item -ItemType Directory -Path $outputDir }

# 1. Generate Icons
Write-Host "Generating Legacy Icons..."
Resize-And-Process $inputIcon "$outputDir\ic_launcher_48.png" 48 48
Resize-And-Process $inputIcon "$outputDir\ic_launcher_72.png" 72 72
Resize-And-Process $inputIcon "$outputDir\ic_launcher_96.png" 96 96
Resize-And-Process $inputIcon "$outputDir\ic_launcher_144.png" 144 144
Resize-And-Process $inputIcon "$outputDir\ic_launcher_192.png" 192 192

Write-Host "Generating Foreground (108x108)..."
Resize-And-Process $inputIcon "$outputDir\ic_launcher_foreground.png" 108 108 $true

# 2. Implement (Copy to Project)
Write-Host "Implementing into Android Project..."
Copy-Item "$outputDir\ic_launcher_48.png" "$resDir\mipmap-mdpi\ic_launcher.png" -Force
Copy-Item "$outputDir\ic_launcher_72.png" "$resDir\mipmap-hdpi\ic_launcher.png" -Force
Copy-Item "$outputDir\ic_launcher_96.png" "$resDir\mipmap-xhdpi\ic_launcher.png" -Force
Copy-Item "$outputDir\ic_launcher_144.png" "$resDir\mipmap-xxhdpi\ic_launcher.png" -Force
Copy-Item "$outputDir\ic_launcher_192.png" "$resDir\mipmap-xxxhdpi\ic_launcher.png" -Force

# Copy Foreground to drawable
if (-not (Test-Path "$resDir\drawable")) { New-Item -ItemType Directory -Path "$resDir\drawable" }
Copy-Item "$outputDir\ic_launcher_foreground.png" "$resDir\drawable\ic_launcher_foreground.png" -Force

Write-Host "Done!"
