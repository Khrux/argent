import sys
from pathlib import Path
from PIL import Image

OUTLINE = (92, 45, 48, 255)
DARK = (113, 62, 62, 255)
SHADOW = (150, 89, 84, 255)
MID = (160, 100, 96, 255)
LIGHT = (182, 123, 118, 255)
HIGHLIGHT = (198, 136, 132, 255)
TRIM_DARK = (170, 170, 170, 255)
TRIM = (191, 191, 191, 255)
TRIM_LIGHT = (205, 205, 205, 255)
SHADES = {"o": OUTLINE, "d": DARK, "s": SHADOW, "m": MID, "l": LIGHT, "h": HIGHLIGHT}
TRIMS = {"t": TRIM_DARK, "T": TRIM, "L": TRIM_LIGHT}

BODY = ((0, 0), (4, 14, 6))
CAP = ((0, 24), (4, 1, 3))

ICON = [
    "................",
    "................",
    "................",
    "....oooooooo....",
    "...ohhlTTlhho...",
    "..ohlmmTTmmlho..",
    "..olmmsLLsmmlo..",
    "..olmsdTTdsmlo..",
    "..odsdoTTodsdo..",
    "..odoo.tt.oodo..",
    "..oo...tt...oo..",
    "...............",
    "................",
    "................",
    "................",
    "................",
]


def faces(origin, size):
    u, v = origin
    width, height, depth = size
    return {
        "chest": (u + depth, v, width, depth),
        "rear": (u + depth + width, v, width, depth),
        "right": (u, v + depth, depth, height),
        "belly": (u + depth, v + depth, width, height),
        "left": (u + depth + width, v + depth, depth, height),
        "back": (u + depth + width + depth, v + depth, width, height),
    }


def plates(image, rect, edge_column=None):
    x, y, w, h = rect
    for row in range(h):
        band = (row // 3) % 2
        for column in range(w):
            colour = LIGHT if band == 0 else MID
            if row % 3 == 2:
                colour = SHADOW
            if edge_column is not None and column == edge_column:
                colour = DARK
            image.putpixel((x + column, y + row), colour)


def strap(image, rect, row, colour):
    x, y, w, h = rect
    for column in range(w):
        image.putpixel((x + column, y + row), colour)


def main(assets):
    assets = Path(assets)
    base = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    overlay = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    body = faces(*BODY)
    plates(base, body["back"])
    plates(base, body["right"], edge_column=0)
    plates(base, body["left"], edge_column=5)
    x, y, w, h = body["chest"]
    for column in range(w):
        for row in range(h):
            base.putpixel((x + column, y + row), HIGHLIGHT if row < 2 else MID)
            overlay.putpixel((x + column, y + row), TRIM_LIGHT if row < 2 else TRIM)
    for row in (1, 8):
        strap(base, body["belly"], row, SHADOW)
        strap(overlay, body["belly"], row, TRIM_DARK)
    x, y, w, h = body["back"]
    for row in range(h):
        for column in (1, 2):
            overlay.putpixel((x + column, y + row), TRIM if row % 3 != 2 else TRIM_DARK)
    for name, rect in faces(*CAP).items():
        if name in ("belly", "rear"):
            continue
        cx, cy, cw, ch = rect
        for column in range(cw):
            for row in range(ch):
                base.putpixel((cx + column, cy + row), LIGHT if name == "chest" else SHADOW)
        if name == "back":
            for column in range(cw):
                overlay.putpixel((cx + column, cy), TRIM)
    folder = assets / "textures/entity/cat"
    folder.mkdir(parents=True, exist_ok=True)
    base.save(folder / "cat_armor.png")
    overlay.save(folder / "cat_armor_overlay.png")

    icon = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    icon_overlay = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, line in enumerate(ICON):
        for x, mark in enumerate(line):
            if mark in SHADES:
                icon.putpixel((x, y), SHADES[mark])
            elif mark in TRIMS:
                icon.putpixel((x, y), MID)
                icon_overlay.putpixel((x, y), TRIMS[mark])
    folder = assets / "textures/item"
    folder.mkdir(parents=True, exist_ok=True)
    icon.save(folder / "cat_armor.png")
    icon_overlay.save(folder / "cat_armor_overlay.png")
    print(folder)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit("usage: render_cat_armor.py <src/main/resources/assets/<modid>>")
    main(sys.argv[1])
