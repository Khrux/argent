import sys
from pathlib import Path
from PIL import Image

SIZE = 16
SHINE = {0: 0.85, 1: 0.5, 15: 0.4, 5: 0.3}


def palette(glass):
    return sorted(set(glass.get_flattened_data()), key=lambda colour: sum(colour[:3]))


def shade(colours, level):
    return colours[round(level * (len(colours) - 1))]


def brightness(x, y):
    diagonal = (x + y) % SIZE
    if diagonal in SHINE:
        return SHINE[diagonal]
    return 0.15 * (1.0 - abs(diagonal - 10) / 6.0)


def main():
    assets = Path(sys.argv[1])
    glass = Image.open(assets / "textures/block/mirror_glass_top.png").convert("RGBA")
    colours = palette(glass)
    tile = Image.new("RGBA", (SIZE, SIZE))
    for x in range(SIZE):
        for y in range(SIZE):
            tile.putpixel((x, y), shade(colours, max(0.0, brightness(x, y))))
    tile.save(assets / "textures/block/mirror_glass_tile.png")


if __name__ == "__main__":
    main()
