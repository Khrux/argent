import sys, zipfile, io, json, math
from pathlib import Path
from PIL import Image

SIZE = 128
SCALE = 4
BACKGROUND = (43, 43, 43, 255)


def load_texture(ref, jar, assets):
    namespace, path = ref.split(":")
    if namespace == "minecraft":
        with zipfile.ZipFile(jar) as z:
            return Image.open(io.BytesIO(z.read(f"assets/minecraft/textures/{path}.png"))).convert("RGBA")
    return Image.open(assets / "textures" / f"{path}.png").convert("RGBA")


def rotate(point, rotation):
    if rotation is None:
        return point
    angle = math.radians(rotation["angle"])
    ox, oy, oz = rotation["origin"]
    x, y, z = point
    dy, dz = y - oy, z - oz
    return (x, oy + dy * math.cos(angle) - dz * math.sin(angle), oz + dy * math.sin(angle) + dz * math.cos(angle))


def north_faces(model, textures):
    for element in model["elements"]:
        face = element["faces"].get("north")
        if face is None:
            continue
        (x0, y0, z0), (x1, y1, _) = element["from"], element["to"]
        rotation = element.get("rotation")
        corners = [rotate(point, rotation) for point in ((x1, y1, z0), (x0, y1, z0), (x1, y0, z0))]
        yield corners, face["uv"], textures[face["texture"].lstrip("#")]


def render(model, textures):
    faces = list(north_faces(model, textures))
    points = [corner for corners, _, _ in faces for corner in corners] + [
        (corners[1][0], corners[2][1], 0) for corners, _, _ in faces
    ]
    left = max(p[0] for p in points)
    right = min(p[0] for p in points)
    top = max(p[1] for p in points)
    bottom = min(p[1] for p in points)
    offset_x = (SIZE - (left - right) * SCALE) / 2
    offset_y = (SIZE - (top - bottom) * SCALE) / 2
    image = Image.new("RGBA", (SIZE, SIZE), BACKGROUND)
    depth = [[math.inf] * SIZE for _ in range(SIZE)]
    for (a, b, c), uv, texture in faces:
        ax, ay = (left - a[0]) * SCALE + offset_x, (top - a[1]) * SCALE + offset_y
        ux, uy = (left - b[0]) * SCALE + offset_x - ax, (top - b[1]) * SCALE + offset_y - ay
        vx, vy = (left - c[0]) * SCALE + offset_x - ax, (top - c[1]) * SCALE + offset_y - ay
        determinant = ux * vy - uy * vx
        for py in range(SIZE):
            for px in range(SIZE):
                dx, dy = px + 0.5 - ax, py + 0.5 - ay
                s = (dx * vy - dy * vx) / determinant
                t = (ux * dy - uy * dx) / determinant
                if not (0 <= s < 1 and 0 <= t < 1):
                    continue
                z = a[2] + s * (b[2] - a[2]) + t * (c[2] - a[2])
                if z >= depth[py][px]:
                    continue
                u = uv[0] + s * (uv[2] - uv[0])
                v = uv[1] + t * (uv[3] - uv[1])
                texel = texture.getpixel((min(int(u * texture.width / 16), texture.width - 1), min(int(v * texture.height / 16), texture.height - 1)))
                if texel[3] == 0:
                    continue
                depth[py][px] = z
                image.putpixel((px, py), texel)
    return image


def main(jar, assets_root, output):
    assets = Path(assets_root)
    model = json.loads((assets / "models" / "block" / "mirror.json").read_text())
    textures = {key: load_texture(ref, jar, assets) for key, ref in model["textures"].items() if key != "particle"}
    render(model, textures).save(output)
    print(output)


if __name__ == "__main__":
    if len(sys.argv) != 4:
        raise SystemExit("usage: render_icon.py <client.jar> <src/main/resources/assets/mirror> <icon.png>")
    main(*sys.argv[1:])
