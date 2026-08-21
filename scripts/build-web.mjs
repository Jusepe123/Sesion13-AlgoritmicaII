import { cp, mkdir, rm, stat } from 'node:fs/promises';
import { resolve } from 'node:path';

const source = resolve('web');
const output = resolve('dist');
const requiredFiles = ['index.html', 'styles.css', 'app.js'];

for (const file of requiredFiles) {
  const info = await stat(resolve(source, file));
  if (!info.isFile()) throw new Error(`Missing required web artifact: ${file}`);
}

await rm(output, { recursive: true, force: true });
await mkdir(output, { recursive: true });
await cp(source, output, { recursive: true });

console.log(`Built ${requiredFiles.length} required assets in dist/`);
