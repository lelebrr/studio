Add-Type -AssemblyName System.Drawing

function Resize-And-Process {
    param (
        [string]$InputPath,
        [string]$OutputPath,
        [int]$Width,
        [int]$Height,
        [bool]$RemoveBackground = $false
    )

    $img = [System.Drawing.Bitmap]::FromFile($InputPath)
    $newImg = New-Object System.Drawing.Bitmap($Width, $Height)
    $g = [System.Drawing.Graphics]::FromImage($newImg)
    
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality

    # If it's adaptive foreground, we might want to scale it down slightly to fit the safe zone (72/108 = 66%)
    # But the user asked for 108x108.
    $g.DrawImage($img, 0, 0, $Width, $Height)
    $g.Dispose()

    if ($RemoveBackground) {
        # Simple chroma key for dark/black areas
        for ($x = 0; $x -lt $newImg.Width; $x++) {
            for ($y = 0; $y -lt $newImg.Height; $y++) {
                $c = $newImg.GetPixel($x, $y)
                # Threshold for "background" (black circle)
                # Also check distance from center to remove the border circle if needed
                $distX = $x - ($Width / 2)
                $distY = $y - ($Height / 2)
                $dist = [Math]::Sqrt($distX * $distX + $distY * $distY)
                $radius = $Width * 0.45 # Rough radius of the logo parts

                # If it's outside a certain radius OR very dark, make it transparent
                if ($dist -gt $radius -or ($c.R -lt 60 -and $c.G -lt 60 -and $c.B -lt 60)) {
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

if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir
}

Write-Host "Generating Legacy Icons..."
Resize-And-Process $inputIcon "$outputDir\ic_launcher.png" 48 48
Resize-And-Process $inputIcon "$outputDir\ic_launcher_72.png" 72 72
Resize-And-Process $inputIcon "$outputDir\ic_launcher_96.png" 96 96
Resize-And-Process $inputIcon "$outputDir\ic_launcher_144.png" 144 144
Resize-And-Process $inputIcon "$outputDir\ic_launcher_192.png" 192 192

Write-Host "Generating Adaptive Foreground (108x108)..."
Resize-And-Process $inputIcon "$outputDir\ic_launcher_foreground.png" 108 108 $true

Write-Host "Generating Full Adaptive Icon (216x216)..."
Resize-And-Process $inputIcon "$outputDir\ic_launcher_adaptive_full.png" 216 216

Write-Host "All icons generated successfully in $outputDir"
