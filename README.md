# CSS Vars Linker

A JetBrains IntelliJ plugin that exposes CSS custom properties (`--tokens`) defined in external files, such as design systems or packages in `node_modules`.

✅ Perfect for Nuxt, Vue, or any modern frontend project using native CSS design tokens.

---

## ✨ Features

- ✅ Recognizes custom CSS variables defined in external files (e.g. `node_modules`)
- ✅ Enables autocompletion and validation in `.css`, `.scss`, `.vue`, and similar files
- ✅ Supports scoped or unscoped token systems
- ✅ No runtime impact – purely for editor experience

---

## 📦 Installation

You can either:

1. **Install from JetBrains Marketplace** (when available)
2. Or **run locally** for testing and development:

```bash
./gradlew runIde
```

---

## 🧰 Usage

Create a `.cssvarsconfig` file at the root of your project (next to your `package.json`) with a list of external CSS files to scan:

```
node_modules/@nextlify/ui/dist/style.css
node_modules/@acme/tokens/dist/theme.css
```

These files will be parsed for variables like `--primary-color`, `--space-xl`, etc.

You can now use them like:

```css
padding: var(--space-xl);
```

And your editor will no longer show any false errors like:

> `Cannot resolve '--space-xl' custom property`

---

## 🛠 Development

To build and test the plugin locally:

```bash
./gradlew buildPlugin      # creates a ZIP file in build/distributions/
./gradlew runIde           # launches IntelliJ sandbox for local testing
```

You’ll find your plugin build under:
```
build/distributions/css-vars-linker-1.0.0.zip
```

---

## 🔐 Optional: Signing and Publishing

To publish on the JetBrains Marketplace:

- Generate a private key and certificate chain from [JetBrains Plugin Signing](https://plugins.jetbrains.com/docs/marketplace/plugin-signing.html)
- Set these as environment variables:

```
CERT_CHAIN=...
PRIVATE_KEY=...
PRIVATE_KEY_PASSWORD=...
JETBRAINS_TOKEN=...
```

Then run:

```bash
./gradlew signPlugin publishPlugin
```

---

## 📄 License

MIT — feel free to use, fork, or contribute!

---

## 🙌 Credits

Created by [Eddy Dezuraud](https://github.com/EddyDezuraud).