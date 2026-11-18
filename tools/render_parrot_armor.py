import sys
from pathlib import Path
from PIL import Image

OUTLINE = (53, 53, 53, 255)
SHADOW = (115, 115, 115, 255)
MID = (150, 150, 150, 255)
LIGHT = (179, 179, 179, 255)
HIGHLIGHT = (207, 207, 207, 255)
SHADES = {"o": OUTLINE, "s": SHADOW, "m": MID, "l": LIGHT, "h": HIGHLIGHT}

SIDE = (176, 176, 176, 255)
TOP = (204, 204, 204, 255)
UNDER = (128, 128, 128, 255)
BACK = (152, 152, 152, 255)
EDGE = (190, 190, 190, 255)
HEM = (140, 140, 140, 255)
STITCH = (238, 236, 230, 255)

BOXES = {
    "body": ((0, 0), (3, 4, 3)),
    "cap": ((0, 8), (2, 1, 2)),
    "crown": ((0, 12), (2, 1, 4)),
}

ICON = [
    "................",
    "..ooooooooo.....",
    "..ohhhhhhlo.....",
    "..ooooolmso.....",
    "......olmso.....",
    "......ohlso.....",
    "......oohho.....",
    ".......ohlmo....",
    ".....oohlmmo....",
    "....ohhlmmmso...",
    "....olmmmmmso...",
    ".....olmmmmsso..",
    ".....olmmmssoo..",
    "......olssoo....",
    "......oooo......",
    "................",
]


def faces(origin, size):
    u, v = origin
    width, height, depth = size
    return {
        "top": (u + depth, v, width, depth),
        "bottom": (u + depth + width, v, width, depth),
        "right": (u, v + depth, depth, height),
        "front": (u + depth, v + depth, width, height),
        "left": (u + depth + width, v + depth, depth, height),
        "back": (u + depth + width + depth, v + depth, width, height),
    }


def fill(image, rect, colour):
    x, y, w, h = rect
    for column in range(w):
        for row in range(h):
            image.putpixel((x + column, y + row), colour)


def paint_box(image, origin, size):
    colours = {"top": TOP, "bottom": UNDER, "right": SIDE, "front": SIDE, "left": SIDE, "back": BACK}
    for name, rect in faces(origin, size).items():
        fill(image, rect, colours[name])
        x, y, w, h = rect
        if name in ("right", "front", "left", "back") and h > 2:
            for column in range(w):
                image.putpixel((x + column, y), EDGE)
                image.putpixel((x + column, y + h - 1), HEM)


def stitch(image):
    body = faces(*BOXES["body"])
    for name in ("front", "back"):
        x, y, w, h = body[name]
        image.putpixel((x + w // 2, y + 1), STITCH)
        image.putpixel((x + w // 2, y + 3), STITCH)
    for name in ("right", "left"):
        x, y, w, h = body[name]
        for column in range(0, w, 2):
            image.putpixel((x + column, y + h - 1), STITCH)


def main(assets):
    assets = Path(assets)
    texture = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for origin, size in BOXES.values():
        paint_box(texture, origin, size)
    stitch(texture)
    output = assets / "textures/entity/parrot/parrot_armor.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    texture.save(output)
    print(output)

    icon = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, line in enumerate(ICON):
        for x, mark in enumerate(line):
            if mark in SHADES:
                icon.putpixel((x, y), SHADES[mark])
    output = assets / "textures/item/parrot_armor.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    icon.save(output)
    print(output)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit("usage: render_parrot_armor.py <src/main/resources/assets/<modid>>")
    main(sys.argv[1])
