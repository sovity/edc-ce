module.exports = {
    tabWidth: 4,
    useTabs: false,
    singleQuote: true,
    semi: true,
    arrowParens: 'always',
    trailingComma: 'all',
    bracketSameLine: true,
    printWidth: 80,
    bracketSpacing: false,
    proseWrap: 'always',

    // @trivago/prettier-plugin-sort-imports
    importOrder: [
        '<THIRD_PARTY_MODULES>',
        // rest after
        '^[./]',
    ],
    importOrderParserPlugins: [
        'typescript',
        'classProperties',
        'decorators-legacy',
    ],
    importOrderSeparation: false,
    importOrderSortSpecifiers: true,
};
