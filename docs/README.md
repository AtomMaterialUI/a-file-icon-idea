# Documentation

This is the documentation project for the **Atom Material File Icons** plugin, built using [Astro](https://astro.build)
and [Starlight](https://starlight.astro.build).

## 🚀 Getting Started

To run the documentation locally, follow these steps:

1. **Install dependencies**:
   ```bash
   pnpm install
   ```

2. **Generate/Copy Icons**:
   Before running or building the docs, you **must** run the prebuild script to copy the icons from the `iconGenerator` module:
   ```bash
   pnpm run prebuild
   ```
   This script ensures that the latest icons from `../iconGenerator/assets/icons` are available in `public/icons`.

3. **Start the development server**:
   ```bash
   pnpm run dev
   ```
   The documentation will be available at `http://localhost:4321`.

## 📦 Dependencies

The project relies on the following key dependencies:

- **Astro**: The web framework for building fast content-driven websites.
- **Starlight**: A documentation theme for Astro.
- **Sharp**: Used for high-performance image processing.
- **Glob**: Used for file matching patterns.

## 🧞 Commands

All commands are run from the `docs` directory:

| Command             | Action                                           |
|:--------------------|:-------------------------------------------------|
| `npm install`       | Installs dependencies                            |
| `npm run prebuild`  | **Required**: Copies icons from `iconGenerator`  |
| `npm run dev`       | Starts local dev server at `localhost:4321`      |
| `npm run build`     | Build your production site to `./dist/`          |
| `npm run preview`   | Preview your build locally, before deploying     |
| `npm run astro ...` | Run CLI commands like `astro add`, `astro check` |

## 📁 Project Structure

Inside the documentation project, you'll see the following folders and files:

- `public/icons/`: Where icons are copied to (after running `npm run prebuild`).
- `src/content/docs/`: Where the documentation content (`.md` or `.mdx`) resides.
- `astro.config.mjs`: Astro configuration.
- `package.json`: Project dependencies and scripts.

## Deployment

The website is found at https://a-file-icon-idea.netlify.app/ and is automatically deployed on push to the `main`
branch. The build command is `npm run build` and the publish directory is `./dist/`.
