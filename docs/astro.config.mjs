// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";

const site = "https://a-file-icon-idea.netlify.app/";

// https://astro.build/config
export default defineConfig({
  site: "https://a-file-icon-idea.netlify.app/",
  integrations: [
    starlight({
      title: "Atom Material Icons",
      description: "Make your IDE more vibrant with beautiful Material Icons",
      logo: {
        light: "/src/assets/logo.svg",
        dark: "/src/assets/logo.svg",
      },
      social: [
        {
          icon: "github",
          label: "GitHub",
          href: "https://github.com/AtomMaterialUI",
        },
      ],
      head: [
        {
          tag: "meta",
          attrs: { property: "og:image", content: site + "og.jpg?v=1" },
        },
        {
          tag: "meta",
          attrs: { property: "twitter:image", content: site + "og.jpg?v=1" },
        },
        {
          tag: "link",
          attrs: { rel: "preconnect", href: "https://fonts.googleapis.com" },
        },
        {
          tag: "link",
          attrs: {
            rel: "preconnect",
            href: "https://fonts.gstatic.com",
            crossorigin: true,
          },
        },
        {
          tag: "link",
          attrs: {
            rel: "stylesheet",
            href: "https://fonts.googleapis.com/css2?family=Google+Sans:wght@500;600&display=swap",
          },
        },
        {
          tag: "script",
          attrs: {
            src: "https://cdn.jsdelivr.net/npm/@minimal-analytics/ga4/dist/index.js",
            async: true,
          },
        },
        {
          tag: "script",
          content: ` window.minimalAnalytics = {
            trackingId: 'G-GQ45JJD1JC',
            autoTrack: true,
          };`,
        },
      ],
      sidebar: [
        {
          label: "Getting Started",
          items: [{ label: "Installation", slug: "installation" }],
        },
        {
          label: "Usage",
          items: [
            { label: "Basic Usage", slug: "usage/basic" },
            { label: "Configuration", slug: "usage/configuration" },
          ],
        },
        {
          label: "Reference",
          items: [{ label: "Icon Gallery", link: "/icons" }],
        },
      ],
      plugins: [],
      customCss: ["./src/styles/custom.css"],
    }),
  ],
});