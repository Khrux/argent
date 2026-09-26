import sys, zipfile, io, colorsys
from pathlib import Path
from PIL import Image

RAMP = [
    (0.00, (43, 52, 64)),
    (0.25, (85, 98, 111)),
    (0.50, (133, 147, 162)),
    (0.75, (184, 197, 210)),
    (0.90, (223, 232, 241)),
    (1.00, (246, 251, 255)),
]
GAMMA = 0.85

JOBS = [
    ("block/gold_ore.png", "block/silver_ore.png", True),
    ("block/deepslate_gold_ore.png", "block/deepslate_silver_ore.png", True),
    ("item/raw_gold.png", "item/raw_silver.png", False),
    ("item/gold_ingot.png", "item/silver_ingot.png", False),
    ("item/gold_nugget.png", "item/silver_nugget.png", False),
    ("block/gold_block.png", "block/silver_block.png", False),
    ("block/raw_gold_block.png", "block/raw_silver_block.png", False),
    ("entity/bell/bell_body.png", "block/silver_bell_body.png", True, 0.06, 0.2),
    ("block/bell_bottom.png", "block/silver_bell.png", True, 0.06, 0.2),
    ("item/bell.png", "item/silver_bell.png", True, 0.06, 0.2),
]

def ramp(t):
    t = max(0.0, min(1.0, t)) ** GAMMA
    for (a, ca), (b, cb) in zip(RAMP, RAMP[1:]):
        if t <= b:
            k = (t - a) / (b - a)
            return tuple(round(ca[i] + (cb[i] - ca[i]) * k) for i in range(3))
    return RAMP[-1][1]

def is_gold(r, g, b, min_hue=0.03, min_saturation=0.30):
    h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    return min_hue <= h <= 0.20 and s > min_saturation and v > 0.12

def luma(r, g, b):
    return 0.2126 * r + 0.7152 * g + 0.0722 * b

def recolor(img, ore, *gold_limits):
    img = img.convert("RGBA")
    px = img.load()
    w, h = img.size
    targets = [(x, y) for y in range(h) for x in range(w)
               if px[x, y][3] > 0 and (not ore or is_gold(*px[x, y][:3], *gold_limits))]
    if not targets:
        raise SystemExit("no gold pixels found")
    ls = [luma(*px[x, y][:3]) for x, y in targets]
    lo, hi = min(ls), max(ls)
    for (x, y), l in zip(targets, ls):
        px[x, y] = ramp((l - lo) / (hi - lo or 1)) + (px[x, y][3],)
    return img

def main(jar, out_root):
    out_root = Path(out_root)
    with zipfile.ZipFile(jar) as z:
        for src, dst, ore, *gold_limits in JOBS:
            name = "assets/minecraft/textures/" + src
            if name not in z.namelist():
                raise SystemExit(f"missing in jar: {name}")
            img = recolor(Image.open(io.BytesIO(z.read(name))), ore, *gold_limits)
            path = out_root / "textures" / dst
            path.parent.mkdir(parents=True, exist_ok=True)
            img.save(path)
            print(path)

if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise SystemExit("usage: recolor_silver.py <client.jar> <src/main/resources/assets/<modid>>")
    main(sys.argv[1], sys.argv[2])
