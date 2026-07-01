import fs from "node:fs/promises";
import path from "node:path";
import { pathToFileURL } from "node:url";

const playwrightModuleUrl = pathToFileURL(
  "/Users/rohanc/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/playwright/index.mjs"
).href;
const { chromium } = await import(playwrightModuleUrl);

function extractMermaidBlocks(markdown) {
  const lines = markdown.split(/\r?\n/);
  const blocks = [];
  let inMermaid = false;
  let current = [];

  for (const line of lines) {
    if (!inMermaid && line.trim() === "```mermaid") {
      inMermaid = true;
      current = [];
      continue;
    }
    if (inMermaid && line.trim() === "```") {
      blocks.push(current.join("\n").trim());
      inMermaid = false;
      current = [];
      continue;
    }
    if (inMermaid) {
      current.push(line);
    }
  }
  return blocks;
}

async function ensureDir(dir) {
  await fs.mkdir(dir, { recursive: true });
}

async function renderDiagram(page, definition, outputPath) {
  const html = `
    <!doctype html>
    <html>
      <head>
        <meta charset="utf-8" />
        <style>
          html, body {
            margin: 0;
            padding: 0;
            background: white;
            font-family: Aptos, Arial, sans-serif;
          }
          #root {
            padding: 24px;
            display: inline-block;
            background: white;
          }
        </style>
      </head>
      <body>
        <div id="root"><div id="diagram"></div></div>
        <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
      </body>
    </html>
  `;

  await page.setContent(html, { waitUntil: "load" });
  await page.evaluate(async (diagramDefinition) => {
    mermaid.initialize({
      startOnLoad: false,
      securityLevel: "loose",
      theme: "default",
      flowchart: { useMaxWidth: false, htmlLabels: true },
      sequence: { useMaxWidth: false }
    });
    const { svg } = await mermaid.render(`manual-diagram-${Math.random().toString(36).slice(2)}`, diagramDefinition);
    const target = document.getElementById("diagram");
    target.innerHTML = svg;
  }, definition);

  const root = page.locator("#root");
  await root.screenshot({ path: outputPath });
}

async function main() {
  const markdownPath = path.resolve(process.argv[2]);
  const outputDir = path.resolve(process.argv[3]);
  const markdown = await fs.readFile(markdownPath, "utf8");
  const blocks = extractMermaidBlocks(markdown);

  await ensureDir(outputDir);

  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ deviceScaleFactor: 2 });

  for (let index = 0; index < blocks.length; index += 1) {
    const fileName = `manual-diagram-${String(index + 1).padStart(2, "0")}.png`;
    const outputPath = path.join(outputDir, fileName);
    await renderDiagram(page, blocks[index], outputPath);
    console.log(outputPath);
  }

  await browser.close();
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
