// noinspection JSUnusedGlobalSymbols
import {resolve} from 'path';
import {defineConfig} from 'vite';
import dts from 'vite-plugin-dts';

// https://vitejs.dev/guide/build.html#library-mode
export default defineConfig({
    build: {
        lib: {
            entry: resolve(__dirname, 'src/index.ts'),
            name: 'sovity-edc-client',
            fileName: 'sovity-edc-client',
        },
        sourcemap: true,
    },
    plugins: [dts()],
});
