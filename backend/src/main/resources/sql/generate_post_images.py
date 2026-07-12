"""
生成打卡占位图脚本
使用方法：在终端运行 python generate_post_images.py
会在 backend/uploads/images/posts/ 目录生成 6 张占位图
"""
import os
import struct
import zlib

output_dir = os.path.join(os.path.dirname(__file__), '..', '..', '..', 'uploads', 'images', 'posts')

# 6 种颜色
colors = [
    (139, 26, 26),   # 朱砂红
    (196, 30, 58),   # 辅红
    (212, 168, 83),  # 金色
    (46, 125, 50),   # 翠微绿
    (230, 126, 34),  # 橙色
    (52, 152, 219),  # 蓝色
]

def create_png(width, height, r, g, b):
    """创建纯色PNG图片"""
    # PNG signature
    signature = b'\x89PNG\r\n\x1a\n'

    # IHDR
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0)
    ihdr = make_chunk(b'IHDR', ihdr_data)

    # IDAT (pixel data)
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'  # filter byte
        for x in range(width):
            raw_data += bytes([r, g, b])

    compressed = zlib.compress(raw_data)
    idat = make_chunk(b'IDAT', compressed)

    # IEND
    iend = make_chunk(b'IEND', b'')

    return signature + ihdr + idat + iend

def make_chunk(chunk_type, data):
    """创建PNG chunk"""
    chunk = struct.pack('>I', len(data)) + chunk_type + data
    crc_value = zlib.crc32(chunk_type + data) & 0xffffffff
    chunk += struct.pack('>I', crc_value)
    return chunk

# 确保目录存在
os.makedirs(output_dir, exist_ok=True)

# 生成6张占位图
for i, (r, g, b) in enumerate(colors, 1):
    png_data = create_png(200, 200, r, g, b)
    filename = f'post_{i}.png'
    filepath = os.path.join(output_dir, filename)
    with open(filepath, 'wb') as f:
        f.write(png_data)
    print(f'✅ 已生成: {filepath}')

print(f'\n🎉 共生成 {len(colors)} 张打卡占位图')
print(f'📁 目录: {output_dir}')
