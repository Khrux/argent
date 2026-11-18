import io, sys, zipfile
from pathlib import Path
from PIL import Image

SIZE = 128
BACKGROUND = (43, 43, 43, 255)
LEATHER = (0xA0, 0x65, 0x40)
SPRITE_SCALE = 6
FACE_SCALE = 10


def sprite(path):
    return Image.open(path).convert("RGBA")


def tinted(image, colour):
    pixels = image.load()
    for x in range(image.width):
        for y in range(image.height):
            r, g, b, a = pixels[x, y]
            pixels[x, y] = (r * colour[0] // 255, g * colour[1] // 255, b * colour[2] // 255, a)
    return image


def face(texture):
    image = sprite(texture)
    head = image.crop((8, 8, 16, 16))
    hat = image.crop((40, 8, 48, 16))
    head.alpha_composite(hat)
    return head


def icon(image, scale):
    canvas = Image.new("RGBA", (SIZE, SIZE), BACKGROUND)
    big = image.resize((image.width * scale, image.height * scale), Image.NEAREST)
    canvas.alpha_composite(big, ((SIZE - big.width) // 2, (SIZE - big.height) // 2))
    return canvas


def main(jar, root):
    root = Path(root)
    with zipfile.ZipFile(jar) as z:
        lantern = Image.open(io.BytesIO(z.read("assets/minecraft/textures/item/soul_lantern.png"))).convert("RGBA")
    icons = {
        "just-silver/src/main/resources/assets/justsilver/icon.png": icon(sprite(root / "just-silver/src/main/resources/assets/justsilver/textures/item/silver_ingot.png"), SPRITE_SCALE),
        "just-spawn-lanterns/src/main/resources/assets/justspawnlanterns/icon.png": icon(lantern, SPRITE_SCALE),
        "just-scrying/src/main/resources/assets/justscrying/icon.png": icon(tinted(sprite(root / "just-scrying/src/main/resources/assets/justscrying/textures/item/parrot_armor.png"), LEATHER), SPRITE_SCALE),
        "just-wither-zombies/src/main/resources/assets/justwitherzombies/icon.png": icon(face(root / "just-wither-zombies/src/main/resources/assets/justwitherzombies/textures/entity/wither_zombie/wither_zombie.png"), FACE_SCALE),
    }
    for path, image in icons.items():
        output = root / path
        output.parent.mkdir(parents=True, exist_ok=True)
        image.save(output)
        print(output)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
