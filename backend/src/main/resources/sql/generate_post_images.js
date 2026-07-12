/**
 * 生成打卡占位图脚本
 * 在终端运行：node generate_post_images.js
 * 会在 backend/uploads/images/posts/ 目录生成 6 张彩色占位图
 */
const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

const outputDir = path.join(__dirname, '..', 'backend', 'uploads', 'images', 'posts');

// 6 种颜色（江右拾遗品牌色 + 补充色）
const colors = [
  { name: '1', r: 139, g: 26, b: 26 },   // 朱砂红
  { name: '2', r: 196, g: 30, b: 58 },   // 辅红
  { name: '3', r: 212, g: 168, b: 83 },  // 金色
  { name: '4', r: 46, g: 125, b: 50 },   // 翠微绿
  { name: '5', r: 230, g: 126, b: 34 },  // 橙色
  { name: '6', r: 52, g: 152, b: 219 },  // 蓝色
];

function createPNG(width, height, r, g, b) {
  // PNG Signature
  const signature = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);

  // IHDR chunk
  const ihdrData = Buffer.alloc(13);
  ihdrData.writeUInt32BE(width, 0);
  ihdrData.writeUInt32BE(height, 4);
  ihdrData[8] = 8;  // bit depth
  ihdrData[9] = 2;  // color type: RGB
  ihdrData[10] = 0; // compression
  ihdrData[11] = 0; // filter
  ihdrData[12] = 0; // interlace
  const ihdr = createChunk('IHDR', ihdrData);

  // Raw pixel data (RGB, filter byte 0 for each row)
  const rawData = Buffer.alloc(height * (1 + width * 3));
  for (let y = 0; y < height; y++) {
    const offset = y * (1 + width * 3);
    rawData[offset] = 0; // filter byte: None
    for (let x = 0; x < width; x++) {
      const pixelOffset = offset + 1 + x * 3;
      rawData[pixelOffset] = r;
      rawData[pixelOffset + 1] = g;
      rawData[pixelOffset + 2] = b;
    }
  }

  // IDAT chunk (compressed pixel data)
  const compressed = zlib.deflateSync(rawData);
  const idat = createChunk('IDAT', compressed);

  // IEND chunk
  const iend = createChunk('IEND', Buffer.alloc(0));

  return Buffer.concat([signature, ihdr, idat, iend]);
}

function createChunk(type, data) {
  const length = Buffer.alloc(4);
  length.writeUInt32BE(data.length, 0);
  const typeBuffer = Buffer.from(type, 'ascii');
  const crcData = Buffer.concat([typeBuffer, data]);
  const crc = crc32(crcData);
  const crcBuffer = Buffer.alloc(4);
  crcBuffer.writeUInt32BE(crc, 0);
  return Buffer.concat([length, typeBuffer, data, crcBuffer]);
}

function crc32(buf) {
  let crc = 0xFFFFFFFF;
  for (let i = 0; i < buf.length; i++) {
    crc ^= buf[i];
    for (let j = 0; j < 8; j++) {
      if (crc & 1) crc = (crc >>> 1) ^ 0xEDB88320;
      else crc >>>= 1;
    }
  }
  return (crc ^ 0xFFFFFFFF) >>> 0;
}

// 确保目录存在
if (!fs.existsSync(outputDir)) {
  fs.mkdirSync(outputDir, { recursive: true });
}

// 生成6张占位图
colors.forEach(c => {
  const pngData = createPNG(200, 200, c.r, c.g, c.b);
  const filename = `post_${c.name}.png`;
  const filepath = path.join(outputDir, filename);
  fs.writeFileSync(filepath, pngData);
  console.log(`✅ 已生成: ${filepath}`);
});

console.log(`\n🎉 共生成 ${colors.length} 张打卡占位图`);
console.log(`📁 目录: ${outputDir}`);
