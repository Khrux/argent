import sys
from pathlib import Path
from PIL import Image

OUTLINE = (43, 52, 64, 255)
SHADOW = (85, 98, 111, 255)
MID = (133, 147, 162, 255)
LIGHT = (184, 197, 210, 255)
HIGHLIGHT = (223, 232, 241, 255)
SHADES = {"o": OUTLINE, "s": SHADOW, "m": MID, "l": LIGHT, "h": HIGHLIGHT}

BOXES = [
    ((0, 0), (3, 4, 3)),
    ((0, 8), (2, 1, 2)),
    ((0, 12), (2, 1, 4)),
]

ICON = [
    "................",
    "......oooo......",
    ".....ohhlmo.....",
    ".....olmmso.....",
    "......oooo......",
    "................",
    "...oo......oo...",
    "..ohmo....omso..",
    "..ohlmooooommso.",
    "...ohlmmmmmmso..",
    "....ohlmmmmso...",
    "....ohlmhmmso...",
    "....ohlmmmmso...",
    ".....ohlmmso....",
    "......ossso.....",
    ".......ooo......",
]


def paint_box(image, origin, size):
    u, v = origin
    width, height, depth = size
    faces = {
        "top": (u + depth, v, width, depth, LIGHT),
        "bottom": (u + depth + width, v, width, depth, SHADOW),
        "right": (u, v + depth, depth, height, MID),
        "front": (u + depth, v + depth, width, height, MID),
        "left": (u + depth + width, v + depth, depth, height, MID),
        "back": (u + depth + width + depth, v + depth, width, height, SHADOW),
    }
    for name, (x, y, w, h, colour) in faces.items():
        for column in range(w):
            for row in range(h):
                shade = colour
                if name in ("right", "front", "left") and row == 0 and h > 1:
                    shade = LIGHT
                if name in ("right", "front", "left") and row == h - 1 and h > 2:
                    shade = SHADOW
                if name == "front" and column == w // 2 and 0 < row < h - 1:
                    shade = HIGHLIGHT
                image.putpixel((x + column, y + row), shade)


def main(assets):
    assets = Path(assets)
    texture = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for origin, size in BOXES:
        paint_box(texture, origin, size)
    output = assets / "textures/entity/parrot/silver_parrot_armor.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    texture.save(output)
    print(output)

    icon = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, line in enumerate(ICON):
        for x, mark in enumerate(line):
            if mark in SHADES:
                icon.putpixel((x, y), SHADES[mark])
    output = assets / "textures/item/silver_parrot_armor.png"
    icon.save(output)
    print(output)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit("usage: render_parrot_armor.py <src/main/resources/assets/<modid>>")
    main(sys.argv[1])
