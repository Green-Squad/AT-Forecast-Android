const fs = require('fs');
const path = require('path');

// Ensure assets directory exists
const assetsDir = path.join(__dirname, 'src', 'assets');
if (!fs.existsSync(assetsDir)) {
  fs.mkdirSync(assetsDir, { recursive: true });
}

// Create a basic placeholder PNG file (1x1 transparent pixel)
const transparentPixelBuffer = Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=', 'base64');

// List of image files to create
const imageFiles = [
  'icon.png',
  'splash.png',
  'adaptive-icon.png',
  'favicon.png',
  'placeholder.png'
];

// Create each placeholder image
imageFiles.forEach(filename => {
  try {
    const filePath = path.join(assetsDir, filename);
    fs.writeFileSync(filePath, transparentPixelBuffer);
    console.log(`Created placeholder image: ${filename}`);
  } catch (error) {
    console.error(`Error creating ${filename}: ${error.message}`);
  }
});

console.log('All placeholder images created successfully.'); 