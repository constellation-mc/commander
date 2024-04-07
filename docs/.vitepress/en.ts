import { defineConfig } from 'vitepress'

export const en = defineConfig({
    lang: "en-US",
    description: "An extension of the data pack system.",

    themeConfig: {
        nav: [
            { text: 'Home', link: '/' }
        ],

        sidebar: [
            {
                text: 'Examples',
                items: [
                    { text: 'Welcome!', link: '/' },
                    { text: 'Events', link: '/Events' },
                    { text: 'Commands', link: '/Commands' },
                    { text: 'Arithmetica', link: '/Arithmetica' }
                ]
            }
        ]
    }
})