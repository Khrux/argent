import colorsys, io, sys, zipfile
from pathlib import Path
from PIL import Image

FLESH = [
    (0.00, (13, 12, 13)),
    (0.30, (31, 28, 29)),
    (0.60, (50, 46, 45)),
    (0.85, (66, 61, 59)),
    (1.00, (80, 75, 71)),
]
BONE = (98, 100, 97)
BONE_SHADOW = (67, 69, 68)
SOCKET = (8, 8, 9)
PUPIL = (178, 176, 160)
HOLLOW = (9, 9, 10)
DARK_FLESH = (22, 20, 21)
MARKS = {"B": BONE, "b": BONE_SHADOW, "H": HOLLOW, "S": SOCKET, "P": PUPIL, "D": DARK_FLESH}

REGIONS = {
    "skin": (0.20, 0.90),
    "shirt": (0.22, 0.72),
    "trousers": (0.18, 0.62),
    "shoes": (0.06, 0.34),
}

RIGHT_ARM = (40, 16)
LEFT_ARM = (32, 48)
RIGHT_LEG = (0, 16)
LEFT_LEG = (16, 48)
FINGER = (56, 16)

ROT = [
    ((8, 11), [".DD..DD.", ".SP..PS.", "...DD.bB", "..DBHBHB", "....DbBb"]),
    ((16, 13), ["Bb.", "HB.", "bB."]),
    ((20, 21), ["..HHHH..", ".BBBBBB.", ".HHbbHH.", ".BBBBBB.", ".HHbbHH.", "..BBBB..", "...HH..."]),
    ((16, 22), ["..BB", "..HH", "..BB", "..HH", "...B"]),
    ((32, 22), ["...bb...", "..HBBH..", "...bb...", "..HBBH..", "...bb...", "..HBBH..", "...bb..."]),
    ((40, 26), ["H..H", "HBBH", "HbbH", "HBBH", "H..H"]),
    ((44, 26), ["H..H", "HBBH", "HbbH", "HBBH", "H..H"]),
    ((52, 26), ["H..H", "HbbH", "HbbH", "HbbH", "H..H"]),
    ((20, 56), ["H..H", "HBBH", "HbbH", "HBBH", "HBBH", "H..H"]),
    ((24, 56), ["H..H", "HBBH", "HbbH", "HBBH", "HBBH", "H..H"]),
    ((56, 16), [".bB.", "....", "....", "B..."]),
]


def ramp(stops, t):
    t = max(0.0, min(1.0, t))
    for (a, ca), (b, cb) in zip(stops, stops[1:]):
        if t <= b:
            k = (t - a) / (b - a)
            return tuple(round(ca[i] + (cb[i] - ca[i]) * k) for i in range(3)) + (255,)
    return stops[-1][1] + (255,)


def luma(colour):
    return 0.2126 * colour[0] + 0.7152 * colour[1] + 0.0722 * colour[2]


def region(colour, y):
    hue, saturation, value = colorsys.rgb_to_hsv(*(channel / 255 for channel in colour[:3]))
    if saturation < 0.2:
        return "shoes" if y >= 16 else None
    if hue < 0.4:
        return "skin"
    if hue < 0.6:
        return "shirt"
    return "trousers"


def flesh(zombie):
    result = Image.new("RGBA", zombie.size, (0, 0, 0, 0))
    source = zombie.load()
    pixels = {}
    for x in range(zombie.width):
        for y in range(zombie.height):
            if source[x, y][3]:
                pixels.setdefault(region(source[x, y], y), []).append((x, y))

    target = result.load()
    for name, positions in pixels.items():
        if name is None:
            continue
        low, high = REGIONS[name]
        values = [luma(source[x, y]) for x, y in positions]
        median = sorted(values)[len(values) // 2]
        spread = max(values) - min(values) or 255
        for (x, y), value in zip(positions, values):
            target[x, y] = ramp(FLESH, (low + high) / 2 + (value - median) / spread * (high - low))
    return result


def box_faces(u, v, width, height, depth):
    return {
        "top": (u + depth, v, width, depth),
        "bottom": (u + depth + width, v, width, depth),
        "right": (u, v + depth, depth, height),
        "front": (u + depth, v + depth, width, height),
        "left": (u + depth + width, v + depth, depth, height),
        "back": (u + depth + width + depth, v + depth, width, height),
    }


def mirror_limb(image, source, target):
    faces = box_faces(*source, 4, 12, 4)
    targets = box_faces(*target, 4, 12, 4)
    swap = {"right": "left", "left": "right"}
    for name, (x, y, w, h) in faces.items():
        face = image.crop((x, y, x + w, y + h)).transpose(Image.Transpose.FLIP_LEFT_RIGHT)
        tx, ty, _, _ = targets[swap.get(name, name)]
        image.paste(face, (tx, ty))


def stamp(image, origin, mask):
    pixels = image.load()
    ox, oy = origin
    for row, line in enumerate(mask):
        for column, mark in enumerate(line):
            if mark in MARKS:
                pixels[ox + column, oy + row] = MARKS[mark] + (255,)


def wither(zombie):
    texture = flesh(zombie.convert("RGBA"))
    mirror_limb(texture, RIGHT_ARM, LEFT_ARM)
    mirror_limb(texture, RIGHT_LEG, LEFT_LEG)
    pixels = texture.load()
    fx, fy = FINGER
    for x in range(fx, fx + 4):
        for y in range(fy + 1, fy + 4):
            pixels[x, y] = ramp(FLESH, 0.45 - 0.1 * (y - fy - 1))
    pixels[fx + 1, fy] = ramp(FLESH, 0.5)
    for origin, mask in ROT:
        stamp(texture, origin, mask)
    return texture


def main(jar, assets):
    with zipfile.ZipFile(jar) as z:
        zombie = Image.open(io.BytesIO(z.read("assets/minecraft/textures/entity/zombie/zombie.png")))
        egg = Image.open(io.BytesIO(z.read("assets/minecraft/textures/item/zombie_spawn_egg.png"))).convert("RGBA")

    texture = wither(zombie)
    output = Path(assets) / "textures/entity/wither_zombie/wither_zombie.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    texture.save(output)
    print(output)

    egg_pixels = egg.load()
    for x in range(egg.width):
        for y in range(egg.height):
            colour = egg_pixels[x, y]
            if colour[3]:
                hue, saturation, value = colorsys.rgb_to_hsv(*(channel / 255 for channel in colour[:3]))
                ramp_t = luma(colour) / 255
                bone = hue >= 0.4 and saturation >= 0.2
                egg_pixels[x, y] = (BONE if bone and ramp_t > 0.3 else BONE_SHADOW if bone else ramp(FLESH, ramp_t * 1.2)[:3]) + (colour[3],)
    output = Path(assets) / "textures/item/wither_zombie_spawn_egg.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    egg.save(output)
    print(output)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise SystemExit("usage: render_wither_zombie.py <client.jar> <src/main/resources/assets/<modid>>")
    main(sys.argv[1], sys.argv[2])
